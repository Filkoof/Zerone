package ru.example.group.main.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.entity.enumerated.PostType;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ResponseEntity<CommonResponseDto<PostResponseDto>> addNewPost(
        final PostRequestDto request,
        final long id,
        final long publishDate
    ) {
        var response = new CommonResponseDto<PostResponseDto>();
        var userEntity = userRepository.findById(id).orElseThrow();
        var requestedDateTime = LocalDateTime.ofEpochSecond(publishDate, 0, ZoneOffset.UTC);
        var dateTimeNow = LocalDateTime.now();
        var publishDateTime = requestedDateTime.isBefore(dateTimeNow) ? dateTimeNow : requestedDateTime;
        var isQueued = publishDateTime.isAfter(dateTimeNow);
        var userDto = getUserDtoFromEntity(userEntity);
        var postEntity = new PostEntity();
        postEntity.setTime(publishDateTime);
        postEntity.setTitle(request.getTitle());
        postEntity.setPostText(request.getText());
        postEntity.setUpdateDate(dateTimeNow);
        postEntity.setUser(userEntity);

        var post = postRepository.save(postEntity);

        response.setData(getFromAddRequest(publishDateTime, isQueued, userDto, post));
        return ResponseEntity.ok(response);
    }

    private PostResponseDto getFromAddRequest(
        final LocalDateTime publishDateTime,
        final boolean isQueued,
        final UserDataResponseDto userDto,
        final PostEntity post
    ) {
        return PostResponseDto.builder()
            .isBlocked(false)
            .myLike(false)
            .author(userDto)
            .id(post.getId())
            .time(publishDateTime)
            .title(post.getTitle())
            .type(isQueued ? PostType.QUEUED.name() : PostType.POSTED.name())
            .postText(post.getPostText())
            .likes(0)
            .tags(post.getTagEntities().stream()
                .map(TagEntity::getTag)
                .collect(Collectors.toList()))
            .build();
    }

    private UserDataResponseDto getUserDtoFromEntity(final UserEntity userEntity) {
        return UserDataResponseDto.builder()
            .id(userEntity.getId())
            .firstName(userEntity.getFirstName())
            .lastName(userEntity.getLastName())
            .regDate(userEntity.getRegDate())
            .birthDate(userEntity.getBirthDate())
            .eMail(userEntity.getEmail())
            .phone(userEntity.getPhone())
            .photo(userEntity.getPhoto())
            .about(userEntity.getAbout())
            .city(userEntity.getCity())
            .country(userEntity.getCountry())
            .messagePermissions(MessagesPermission.getFromBoolean(userEntity.isMessagePermissions()))
            .lastOnlineTime(userEntity.getLastOnlineTime())
            .isBlocked(userEntity.isBlocked())
            .isDeleted(userEntity.isDeleted())
            .build();
    }
}
