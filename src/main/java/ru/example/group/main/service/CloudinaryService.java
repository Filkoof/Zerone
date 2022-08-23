package ru.example.group.main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.example.group.main.repository.UserRepository;


@Service
public class CloudinaryService {

    private final Cloudinary cloudinaryConfig;

    public CloudinaryService(Cloudinary cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;}

    public String uploadFile(MultipartFile file) {
        try {
            var uploadResult = cloudinaryConfig.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
