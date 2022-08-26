package ru.example.group.main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.UrlImageResponseDto;
import ru.example.group.main.exception.CloudinaryException;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinaryConfig;

    public CloudinaryService(Cloudinary cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;}

    private String uploadFile(MultipartFile file) throws CloudinaryException {
        try {
            var uploadResult = cloudinaryConfig.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new CloudinaryException("Не удалось загрузить файл на сервер Cloudinary! " + e.getMessage());
        }
    }

    public CommonResponseDto<UrlImageResponseDto> uploadFileEndGetUrl(MultipartFile file) throws CloudinaryException {
        CommonResponseDto<UrlImageResponseDto> response = new CommonResponseDto<>();
        UrlImageResponseDto urlImageDto = new UrlImageResponseDto();
        urlImageDto.setUrl(uploadFile(file));
        response.setData(urlImageDto);
        return response;
    }

}
