package ru.example.group.main.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.UserDataResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.entity.enumerated.PostType;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.response.CommonListResponseDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;

    public CommonListResponseDto<PostResponseDto> getNewsfeed(String text, int offset, int itemPerPage) {
        var posts = postRepository.findAll(PageRequest.of(offset/itemPerPage, itemPerPage))
                .stream()
                .filter(postEntity -> !postEntity.isDeleted())
                .filter(postEntity -> !postEntity.isBlocked())
                .filter(postEntity -> postEntity.getTime().isBefore(LocalDateTime.now()))
                .map(this::getPostDtoFromEntity).toList();

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

