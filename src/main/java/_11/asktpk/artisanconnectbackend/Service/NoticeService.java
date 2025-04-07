package _11.asktpk.artisanconnectbackend.Service;

import _11.asktpk.artisanconnectbackend.Model.Notice;
import _11.asktpk.artisanconnectbackend.Repository.NoticeRepository;
import _11.asktpk.artisanconnectbackend.dto.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

//    private NoticeDTO mapToDTO(Notice notice) {
//        return new NoticeDTO(
//
//        );
//    }
//
//    private Notice mapToEntity(NoticeDTO productDTO) {
//        return new Notice(
//
//        );
//    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public void addNotice(Notice notice) {
        noticeRepository.save(notice);
    }
}
