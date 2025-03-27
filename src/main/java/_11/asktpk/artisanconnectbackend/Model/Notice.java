package _11.asktpk.artisanconnectbackend.Model;

import jakarta.persistence.*;
import java.sql.Blob;

@Entity
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String username;

    @Column
    private String description;

    public Notice() {
        this.title = "";
        this.username = "";
        this.description = "";
    }

    public Notice(String nTitle, String nUsername, String nDescription) {
        this.title = nTitle;
        this.username = nUsername;
        this.description = nDescription;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
