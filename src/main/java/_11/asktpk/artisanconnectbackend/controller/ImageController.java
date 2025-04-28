package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.RequestResponseDTO;
import _11.asktpk.artisanconnectbackend.service.ImageService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
    public ResponseEntity<RequestResponseDTO> uploadImage(@RequestParam("file") MultipartFile file, @PathVariable("id") Long noticeId) {
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
            imageService.addImageUrlToDB(newImageName, noticeId);

            return ResponseEntity.ok(new RequestResponseDTO("Image uploaded successfully with new name: " + newImageName));
        } catch (Exception e) {
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
    public ResponseEntity<List<String>> getImagesNamesList(@PathVariable("id") Long noticeId) {
        if(noticeId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonList("Notice ID is invalid or does not exist."));
        }

        List<String> result;
        try {
            result = imageService.getImagesList(noticeId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(e.getMessage()));
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RequestResponseDTO> deleteImage(@PathVariable("id") String filename) {
        try {
            imageService.deleteImage(uploadDir, filename);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Image deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getMessage()));
        }
    }
}
