package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.SupportRequestDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.entity.SupportRequestEntity;
import ru.example.group.main.entity.enumerated.SupportRequestStatus;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.SupportRequestException;
import ru.example.group.main.mapper.SupportRequestMapper;
import ru.example.group.main.repository.SupportRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final SupportRequestMapper supportRequestMapper;
    private final ZeroneMailSenderService zeroneMailSenderService;

    public ResponseEntity<ResultMessageDto> receiveSupportRequest(SupportRequestDto supportRequestDto) throws SupportRequestException, EmailNotSentException {
        ResultMessageDto resultMessageDto = new ResultMessageDto();
        try {
            SupportRequestEntity newSupportRequest = supportRequestMapper.dtoToEntity(supportRequestDto,
                    SupportRequestStatus.getStringFromSupportRequestStatus(SupportRequestStatus.NEW),
                    LocalDateTime.now().toString());
            supportRequestRepository.save(newSupportRequest);
        }catch (Exception e){
            emailConfirmation(false, supportRequestDto.getEmail());
            throw new SupportRequestException("Ошибка обработки запроса, попробуйте позднее.");
        }
        emailConfirmation(true, supportRequestDto.getEmail());
        return new ResponseEntity<>(resultMessageDto, HttpStatus.OK);
    }

    private void emailConfirmation(Boolean successTrue, String email) throws EmailNotSentException {
        if (successTrue){
            zeroneMailSenderService.emailSend(email,
                    "Ваше обращение в поддержку ZERONE зарегистрировано.",
                    """
                            Уважаемый пользователь, здравствуйте.
                            Ваш запрос был принят, ожидайте ответа.
                            Спасибо.
                            """);
            return;
        }

        zeroneMailSenderService.emailSend(email,
                "Ошибка при обращении в службу поддержки.",
                """
                        Уважаемый пользователь, здравствуйте.
                        Ваш запрос не был принят по техническим причинам. Попробуйте позднее.
                        Приносим извинения за технические проблемы.
                        """);
    }
}
