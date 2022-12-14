package ru.example.group.main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.CloudinaryException;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CloudinaryService {

    @Value("${cloudinary.default_avatar}")
    private String defaultAvatar;

    private final Cloudinary cloudinary;
    private final SocialNetUserRegisterService socialNetUserRegisterService;

    public String uploadFile(MultipartFile file) throws CloudinaryException {
        try {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new CloudinaryException("Не удалось загрузить файл на сервер Cloudinary! " + e.getMessage());
        }
    }

    public void deleteImageFromUserEntity() throws CloudinaryException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        if (user.getPhoto() != null) {
            String publicId = parsePublicIdFromUserEntityPhoto(socialNetUserRegisterService.getCurrentUser());
            if (!publicId.contains("avatar")) {
                deleteFile(publicId);
            }
        }
    }

    public String parsePublicIdFromUserEntityPhoto(UserEntity user) {
        return user.getPhoto().substring(user.getPhoto().length() - 24, user.getPhoto().length() - 4);
    }

    public void deleteFile(String publicId) throws CloudinaryException {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("folder", "pets");
            param.put("invalidate", "true");
            cloudinary.uploader().destroy(publicId, param);
        } catch (Exception ex) {
            throw new CloudinaryException("Не удалось удалить файл с сервера Cloudinary! " + ex.getMessage());
        }
    }

    public CommonResponseDto<FileResponseDto> uploadFileEndGetUrl(MultipartFile file) throws CloudinaryException {
        deleteImageFromUserEntity();
        CommonResponseDto<FileResponseDto> response = new CommonResponseDto<>();
        FileResponseDto urlImageDto = new FileResponseDto();
        urlImageDto.setUrl(uploadFile(file));
        response.setData(urlImageDto);
        return response;
    }
}