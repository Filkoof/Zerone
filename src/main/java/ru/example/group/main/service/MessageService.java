package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.MessageRequestDto;
import ru.example.group.main.dto.response.*;
import ru.example.group.main.mapper.MessageMapper;
import ru.example.group.main.repository.DialogRepository;
import ru.example.group.main.repository.MessageRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.socketIO.SocketEvents;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogRepository dialogRepository;
    private final MessageMapper messageMapper;
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final SocketEvents socketEvents;

    public CommonResponseDto<MessageDto> postMessage(Long dialogId, MessageRequestDto request) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var dialog = dialogRepository.findById(dialogId).orElseThrow(EntityNotFoundException::new);
        var messageEntity = messageMapper.messageRequestToEntity(request, dialog, currentUser);
        messageRepository.save(messageEntity);

        socketEvents.sentMessage(messageEntity, dialog.getRecipient());

        return CommonResponseDto.<MessageDto>builder()
                .data(messageMapper.messageEntityToDto(messageEntity, currentUser))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public CommonListResponseDto<MessageDto> getMessages(Long id, int offset, int itemPerPage ) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var statePage = messageRepository.findAllMessagesByDialogIdWithPagination(id, pageable);

        return CommonListResponseDto.<MessageDto>builder()
                .total((int) statePage.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(statePage.stream().map(entity -> messageMapper.messageEntityToDto(entity, currentUser)).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CommonResponseDto<Map<String, String>> getUnreadMessage() {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var unreadMessages = messageRepository.countUnreadMessagesInDialogsByCurrentUser(currentUser);

        return CommonResponseDto.<Map<String, String>>builder()
                .data(Map.of("count", String.valueOf(unreadMessages)))
                .timeStamp(LocalDateTime.now())
                .error("")
                .build();
    }
}
