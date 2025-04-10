package _11.asktpk.artisanconnectbackend.service;

import _11.asktpk.artisanconnectbackend.entities.Notice;

import java.util.List;

public interface IDatabase {
    void add(Notice newNotice);
    List<Notice> get();
}
