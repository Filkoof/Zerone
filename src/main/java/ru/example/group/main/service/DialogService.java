package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.DialogRequestDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.DialogResponseDto;
import ru.example.group.main.entity.DialogEntity;
import ru.example.group.main.entity.MessageEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.mapper.DialogMapper;
import ru.example.group.main.mapper.MessageMapper;
import ru.example.group.main.repository.DialogRepository;
import ru.example.group.main.repository.MessageRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.util.UtilZerone;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DialogService {

    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final DialogMapper dialogMapper;
    private final MessageMapper messageMapper;

    public CommonResponseDto<DialogResponseDto> postDialog(DialogRequestDto request) {
        var sender = socialNetUserRegisterService.getCurrentUser();
        var recipient = userRepository.findById(request.getUsersIds().get(0)).orElseThrow(EntityNotFoundException::new);

        return CommonResponseDto.<DialogResponseDto>builder()
                .data(getPostDialogDto(sender, recipient))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    private DialogResponseDto getPostDialogDto(UserEntity sender, UserEntity recipient) {
        return isDialogExist(sender, recipient) ?
                getDialogDto(dialogRepository.findDialogByUsersIds(sender, recipient), sender)
                :
                getDialogDto(dialogRepository.save(dialogMapper.dialogRequestToEntity(sender, recipient)), sender);
    }

    private boolean isDialogExist(UserEntity sender, UserEntity recipient) {
        return dialogRepository.existsBySenderAndRecipient(sender, recipient) || dialogRepository.existsBySenderAndRecipient(recipient, sender);
    }

    public CommonListResponseDto<DialogResponseDto> getDialogs(Integer offset, Integer itemPerPage) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var dialogs = dialogRepository.findAllDialogsByCurrentUserWithPagination(currentUser, UtilZerone.getPagination(itemPerPage, offset));

        return CommonListResponseDto.<DialogResponseDto>builder()
                .total((int) dialogs.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(dialogs.stream().map(dialog -> getDialogDto(dialog, currentUser)).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private DialogResponseDto getDialogDto(DialogEntity dialog, UserEntity currentUser) {
        var lastMessage = messageRepository.existsByDialogId(dialog.getId()) ?
                messageRepository.findLastMessage(dialog.getId()) : getStartMessage(dialog, currentUser);

        return dialogMapper.dialogEntityToDto(
                dialog,
                dialog.getRecipient().getId().equals(currentUser.getId()) ? dialog.getSender() : dialog.getRecipient(),
                messageRepository.countUnreadMessagesInDialog(dialog, currentUser),
                messageMapper.messageEntityToDto(lastMessage, currentUser));
    }

    private MessageEntity getStartMessage(DialogEntity dialog, UserEntity sender) {
        return messageRepository.save(messageMapper.dialogAndUserToStartMessage(dialog, sender));
    }
}
