package _11.asktpk.artisanconnectbackend.dto;

//[
//        { "label": "Meble", "value": "Furniture" },
//        { "label": "Biżuteria", "value": "Jewelry" },
//        { "label": "Ceramika", "value": "Ceramics" }
//]

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoriesDTO {
    String label;
    String value;
}
