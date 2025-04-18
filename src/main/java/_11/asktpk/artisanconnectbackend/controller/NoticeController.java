package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    public NoticeController(NoticeService noticeService, ClientService clientService) {
        this.noticeService = noticeService;
        this.clientService = clientService;
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
        if (!noticeService.noticeExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono ogłoszenia o ID: " + id);
        }

        try {
            String filePath = noticeService.saveImage(id, file);
            return ResponseEntity.ok("Zdjęcie zapisane pod ścieżką: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas zapisywania zdjęcia: " + e.getMessage());
        }
    }


    @GetMapping("/images/{id}")
    public ResponseEntity<List<String>> getAllImages(@PathVariable("id") Long id) {
        try {
            Path directoryPath = Paths.get("src/main/resources/static/images/notices/" + id);
            if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<String> imagePaths = new ArrayList<>();
            Files.list(directoryPath).forEach(file -> {
                if (Files.isRegularFile(file) && Files.isReadable(file)) {
                    imagePaths.add(file.toString());
                }
            });

            if (imagePaths.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(imagePaths);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/check/{id}")
//    public ResponseEntity<String> checkNotice(@PathVariable("id") long id) {
//        if (noticeService.noticeExists(id)) {
//            return ResponseEntity.ok("Ogłoszenie o ID " + id + " istnieje.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono ogłoszenia o ID: " + id);
//        }
//    }
}
