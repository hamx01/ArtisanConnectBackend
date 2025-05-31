package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.dto.CategoriesDTO;
import _11.asktpk.artisanconnectbackend.utils.Enums;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vars")
public class VariablesController {
    @GetMapping("/categories")
    public List<CategoriesDTO> getAllVariables() {
        List<CategoriesDTO> categoriesDTOList = new ArrayList<>();
        for (Map.Entry<Enums.Category, String> entry : Enums.categoryPL.entrySet()) {
            CategoriesDTO categoriesDTO = new CategoriesDTO();
            categoriesDTO.setLabel(entry.getValue());
            categoriesDTO.setValue(entry.getKey().toString());
            categoriesDTOList.add(categoriesDTO);
        }

        return categoriesDTOList;
    }

    @GetMapping("/statuses")
    public List<Enums.Status> getAllStatuses() {
        return List.of(Enums.Status.values());
    }
}
