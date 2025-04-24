package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.ImageService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1/notices")
@RestController
public class NoticeController {
    private final NoticeService noticeService;
    private final ClientService clientService;
    private final ImageService imageService;

    public NoticeController(NoticeService noticeService, ClientService clientService, ImageService imageService) {
        this.noticeService = noticeService;
        this.clientService = clientService;
        this.imageService = imageService;
    }

    @GetMapping("/get/all")
    public List<NoticeDTO> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getNoticeById(@PathVariable long id) {
        if (noticeService.noticeExists(id)) {
            return ResponseEntity.ok(noticeService.getNoticeById(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNotice(@RequestBody NoticeDTO dto) {
        if (!clientService.clientExists(dto.getClientId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Nie znaleziono klienta o ID: " + dto.getClientId());
        }

        dto.setPublishDate(java.time.LocalDateTime.now());

        noticeService.addNotice(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Dodano ogłoszenie.");
    }


    // TODO: zamiast dodawać tutaj pętlą, musi to robić NoticeService, trzeba zaimplementować odpowienią metodę
    @PostMapping("/bulk_add")
    public ResponseEntity<String> addNotices(@RequestBody List<NoticeDTO> notices_list) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.CREATED);
        List<String> errors = new ArrayList<>();
        boolean isError = false;

        if (notices_list.isEmpty()) {
            return response.status(HttpStatus.BAD_REQUEST).body("Lista ogłoszeń jest pusta.");
        }

        for (NoticeDTO dto : notices_list) {
            if (!clientService.clientExists(dto.getClientId())) {
                isError = true;
                errors.add(dto.getClientId().toString());
            } else {
                if (!isError) {
                    noticeService.addNotice(dto);
                }
            }
        }

        if (isError) {
            return response.status(HttpStatus.BAD_REQUEST).body("Nie znaleziono klientów: " + errors);
        }

        return response;
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> editNotice(@PathVariable("id") long id, @RequestBody NoticeDTO dto) {
        if (noticeService.noticeExists(id)) {
            try {
                return new ResponseEntity<>(noticeService.updateNotice(id, dto), HttpStatus.OK);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono ogłoszenia o ID: " + id);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteNotice(@PathVariable("id") long id) {
        if (noticeService.noticeExists(id)) {
            noticeService.deleteNotice(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nie przesłano pliku.");
        }

        if (!noticeService.noticeExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono ogłoszenia o ID: " + id);
        }

        try {
            String filePath = noticeService.saveImage("./app/images/notices/", id, file);
            return ResponseEntity.ok("Zdjęcie zapisane pod ścieżką: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas zapisywania zdjęcia: " + e.getMessage());
        }
    }


    @GetMapping("/images/{noticeId}/{imageIndex}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long noticeId, @PathVariable Integer imageIndex) {
        try {
            if (!noticeService.noticeExists(noticeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            NoticeDTO notice = noticeService.getNoticeById(noticeId);
            List<String> imagePaths = notice.getImages();

            if (imagePaths == null || imagePaths.isEmpty() || imageIndex >= imagePaths.size() || imageIndex < 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String imagePath = imagePaths.get(imageIndex);
            byte[] imageBytes = imageService.getImageBytes(imagePath);
            MediaType mediaType = imageService.getMediaTypeForImage(imagePath);

            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<List<String>> getNoticeImageUrls(@PathVariable("id") Long id) {
        try {
            if (!noticeService.noticeExists(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            NoticeDTO notice = noticeService.getNoticeById(id);
            List<String> imagePaths = notice.getImages();

            if (imagePaths == null || imagePaths.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Zamiast przesyłać bajty, zwracamy listę URL-i do obrazów
            List<String> imageUrls = new ArrayList<>();
            for (int i = 0; i < imagePaths.size(); i++) {
                imageUrls.add("/api/v1/notices/images/" + id + "/" + i);
            }

            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
