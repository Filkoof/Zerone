package ru.example.group.main.controller.administrator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.response.UserAdminResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.service.AdminDashboardService;


@RequiredArgsConstructor
@RequestMapping("/admin/userAdmin")
@Controller
public class AdminUsersAdminController {

    private final UserRepository userRepository;
    private final AdminDashboardService adminDashboardService;

    @GetMapping()
    public String userAdmin (Model model,
                             @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("title", "Админ панель - Админы портала");
        model.addAttribute("page", userRepository.findAllUserAdmin(pageable));
        return "userAdmin";
    }

    @GetMapping("/{id}")
    public String userAdminEdit (@PathVariable Long id,
                                 Model model) {
        model.addAttribute("title", "Админ панель - Редактирование администратора");
        UserAdminResponseDto user = userRepository.findUserAdminById(id);
        model.addAttribute("user", user);
        return "userAdmin-edit";
    }

    @PostMapping("/{id}/save")
    public String userAdminSave (@PathVariable Long id,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String email,
                                 @RequestParam Boolean isBlocked,
                                 @RequestParam Boolean isDeleted,
                                 @RequestParam Boolean isAdmin,
                                 Model model) {
        UserEntity user = userRepository.findById(id).orElseThrow(IllegalStateException::new);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setBlocked(isBlocked);
        user.setDeleted(isDeleted);
        userRepository.save(user);
        adminDashboardService.makeRoleUserForUser(isAdmin, id);
        model.addAttribute("title", "Админ панель - Редактирование администратора");
        return "redirect:/admin/userAdmin";
    }
}
