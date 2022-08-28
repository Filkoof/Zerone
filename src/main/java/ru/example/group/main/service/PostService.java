package ru.example.group.main.service;

import java.util.ArrayList;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.entity.enumerated.PostType;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.stream.Collectors;
import ru.example.group.main.security.SocialNetUserRegisterService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Value("${post.time.Life.Auto-Delete}")
    private int postLife;
    private final SocialNetUserRegisterService registerService;
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
            .stream().map(postEntity -> getPostDto(postEntity,postEntity.getUser())).toList();

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

//    private PostResponseDto getPostDtoFromEntity(PostEntity postEntity) {
//        return getDefaultBuilder(postEntity)
//            .comments(Collections.singletonList(postEntity.getComments()))
//            .author(getUserDtoFromEntity(postEntity.getUser()))
//            .type(PostType.POSTED.name())
//            .build();
//    }

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
    @Scheduled(cron = "@daily")
    public void deletePostAfter30Days() {
        postRepository.deletePostEntity(LocalDateTime.now().minusDays(postLife));

    }

    @Transactional
    public ResponseEntity<CommonResponseDto<PostResponseDto>> deletePost(Long id)
        throws EntityNotFoundException {
        try {
            var post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            var user = registerService.getCurrentUser();
            if (!post.getUser().getId().equals(user.getId())) {
                throw new IdUserException(
                    "id пользователя не совпадает с id пользователя опубликовавшего данный пост");
            }
            post.setDeleted(true);
            postRepository.saveAndFlush(post);

            return ResponseEntity.ok(getResponse(post, user));
        }catch (EntityNotFoundException|IdUserException e){
            log.debug(e.getMessage());
            var dto=new CommonResponseDto<PostResponseDto>();
            dto.setError(e.getMessage());
            return ResponseEntity.ok(dto);
        }
    }

    public ResponseEntity<PostResponseDto> recoverPost(Long id){
        try {
            var post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            var user = registerService.getCurrentUser();
            if (!post.getUser().getId().equals(user.getId())) {
                throw new IdUserException("Id пользователя не совпадает с id пользотеля создавшего пост");
            }
            post.setDeleted(false);
            postRepository.saveAndFlush(post);

            return ResponseEntity.ok(PostResponseDto.builder().build());
        }catch (EntityNotFoundException|IdUserException e){
            log.debug("такого поста нет {}", e.getMessage());
            return ResponseEntity.ok(PostResponseDto.builder().build());
        }
    }
    private CommonResponseDto<PostResponseDto> getResponse(PostEntity post, UserEntity user) {
        var responseDto = new CommonResponseDto<PostResponseDto>();
        responseDto.setError("ERROR");
        responseDto.setTimeStamp(LocalDateTime.now());
        responseDto.setData(getPostDto(post, user));
        return responseDto;
    }

    private PostResponseDto getPostDto(PostEntity post, UserEntity user) {
        return PostResponseDto.builder()
            .isBlocked(post.isBlocked())
            .comments(CommonListResponseDto.<CommentDto>builder()
                .perPage(0)
                .offset(0)
                .total(0)
                .error("")
                .timestamp(LocalDateTime.now())
                .data(new ArrayList<>()).build())
            .myLike(false)
            .author(getUserDtoFromEntity(user))
            .id(post.getId())
            .likes(0)
            .tags(null)
            .postText(post.getPostText())
            .time(post.getTime())
            .title(post.getTitle())
            .build();
    }

}