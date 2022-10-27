package ru.example.group.main.controller.administrator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.service.AdminDashboardService;

@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Controller
public class AdminCommentsController {

    private final CommentRepository commentRepository;
    private final AdminDashboardService adminDashboardService;


    @GetMapping()
    public String comments (Model model,
                            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentEntity> comments = commentRepository.findAll(pageable);
        int number = comments.getNumber();
        model.addAttribute("page", comments);
        model.addAttribute("number", number);
        model.addAttribute("url", "/admin/comments");
        model.addAttribute("title", "Админ панель - Комментарии");
        return "comments";
    }

    @GetMapping("/{id}")
    public String commentEdit (@PathVariable Long id,
                               Model model) {
        model.addAttribute("title", "Админ панель - Редактирование комментариев");
        CommentEntity comment = commentRepository.findById(id).orElseThrow(IllegalStateException::new);
        model.addAttribute("comment", comment);
        return "comment-edit";
    }

    @PostMapping("/{id}/save")
    public String commentSave (@PathVariable Long id,
                               @RequestParam String commentText,
                               @RequestParam Boolean isBlocked,
                               @RequestParam Boolean isDeleted,
                               Model model) {
        CommentEntity comment = commentRepository.findById(id).orElseThrow(IllegalStateException::new);
        comment.setCommentText(commentText);
        comment.setBlocked(isBlocked);
        comment.setDeleted(isDeleted);
        commentRepository.save(comment);
        model.addAttribute("title", "Админ панель - Комментарии");
        return "redirect:/admin/comments";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String filter, Model model,
                         @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentEntity> comments = adminDashboardService.commentFilter(pageable, filter);
        model.addAttribute("url", "/admin/comments");
        model.addAttribute("page", comments);
        return "comments";
    }

}
