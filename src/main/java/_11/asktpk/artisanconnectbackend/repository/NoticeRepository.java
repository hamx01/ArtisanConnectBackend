package _11.asktpk.artisanconnectbackend.repository;

import _11.asktpk.artisanconnectbackend.entities.Notice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    boolean existsByIdNoticeAndClientId(long noticeId, long clientId);

}
