package _11.asktpk.artisanconnectbackend.Controller;

import _11.asktpk.artisanconnectbackend.Model.Notice;
import _11.asktpk.artisanconnectbackend.Repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.Service.PostgresDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class ArtisanConnectController {
    @Autowired
    private PostgresDatabase postgresDatabase;

    @GetMapping("/notices/all")
    public List<Notice> getAllNotices() {
        return postgresDatabase.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/notices/add")
    public void addNotice(@RequestBody Notice notice) {
        postgresDatabase.add(notice);
    }
}
