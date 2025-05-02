package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.NoticeAdditionDTO;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<NoticeAdditionDTO> addNotice(@RequestBody NoticeDTO dto) {
        if (!clientService.clientExists(dto.getClientId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NoticeAdditionDTO("Nie znaleziono klienta o ID: " + dto.getClientId()));
        }

        if (dto.getCategory() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NoticeAdditionDTO("Nie ma takiej kategorii"));
        }

        dto.setPublishDate(java.time.LocalDateTime.now());

        Long newNoticeId = noticeService.addNotice(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new NoticeAdditionDTO(newNoticeId ,"Dodano ogłoszenie."));
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
}
