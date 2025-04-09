package _11.asktpk.artisanconnectbackend.Repository;

import _11.asktpk.artisanconnectbackend.Entities.Notice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
