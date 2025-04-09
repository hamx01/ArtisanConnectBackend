package _11.asktpk.artisanconnectbackend.Service;

import _11.asktpk.artisanconnectbackend.Entities.Notice;

import java.util.List;

public interface IDatabase {
    void add(Notice newNotice);
    List<Notice> get();
}
