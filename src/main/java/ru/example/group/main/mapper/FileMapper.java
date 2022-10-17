package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.FileEntity;
import ru.example.group.main.entity.PostEntity;

@Mapper(componentModel = "spring", uses = {PostMapper.class, CommentMapper.class})
public interface FileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", source = "post")
    @Mapping(target = "path", source = "fileDto.url")
    @Mapping(target = "comment", source = "comment")
    FileEntity commentFileRequestToEntity(FileResponseDto fileDto, PostEntity post, CommentEntity comment);

    @Mapping(target = "id", source = "file.id")
    @Mapping(target = "url", source = "file.path")
    FileResponseDto fileEntityToDto(FileEntity file);
}
