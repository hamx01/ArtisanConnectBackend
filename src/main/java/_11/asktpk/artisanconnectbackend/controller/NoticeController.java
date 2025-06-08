package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.*;
import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import _11.asktpk.artisanconnectbackend.utils.Tools;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/api/v1/notices")
@RestController
public class NoticeController {
    private final NoticeService noticeService;
    private final ClientService clientService;
    private final Tools tools;

    public NoticeController(NoticeService noticeService, ClientService clientService, Tools tools) {
        this.noticeService = noticeService;
        this.clientService = clientService;
        this.tools = tools;
    }

    @GetMapping("/get/all")
    public List<NoticeResponseDTO> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getNoticeById(@PathVariable long id) {
        if (noticeService.noticeExists(id)) {
            return ResponseEntity.ok(noticeService.getNoticeById(id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<NoticeAdditionDTO> addNotice(@RequestBody NoticeRequestDTO dto, HttpServletRequest request) {
        Long clientId = tools.getClientIdFromRequest(request);
        if (!clientService.clientExists(clientId)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new NoticeAdditionDTO("Nie znaleziono klienta o ID: " + clientId));
        }

        dto.setClientId(clientId);

        if (dto.getCategory() == null || !Arrays.asList(Enums.Category.values()).contains(dto.getCategory())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new NoticeAdditionDTO("Nie ma takiej kategorii"));
        }

        Long newNoticeId = noticeService.addNotice(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new NoticeAdditionDTO(newNoticeId ,"Dodano ogłoszenie."));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> editNotice(@PathVariable("id") long id, @RequestBody NoticeRequestDTO dto, HttpServletRequest request) {
        Long clientIdFromToken = tools.getClientIdFromRequest(request);
        if (noticeService.noticeExists(id)) {
            if (!noticeService.isNoticeOwnedByClient(id, clientIdFromToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestResponseDTO("Nie masz uprawnień do edycji tego ogłoszenia."));
            }
            try {
                return ResponseEntity.status(HttpStatus.OK).body(noticeService.updateNotice(id, dto));
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono ogłoszenia o ID: " + id);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RequestResponseDTO> deleteNotice(@PathVariable("id") long id, HttpServletRequest request) {
        Long clientIdFromToken = tools.getClientIdFromRequest(request);
        if (noticeService.noticeExists(id)) {
            if (!noticeService.isNoticeOwnedByClient(id, clientIdFromToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestResponseDTO("Nie masz uprawnień do usunięcia tego ogłoszenia."));
            }

            noticeService.deleteNotice(id);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Pomyślnie usunięto ogłoszenie o ID: " + id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO("Nie znaleziono ogłoszenia o ID: " + id));
        }
    }

    @PostMapping("/boost")
    public ResponseEntity<RequestResponseDTO> boostNotice(@RequestBody NoticeBoostDTO dto, HttpServletRequest request) {
        Long clientId = tools.getClientIdFromRequest(request);
        if (noticeService.isNoticeOwnedByClient(dto.getNoticeId(), clientId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO("Ogłoszenie nie istnieje lub nie należy do zalogowanego klienta."));
        }
        noticeService.boostNotice(dto.getNoticeId());

        return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Ogłoszenie zostało pomyślnie wypromowane."));
    }
}
