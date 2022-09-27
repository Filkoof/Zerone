package ru.example.group.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.repository.UserRepository;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public AdminController(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/admin")
    public String main (Model model) {
        model.addAttribute("title", "Админ панель");
        return "index";
    }
    
    @ModelAttribute("allUsers")
    public int getCountAllUsers() { return userRepository.findAll().size(); }

    @ModelAttribute("allPosts")
    public int getCountAllPosts() { return postRepository.findAll().size(); }

    @ModelAttribute("allComments")
    public int getCountAllComments() { return commentRepository.findAll().size(); }
}
