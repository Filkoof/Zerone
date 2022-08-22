package ru.example.group.main.service;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.UserDataResponseDto;
=======
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.UserDataResponseDto;
import ru.example.group.main.dto.request.PostRequestDto;
>>>>>>> dev
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.entity.enumerated.PostType;
import ru.example.group.main.repository.PostRepository;
<<<<<<< HEAD
import ru.example.group.main.response.CommonListResponseDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
=======
import ru.example.group.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
>>>>>>> dev
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

<<<<<<< HEAD
    private PostRepository postRepository;

    public CommonListResponseDto<PostResponseDto> getNewsfeed(String text, int offset, int itemPerPage) {
        List<PostResponseDto> posts = postRepository.findAll(Pageable.ofSize(itemPerPage))
                .stream().map(this::getPostDtoFromEntity).toList();
        return CommonListResponseDto.<PostResponseDto>builder()
                .total(posts.size())
                .perPage(itemPerPage)
                .offset(offset)
                .data(posts)
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private PostResponseDto getPostDtoFromEntity(PostEntity postEntity) {
        return PostResponseDto.builder()
                .isBlocked(postEntity.isBlocked())
                .comments(Collections.singletonList(postEntity.getComments()))
                .myLike(false)
                .author(getUserDtoFromEntity(postEntity.getUser()))
                .id(postEntity.getId())
                .time(postEntity.getTime())
                .title(postEntity.getTitle())
                .type(PostType.POSTED.name())
                .postText(postEntity.getPostText())
                .likes(0)
                .tags(postEntity.getTagEntities().stream()
                        .map(TagEntity::getTag)
                        .collect(Collectors.toList()))
                .build();
    }

    private UserDataResponseDto getUserDtoFromEntity(UserEntity userEntity) {
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

=======
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
>>>>>>> dev
