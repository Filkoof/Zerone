package ru.example.group.main.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.UserDataResponseDto;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.entity.enumerated.PostType;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.response.CommonListResponseDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
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

        response.setData(getFromAddRequest(isQueued, userDto, post));
        return ResponseEntity.ok(response);
    }

    public CommonListResponseDto<PostResponseDto> getNewsfeed(String text, int offset, int itemPerPage) {
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var statePage = postRepository.findAllPostsWithPagination(text, pageable)
                .stream().map(this::getPostDtoFromEntity).toList();

        return CommonListResponseDto.<PostResponseDto>builder()
                .total(statePage.size())
                .perPage(itemPerPage)
                .offset(offset)
                .data(statePage)
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private PostResponseDto.PostResponseDtoBuilder getDefaultBuilder(PostEntity postEntity) {
        return PostResponseDto.builder()
                .isBlocked(false)
                .myLike(false)
                .id(postEntity.getId())
                .time(postEntity.getTime())
                .title(postEntity.getTitle())
                .postText(postEntity.getPostText())
                .likes(0)
                .tags(postEntity.getTagEntities().stream().map(TagEntity::getTag).collect(Collectors.toList()));
    }

    private PostResponseDto getPostDtoFromEntity(PostEntity postEntity) {
        return getDefaultBuilder(postEntity)
                .comments(Collections.singletonList(postEntity.getComments()))
                .author(getUserDtoFromEntity(postEntity.getUser()))
                .type(PostType.POSTED.name())
                .build();
    }

    private PostResponseDto getFromAddRequest(
        final boolean isQueued,
        final UserDataResponseDto userDto,
        final PostEntity post
    ) {
        return getDefaultBuilder(post)
            .author(userDto)
            .type(isQueued ? PostType.QUEUED.name() : PostType.POSTED.name())
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