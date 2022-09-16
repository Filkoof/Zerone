package ru.example.group.main.service;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.enumerated.PostType;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.map.PostMapper;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.repository.TagRepository;
import ru.example.group.main.repository.UserRepository;
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
    private final TagRepository tagRepository;
    private final CommentService commentService;
    private final PostMapper mapper;

    public ResponseEntity<CommonResponseDto<PostResponseDto>> addNewPost(
            final PostRequestDto request,
            final long id,
            final long publishDate
    ) {
        var response = new CommonResponseDto<PostResponseDto>();
        var userEntity = userRepository.findById(id).orElseThrow();
        var requestedDateTime = LocalDateTime.ofEpochSecond(publishDate / 1000, 0, ZoneOffset.UTC);
        var dateTimeNow = LocalDateTime.now();
        var publishDateTime = requestedDateTime.isBefore(dateTimeNow) ? dateTimeNow : requestedDateTime;
        var isQueued = publishDateTime.isAfter(dateTimeNow);

        var tags = request.getTags() != null ? new HashSet<>(request.getTags().stream().map(tagRepository::findByTag).toList()) : null;
        var postE = mapper.postRequestToEntity(request, publishDateTime, tags, userEntity);
        postRepository.save(postE);

        response.setData(getPostDtoFromEntity(postE));
        return ResponseEntity.ok(response);
    }

    public CommonListResponseDto<PostResponseDto> getNewsfeed(String text, int offset, int itemPerPage) {
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var statePage = postRepository.findAllPostsWithPagination(text, pageable);

        return CommonListResponseDto.<PostResponseDto>builder()
                .total((int) statePage.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(statePage.stream().map(this::getPostDtoFromEntity).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }


    public CommonListResponseDto<PostResponseDto> getNewsUserId(Long id, int offset) {
        var itemPerPage = postRepository.findAllByUserPost(id) == 0 ? 5 : postRepository.findAllByUserPost(id);
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var statePage = postRepository.findAllPostsUserId(id, pageable);
        return CommonListResponseDto.<PostResponseDto>builder()
                .total((int) statePage.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(statePage.stream().map(this::getPostDtoFromEntity).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CommonListResponseDto<Object> getNewsByListUserId(List<Long> listPostId, int offset) {
        var itemPerPage = 0;
        List<Object> postList = new ArrayList();
        for (Long postId : listPostId) {
            itemPerPage += postRepository.findAllByUserPost(postId) == 0 ? 5 : postRepository.findAllByUserPost(postId);
            var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
            postList.add(getPostDtoFromEntity(postRepository.findPostEntityById(postId)));
        }
        return CommonListResponseDto.builder()
                .total(postList.size())
                .perPage(itemPerPage)
                .offset(offset)
                .data(postList)
                .error("OK")
                .timestamp(LocalDateTime.now())
                .build();
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

            var response = new CommonResponseDto<PostResponseDto>();
            response.setError("ERROR");
            response.setTimeStamp(LocalDateTime.now());
            response.setData(getPostDtoFromEntity(post));
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | IdUserException e) {
            log.debug(e.getMessage());
            var dto = new CommonResponseDto<PostResponseDto>();
            dto.setError(e.getMessage());
            return ResponseEntity.ok(dto);
        }
    }

    public ResponseEntity<PostResponseDto> recoverPost(Long id) {
        try {
            var post = postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            var user = registerService.getCurrentUser();
            if (!post.getUser().getId().equals(user.getId())) {
                throw new IdUserException("Id пользователя не совпадает с id пользотеля создавшего пост");
            }
            post.setDeleted(false);
            postRepository.saveAndFlush(post);

            return ResponseEntity.ok(PostResponseDto.builder().build());
        } catch (EntityNotFoundException | IdUserException e) {
            log.debug("такого поста нет {}", e.getMessage());
            return ResponseEntity.ok(PostResponseDto.builder().build());
        }
    }

    @Scheduled(cron = "@daily")
    public void deletePostAfter30Days() {
        postRepository.deletePostEntity(LocalDateTime.now().minusDays(postLife));
    }

    private PostResponseDto getPostDtoFromEntity(PostEntity postEntity) {
        try {
            var tags = postEntity.getTagEntities().stream().map(TagEntity::getTag).collect(toList());
            var type = getType(postEntity);
            var listComment = commentService.getCommonList(postEntity.getId(), 5, 0);
            return mapper.postEntityToDto(postEntity, tags, type, listComment);
        } catch (NullPointerException e) {
            e.getMessage();
        }
        return PostResponseDto.builder().build();
    }

    private PostType getType(PostEntity post) {
        if (post.isDeleted()) {
            return PostType.DELETED;
        } else if (post.getTime().isAfter(LocalDateTime.now())) {
            return PostType.QUEUED;
        } else return PostType.POSTED;
    }
}