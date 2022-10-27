package ru.example.group.main.controller.administrator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.repository.UserRoleRepository;
import ru.example.group.main.service.AdminDashboardService;

@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Controller

public class AdminUsersController {

    private final UserRepository userRepository;
    private final AdminDashboardService adminDashboardService;
    private final UserRoleRepository userRoleRepository;

    @GetMapping()
    public String users (Model model,
                         @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserEntity> users = userRepository.findAllUsersWithoutAdmins(pageable);
        int number = users.getNumber();
        model.addAttribute("page", users);
        model.addAttribute("number", number);
        model.addAttribute("url", "/admin/users");
        model.addAttribute("title", "Админ панель - Пользователи");
        return "users";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String filter, Model model,
                         @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserEntity> users = adminDashboardService.userFilter(pageable, filter);
        model.addAttribute("url", "/admin/users");
        model.addAttribute("page", users);
        return "users";
    }

    @GetMapping("/{id}")
    public String userEdit (@PathVariable Long id,
                            Model model) {
        model.addAttribute("title", "Админ панель - Редактирование пользователей");
        UserEntity user = userRepository.findById(id).orElseThrow(IllegalStateException::new);
        model.addAttribute("user", user);
        model.addAttribute("role", adminDashboardService.isAdmin(user));
        return "user-edit";
    }

    @PostMapping("/{id}/save")
    public String userSave (@PathVariable Long id,
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String about,
                            @RequestParam String email,
                            @RequestParam String country,
                            @RequestParam String city,
                            @RequestParam Boolean isBlocked,
                            @RequestParam Boolean isDeleted,
                            @RequestParam Boolean isAdmin,
                            Model model) {
        UserEntity user = userRepository.findById(id).orElseThrow(IllegalStateException::new);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAbout(about);
        user.setEmail(email);
        user.setCountry(country);
        user.setCity(city);
        user.setBlocked(isBlocked);
        user.setDeleted(isDeleted);
        userRepository.save(user);
        adminDashboardService.makeRoleAdminForUser(isAdmin, id);
        model.addAttribute("title", "Админ панель - Пользователи");
        return "redirect:/admin/users";
    }

}
