package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.RequestResponseDTO;
import _11.asktpk.artisanconnectbackend.service.ImageService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;
    private final NoticeService noticeService;
    ImageController(ImageService imageService, NoticeService noticeService) {
        this.imageService = imageService;
        this.noticeService = noticeService;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload/{id}")
    public ResponseEntity<RequestResponseDTO> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable("id") Long noticeId, @Param("isMainImage") Boolean isMainImage) {
        try {
            if(file.isEmpty()) {
                return ResponseEntity.badRequest().body(new RequestResponseDTO("File is empty"));
            }

            if(!Objects.equals(file.getContentType(), "image/jpeg") && !Objects.equals(file.getContentType(), "image/png")) {
                return ResponseEntity.badRequest().body(new RequestResponseDTO("File must be a JPEG or PNG image."));
            }

            if(noticeId == null || !noticeService.noticeExists(noticeId)) {
                return ResponseEntity.badRequest().body(new RequestResponseDTO("Notice ID is invalid or does not exist."));
            }

            String newImageName = imageService.saveImageToStorage(uploadDir, file);
            imageService.addImageNameToDB(newImageName, noticeId, isMainImage);

            return ResponseEntity.ok(new RequestResponseDTO("Image uploaded successfully with new name: " + newImageName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new RequestResponseDTO(e.getMessage()));
        }
    }

    @GetMapping("/get/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageService.getImage(uploadDir, filename));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> getImagesNamesList(@PathVariable("id") Long noticeId) {
        List<String> result;
        try {
            noticeService.getNoticeById(noticeId);
            result = imageService.getImagesList(noticeId);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<RequestResponseDTO> deleteImage(@PathVariable("filename") String filename) {
        if(filename == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestResponseDTO("Filename is empty."));
        }

        try {
            imageService.deleteImage(uploadDir, filename);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Image deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getMessage()));
        }
    }
}
