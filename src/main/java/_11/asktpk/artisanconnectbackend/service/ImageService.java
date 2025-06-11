package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.entities.Image;
import _11.asktpk.artisanconnectbackend.repository.ImageRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;

    ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String saveImageToStorage(String uploadDirectory, MultipartFile imageFile) throws IOException {
        String uniqueFileName = UUID.randomUUID() + imageFile.getOriginalFilename();

        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    public void addImageNameToDB(String filename, Long noticeId, boolean isMainImage) {
        Image image = new Image();
        image.setImageName(filename);
        image.setNoticeId(noticeId);
        image.setMainImage(isMainImage);
        imageRepository.save(image);
    }

    public Resource getImage(String imageDirectory, String imageName) throws IOException {
        Path filePath = Paths.get(imageDirectory).resolve(imageName);
        Resource resource = new UrlResource(filePath.toUri());

        if(imageName.isEmpty() || imageDirectory.isEmpty()) {
            throw new IOException("Filename or folder is empty. Please check your request and try again.");
        }

        if (!resource.exists()) {
            throw new IOException("File not found");
        }

        return resource;
    }

    public void deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        deleteImageRecordFromDB(imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        } else {
            throw new IOException("File not found");
        }
    }

    public List<String> getImagesList(Long noticeID) throws Exception {
        List<Image> images = imageRepository.findByNoticeId(noticeID);
        if (images.isEmpty()) {
            throw new Exception("There is no images for this notice");
        }

        return images.stream()
                .map(Image::getImageName)
                .collect(Collectors.toList());
    }

    public void deleteImageRecordFromDB(String imageName) {
        if(imageRepository.existsImageByImageNameEqualsIgnoreCase(imageName)) {
            imageRepository.deleteByImageNameEquals(imageName);
        }
    }
}