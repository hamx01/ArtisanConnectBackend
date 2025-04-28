package _11.asktpk.artisanconnectbackend.entities;

import jakarta.persistence.*;
import jdk.jfr.BooleanFlag;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter @Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long noticeId;

    private String imageName;

    @BooleanFlag
    private boolean isMainImage;
}
