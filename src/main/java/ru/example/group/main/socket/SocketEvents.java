package ru.example.group.main.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.socket.*;
import ru.example.group.main.entity.*;
import ru.example.group.main.entity.enumerated.ReadStatusType;
import ru.example.group.main.mapper.MessageMapper;
import ru.example.group.main.mapper.NotificationMapper;
import ru.example.group.main.mapper.UserMapper;
import ru.example.group.main.repository.DialogRepository;
import ru.example.group.main.repository.MessageRepository;
import ru.example.group.main.repository.SessionsRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetailsService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketEvents {

    private static final String USER_OFFLINE = "Отправка ивента не требуется, юзер офлайн";
    private final SocketIOServer socketIOServer;
    private final JWTUtilService jwtUtilService;
    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final SessionsRepository sessionsRepository;
    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @OnConnect
    public void connect(SocketIOClient client) {
        log.info("Клиент {} подключился", client.getSessionId());
    }

    @OnEvent("auth")
    public void authentication(SocketIOClient client, AuthSocketRequestDto authRequest) {
        if (!authRequest.getToken().isEmpty()) {
            var username = jwtUtilService.extractUsername(authRequest.getToken());
            var userDetails = socialNetUserDetailsService.loadUserByUsername(username);
            var user = socialNetUserDetailsService.loadUserEntityByUsername(userDetails.getUsername());
            setOnlineTime(user);

            var userId = user.getId();
            var session = client.getSessionId();

            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setUserId(userId);
            sessionEntity.setSession(session);
            sessionsRepository.save(sessionEntity);

            client.sendEvent("auth-response", sessionsRepository.existsById(session) ? "ok" : "not");
        }
    }

    @OnDisconnect
    public void disconnect(SocketIOClient client) {
        var session = client.getSessionId();
        var sessionEntity = sessionsRepository.findById(session).orElseThrow(EntityNotFoundException::new);
        var user = userRepository.findById(sessionEntity.getUserId()).orElseThrow(EntityNotFoundException::new);
        setOnlineTime(user);

        sessionsRepository.delete(sessionEntity);
        log.info("Клиент {} отключился", client.getSessionId());
    }

    private void setOnlineTime(UserEntity user) {
        user.setLastOnlineTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @OnEvent("newListener")
    public void newListener(SocketIOClient client) {
        var session = client.getSessionId();
        var isExist = sessionsRepository.findById(session).isEmpty();
        client.sendEvent("auth-response", isExist ? "ok" : "not");
    }

    @OnEvent("start-typing")
    public void startTyping(SocketIOClient client, TypingSocketRequestDto typing) {
        var sessionEntity = getSessionEntityFromTypingRequest(typing);

        if (sessionEntity == null) {
            log.info(USER_OFFLINE);
        } else {
            var session = sessionEntity.getSession();
            var clientRecipient = socketIOServer.getClient(session);

            if(clientRecipient != null) clientRecipient.sendEvent("start-typing-response", startTyping(typing));
        }

        client.sendEvent("start-typing-response", startTyping(typing));
    }

    private TypingSocketResponseDto startTyping(TypingSocketRequestDto typingRequest) {
        return TypingSocketResponseDto.builder()
                .authorId(typingRequest.getAuthor())
                .author(userRepository.findById(typingRequest.getAuthor()).orElseThrow(EntityNotFoundException::new).getFirstName())
                .dialog(typingRequest.getDialog())
                .build();
    }

    @OnEvent("stop-typing")
    public void stopTyping(SocketIOClient client, TypingSocketRequestDto typing) {
        var sessionEntity = getSessionEntityFromTypingRequest(typing);

        if (sessionEntity == null) {
            log.info(USER_OFFLINE);
        } else {
            var session = sessionEntity.getSession();
            var clientRecipient = socketIOServer.getClient(session);

            if(clientRecipient != null) clientRecipient.sendEvent("stop-typing-response", stopTyping(typing));
        }

        client.sendEvent("stop-typing-response", stopTyping(typing));
    }

    private TypingSocketResponseDto stopTyping(TypingSocketRequestDto typingRequest) {
        return TypingSocketResponseDto.builder()
                .authorId(typingRequest.getAuthor())
                .author(userRepository.findById(typingRequest.getAuthor()).orElseThrow(EntityNotFoundException::new).getFirstName())
                .dialog(typingRequest.getDialog())
                .build();
    }

    private SessionEntity getSessionEntityFromTypingRequest(TypingSocketRequestDto typing) {
        var dialog = dialogRepository.findById(typing.getDialog()).orElseThrow(EntityNotFoundException::new);
        var recipient = dialog.getSender().getId().equals(typing.getAuthor()) ? dialog.getRecipient() : dialog.getSender();
        return sessionsRepository.findSessionEntityByUserId(recipient.getId());
    }

    @OnEvent("read-messages")
    public void readMessages(SocketIOClient client, ReadMessagesSocketDto readMessages) {
        var dialog = dialogRepository.findById(readMessages.getDialog()).orElseThrow(EntityNotFoundException::new);
        var session = sessionsRepository.findById(client.getSessionId()).orElseThrow(EntityNotFoundException::new);
        var user = userRepository.findById(session.getUserId()).orElseThrow(EntityNotFoundException::new);

        var unreadMessages = messageRepository.findAllUnreadMessagesByDialogIdAndUserId(dialog.getId(), user.getId());
        unreadMessages.forEach(message -> message.setReadStatus(ReadStatusType.READ));
        messageRepository.saveAll(unreadMessages);

        var unreadCount = messageRepository.countUnreadMessagesInDialogsByCurrentUser(user);

        client.sendEvent("unread-response", String.valueOf(unreadCount));
    }

    public void sentMessage(MessageEntity messageEntity, UserEntity recipient) {
        var sessionEntity = sessionsRepository.findSessionEntityByUserId(recipient.getId());
        if (sessionEntity == null) {
            log.info(USER_OFFLINE);
        } else {
            var session = sessionEntity.getSession();
            var client = socketIOServer.getClient(session);

            if(client != null) client.sendEvent("message", CommonResponseDto.<MessageSocketDto>builder()
                    .data(messageMapper.messageEntityToSocketDto(messageEntity))
                    .error("")
                    .timeStamp(LocalDateTime.now())
                    .build());
        }
    }

    public void commentNotification(NotificationEntity notification, CommentEntity comment, UserEntity user) {
        var sessionEntity = sessionsRepository.findSessionEntityByUserId(user.getId());
        if (sessionEntity == null) {
            log.info(USER_OFFLINE);
        } else {
            var author = userMapper.userEntityToSocketDto(user);
            var responseDto = notificationMapper.commentNotificationEntityToSocketDto(notification, comment, author);

            var session = sessionEntity.getSession();
            var client = socketIOServer.getClient(session);
            client.sendEvent("comment-notification-response", responseDto);
        }
    }

    public void friendNotification(NotificationEntity notification, UserEntity user) {
        var sessionEntity = sessionsRepository.findSessionEntityByUserId(user.getId());
        if (sessionEntity == null) {
            log.info(USER_OFFLINE);
        } else {
            var author = userMapper.userEntityToSocketDto(user);
            var responseDto = notificationMapper.friendRequestNotificationToSocketDto(notification, author);

            var session = sessionEntity.getSession();
            var client = socketIOServer.getClient(session);
            client.sendEvent("friend-notification-response", responseDto);
        }
    }
}
