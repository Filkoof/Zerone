package ru.example.group.main.controller.administrator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.service.AdminDashboardService;

@RequiredArgsConstructor
@RequestMapping("/admin/publications")
@Controller
public class AdminPublicationsController {

    private final PostRepository postRepository;
    private final AdminDashboardService adminDashboardService;

    @GetMapping()
    public String publications (Model model,
                                @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostEntity> posts = postRepository.findAll(pageable);
        int number = posts.getNumber();
        model.addAttribute("page", posts);
        model.addAttribute("number", number);
        model.addAttribute("url", "/admin/publications");
        model.addAttribute("title", "Админ панель - Посты");
        return "publications";
    }

    @GetMapping("/{id}")
    public String publicationEdit (@PathVariable Long id,
                                   Model model) {
        model.addAttribute("title", "Админ панель - Редактирование постов");
        PostEntity post = postRepository.findById(id).orElseThrow(IllegalStateException::new);
        model.addAttribute("post", post);
        return "publication-edit";
    }

    @PostMapping("/{id}/save")
    public String postSave (@PathVariable Long id,
                            @RequestParam String postTitle,
                            @RequestParam String postText,
                            @RequestParam Boolean isBlocked,
                            @RequestParam Boolean isDeleted,
                            Model model) {
        PostEntity post = postRepository.findById(id).orElseThrow(IllegalStateException::new);
        post.setTitle(postTitle);
        post.setPostText(postText);
        post.setBlocked(isBlocked);
        post.setDeleted(isDeleted);
        postRepository.save(post);
        model.addAttribute("title", "Админ панель - Посты");
        return "redirect:/admin/publications";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String filter, Model model,
                         @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostEntity> posts = adminDashboardService.postFilter(pageable, filter);
        model.addAttribute("url", "/admin/publications");
        model.addAttribute("page", posts);
        return "publications";
    }

}
