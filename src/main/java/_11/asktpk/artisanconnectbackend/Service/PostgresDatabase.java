package _11.asktpk.artisanconnectbackend.Service;

import _11.asktpk.artisanconnectbackend.Model.Notice;
import _11.asktpk.artisanconnectbackend.Repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostgresDatabase implements IDatabase{
    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public void add(Notice newNotice) {
        noticeRepository.save(newNotice);
    }

    @Override
    public List<Notice> get() {
        return noticeRepository.findAll();
    }
}
