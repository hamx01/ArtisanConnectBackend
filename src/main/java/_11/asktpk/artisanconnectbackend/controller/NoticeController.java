package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.service.ClientService;
import _11.asktpk.artisanconnectbackend.service.NoticeService;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1/notices")
@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ClientService clientService;

    @GetMapping("/all")
    public List<NoticeDTO> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNotice(@RequestBody NoticeDTO dto) {
        if (!clientService.clientExists(dto.getClientId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Nie znaleziono klienta o ID: " + dto.getClientId());
        }

        noticeService.addNotice(noticeService.createFromDTO(dto));

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
                if(!isError){
                    noticeService.addNotice(noticeService.createFromDTO(dto));
                }
            }
        }

        if(isError) {
            return response.status(HttpStatus.BAD_REQUEST).body("Nie znaleziono klientów: " + errors);
        }

        return response;
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity editNotice(@PathVariable("id") long id, @RequestBody NoticeDTO dto) {
        if (noticeService.noticeExists(id)) {
            return new ResponseEntity<>(noticeService.updateNotice(dto), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono ogłoszenia o ID: " + id);
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
