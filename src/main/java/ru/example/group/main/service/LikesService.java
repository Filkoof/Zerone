package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.LikesRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LikesResponseDto;
import ru.example.group.main.entity.LikeEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.LikeType;
import ru.example.group.main.repository.LikesRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final SocialNetUserRegisterService registerService;

    public CommonResponseDto<LikesResponseDto> getLikes(final long itemId, final LikeType type) {
        var likes = likesRepository.findByEntityIdAndType(itemId, type);
        var res = new CommonResponseDto<LikesResponseDto>();
        var userIds = Collections.EMPTY_LIST;
        if (likes.isPresent()) {
            userIds = likes.get().stream()
                    .map(likeEntity -> likeEntity.getUser().getId())
                    .toList();
            res.setData(new LikesResponseDto(userIds, userIds.size()));
        }
        return res;
    }

    public CommonResponseDto<LikesResponseDto> deleteLike(long itemId, String type) {
        var user = registerService.getCurrentUser();
        var res = new CommonResponseDto<LikesResponseDto>();
        var likesCount = likesRepository.countByEntityIdAndType(itemId, LikeType.getLikeTypeFromString(type));
        var deletedLikes = likesRepository.deleteByEntityIdAndUserIdAndType(itemId, user.getId(), LikeType.getLikeTypeFromString(type));

        res.setData(
                new LikesResponseDto(
                        List.of(user.getId()),
                        deletedLikes.isEmpty() ? likesCount : likesCount - 1
                ));
        return res;
    }

    public CommonResponseDto<LikesResponseDto> putLike(final LikesRequestDto request) {
        var user = registerService.getCurrentUser();
        var res = new CommonResponseDto<LikesResponseDto>();
        var like = new LikeEntity();
        var likesCount = likesRepository.countByEntityIdAndType(request.getItemId(), LikeType.getLikeTypeFromString(request.getType()));
        Boolean check = likesRepository.existsByEntityIdAndTypeAndUser(request.getItemId(), LikeType.getLikeTypeFromString(request.getType()), user);
        if (check == Boolean.FALSE) {
            like.setEntityId(request.getItemId());
            like.setUser(user);
            like.setType(LikeType.getLikeTypeFromString(request.getType()));
            like.setTime(LocalDateTime.now());
            try {
                likesRepository.save(like);
            } catch (Exception e) {
                e.printStackTrace();
            }


            likesCount++;
        }

        res.setData(new LikesResponseDto(List.of(user.getId()), likesCount));
        return res;
    }

    public Integer likesCountByPostIdAndType(Long postOrCommentId, LikeType type) {
        return likesRepository.countByEntityIdAndType(postOrCommentId, type);
    }

    public Boolean isMyLikeByPostOrCommentIdAndTypeAndUserId(Long postOrCommentId, LikeType type, UserEntity user) {
        return likesRepository.existsByEntityIdAndTypeAndUser(postOrCommentId, type, user);
    }
}
