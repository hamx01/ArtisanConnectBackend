package _11.asktpk.artisanconnectbackend.Controller;

import _11.asktpk.artisanconnectbackend.Model.Notice;
import _11.asktpk.artisanconnectbackend.Service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class ArtisanConnectController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/notices/all")
    public List<Notice> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/notices/add")
    public void addNotice(@RequestBody List<Notice> notices_list) {
        for (Notice notice : notices_list) {
            noticeService.addNotice(notice);
        }
    }
}
