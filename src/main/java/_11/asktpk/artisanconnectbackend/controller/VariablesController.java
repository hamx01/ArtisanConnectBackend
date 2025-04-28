package _11.asktpk.artisanconnectbackend.controller;

import _11.asktpk.artisanconnectbackend.utils.Enums;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vars")
public class VariablesController {

    @GetMapping("/categories")
    public List<Enums.Category> getAllVariables() {
        return List.of(Enums.Category.values());
    }

    @GetMapping("/statuses")
    public List<Enums.Status> getAllStatuses() {
        return List.of(Enums.Status.values());
    }

    @GetMapping("/roles")
    public List<Enums.Role> getAllRoles() {
        return List.of(Enums.Role.values());
    }

}
