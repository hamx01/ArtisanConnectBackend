package _11.asktpk.artisanconnectbackend.Controller;

import _11.asktpk.artisanconnectbackend.Entities.Notice;
import _11.asktpk.artisanconnectbackend.Repository.ClientRepository;
import _11.asktpk.artisanconnectbackend.Service.NoticeService;
import _11.asktpk.artisanconnectbackend.DTO.NoticeDTO;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.servlet.function.ServerResponse.status;

@RequestMapping("/api/v1")
@RestController
public class ArtisanConnectController {
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/notices/all")
    public List<Notice> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @PostMapping("/notices/add")
    public ResponseEntity<String> addNotice(@RequestBody NoticeDTO dto) {
        if (!clientRepository.existsById(dto.getClientId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Nie znaleziono klienta o ID: " + dto.getClientId());
        }
        noticeService.addNotice(noticeService.createFromDTO(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body("Dodano ogłoszenie.");
    }


    // TODO: zamiast dodawać tutaj pętlą, musi to robić NoticeService, trzeba zaimplementować odpowienią metodę
    @PostMapping("/notices/bulk_add")
    public ResponseEntity<String> addNotice(@RequestBody List<NoticeDTO> notices_list) {
        List<String> errors = new ArrayList<>();
        boolean isError = false;
        if (notices_list.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Lista ogłoszeń jest pusta.");
        }
        for (NoticeDTO dto : notices_list) {
            if (!clientRepository.existsById(dto.getClientId())) {
                isError = true;
                errors.add("Nie znaleziono klienta o ID: " + dto.getClientId());
            }
        }
        if(isError) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errors.toString());
        }

        for(NoticeDTO dto : notices_list) {
            noticeService.addNotice(noticeService.createFromDTO(dto));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Dodano ogłoszenia.");
    }
}
