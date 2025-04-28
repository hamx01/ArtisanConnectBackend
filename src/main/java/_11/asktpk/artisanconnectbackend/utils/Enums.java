package _11.asktpk.artisanconnectbackend.utils;

import java.util.Map;

public class Enums {
    public enum Role {
        ADMIN, USER
    }

    public enum Category {
        Handmade, Woodworking, Metalworking, Ceramics,
        Textiles, Jewelry, Leatherwork, Painting, Sculpture,
        Glasswork, Furniture, Restoration, Tailoring, Weaving,
        Calligraphy, Pottery, Blacksmithing, Basketry, Embroidery,
        Knitting, Carpentry, Other
    }

    public static final Map<Category, String> categoryPL = Map.ofEntries(
            Map.entry(Category.Handmade, "Rękodzieło"),
            Map.entry(Category.Woodworking, "Stolarstwo"),
            Map.entry(Category.Metalworking, "Obróbka metalu"),
            Map.entry(Category.Ceramics, "Ceramika"),
            Map.entry(Category.Textiles, "Tekstylia"),
            Map.entry(Category.Jewelry, "Biżuteria"),
            Map.entry(Category.Leatherwork, "Wyroby skórzane"),
            Map.entry(Category.Painting, "Malarstwo"),
            Map.entry(Category.Sculpture, "Rzeźbiarstwo"),
            Map.entry(Category.Glasswork, "Szklarstwo"),
            Map.entry(Category.Furniture, "Meble"),
            Map.entry(Category.Restoration, "Renowacja"),
            Map.entry(Category.Tailoring, "Krawiectwo"),
            Map.entry(Category.Weaving, "Tkactwo"),
            Map.entry(Category.Calligraphy, "Kaligrafia"),
            Map.entry(Category.Pottery, "Garncarstwo"),
            Map.entry(Category.Blacksmithing, "Kowalstwo"),
            Map.entry(Category.Basketry, "Koszykarstwo"),
            Map.entry(Category.Embroidery, "Hafciarstwo"),
            Map.entry(Category.Knitting, "Dzierganie"),
            Map.entry(Category.Carpentry, "Ciesielstwo"),
            Map.entry(Category.Other, "Inne")
    );

    public enum Status {
        ACTIVE, INACTIVE
    }
}
