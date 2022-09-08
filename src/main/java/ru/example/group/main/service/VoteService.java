package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.VoteRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.VoteResponseDto;
import ru.example.group.main.entity.LikeEntity;
import ru.example.group.main.entity.enumerated.LikeType;
import ru.example.group.main.repository.VoteRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final SocialNetUserRegisterService registerService;

    public CommonResponseDto<VoteResponseDto> getLikes(final long itemId, final LikeType type) {
        var likes = voteRepository.findByEntityIdAndType(itemId, type);
        var res = new CommonResponseDto<VoteResponseDto>();
        var userIds = Collections.EMPTY_LIST;

        if (likes.isPresent()) {
            userIds = likes.get().stream()
                .map(likeEntity -> likeEntity.getUser().getId())
                .toList();
        }

        res.setData(new VoteResponseDto(userIds, userIds.size()));
        return res;
    }

    public CommonResponseDto<VoteResponseDto> deleteLike(final long itemId, final LikeType type) {
        var user = registerService.getCurrentUser();
        var res = new CommonResponseDto<VoteResponseDto>();
        var likesCount = voteRepository.countByEntityIdAndType(itemId, type);
        var deletedLikes = voteRepository.deleteByEntityIdAndUserIdAndType(itemId, user.getId(), type);

        res.setData(
            new VoteResponseDto(
                List.of(user.getId()),
                deletedLikes.get().isEmpty() ? likesCount : likesCount - 1
            ));
        return res;
    }

    public CommonResponseDto<VoteResponseDto> putLike(final VoteRequestDto request) {
        var user = registerService.getCurrentUser();
        var res = new CommonResponseDto<VoteResponseDto>();
        var like = new LikeEntity();
        var likesCount = voteRepository.countByEntityIdAndType(request.getItemId(), (request.getType()));

        if (voteRepository.existsByEntityIdAndType(request.getItemId(), request.getType()) == Boolean.FALSE) {
            like.setEntityId(request.getItemId());
            like.setUser(user);
            like.setType((request.getType()));
            like.setTime(LocalDateTime.now());
            voteRepository.save(like);

            likesCount++;
        }

        res.setData(new VoteResponseDto(List.of(user.getId()), likesCount));
        return res;
    }
}
