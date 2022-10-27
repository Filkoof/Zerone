package ru.example.group.main.controller.administrator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.service.AdminDashboardService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller

public class AdminController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/statistic")
    public String main (Model model) {
        model.addAttribute("title", "Админ панель - статистика");
        return "index";
    }

    @ModelAttribute("allUsers")
    public int getCountAllUsers() { return userRepository.findAll().size(); }

    @ModelAttribute("allUsersDeleted")
    public int getCountAllUsersDeleted() { return userRepository.findAllByDeleted().size(); }

    @ModelAttribute("allUsersBlocked")
    public int getCountAllUsersBlocked() { return userRepository.findAllByBlocked().size(); }

    @ModelAttribute("percentUsersDashboard")
    public float getPercentUsersDashboard() {
        return (float) ((100 - ((getCountAllUsersDeleted() + getCountAllUsersBlocked()) * 100 / getCountAllUsers())) * 0.01);
    }

    @ModelAttribute("allPosts")
    public int getCountAllPosts() { return postRepository.findAll().size(); }

    @ModelAttribute("allPostsDeleted")
    public int getCountAllPostsDeleted() { return postRepository.findAllByDeleted().size(); }

    @ModelAttribute("allPostsBlocked")
    public int getCountAllPostsBlocked() { return postRepository.findAllByBlocked().size(); }

    @ModelAttribute("percentPostsDashboard")
    public float getPercentPostsDashboard() {
        return (float) ((100 - ((getCountAllPostsDeleted() + getCountAllPostsBlocked()) * 100 / getCountAllPosts())) * 0.01);
    }

    @ModelAttribute("allComments")
    public int getCountAllComments() { return commentRepository.findAll().size(); }

    @ModelAttribute("allCommentsDeleted")
    public int getCountAllCommentsDeleted() { return commentRepository.findAllByDeleted().size(); }

    @ModelAttribute("allCommentsBlocked")
    public int getCountAllCommentsBlocked() { return commentRepository.findAllByBlocked().size(); }

    @ModelAttribute("percentCommentsDashboard")
    public float getPercentCommentsDashboard() {
        return (float) ((100 - ((getCountAllCommentsDeleted() + getCountAllCommentsBlocked()) * 100 / getCountAllComments())) * 0.01);
    }

    @ModelAttribute("monthForAccountRetensionData")
    public JSONObject getMonthForAccountRetensionData() {
        return adminDashboardService.getMonthForAccountRetensionData();
    }

    @ModelAttribute("getTwoLastMonth")
    public List<String> getTwoLastMonth() {
        return adminDashboardService.getTwoLastMonth();
    }

    @ModelAttribute("countOfgetMonthDataForAccountRetensionData")
    public Integer countOfgetMonthDataForAccountRetensionData() {
        return adminDashboardService.countOfgetMonthDataForAccountRetensionData();
    }

    @ModelAttribute("percentOfAllPostsAboutSixMonth")
    public String percentOfAllPostsAboutSixMonth() {
        return adminDashboardService.percentOfAllPostsAboutSixMonth();
    }

    @ModelAttribute("getAllCommentsAsString")
    public String getAllCommentsAsString() {
        return adminDashboardService.getAllCommentsAsString();
    }

    @ModelAttribute("getPublicDaysAndData")
    public JSONObject getPublicDaysAndData() {
        return adminDashboardService.getPublicDaysAndData();
    }

    @ModelAttribute("getOldForUsers")
    public JSONObject getOldForUsers() {
        return adminDashboardService.getOldForUsers();
    }

    @ModelAttribute("getCountriesStatistic")
    public JSONArray getCountriesStatistic() {
        return adminDashboardService.getCountriesStatistic();
    }

    @ModelAttribute("getAllUsersAndComments")
    public JSONArray getAllUsersAndComments() {
        return adminDashboardService.getAllUsersAndComments();
    }

    @ModelAttribute("likesStatistic")
    public JSONArray likesStatistic() {
        return adminDashboardService.likesStatistic();
    }

    @ModelAttribute("getCountLikes")
    public Integer getCountLikes() {
        return adminDashboardService.getCountLikes();
    }

}
