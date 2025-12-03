package com.yongwang.controller.mini;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.AgriArticle;
import com.yongwang.core.entity.AgriQa;
import com.yongwang.core.entity.AgriTask;
import com.yongwang.core.entity.SolarTerm;
import com.yongwang.service.agri.AgriArticleService;
import com.yongwang.service.agri.AgriQaService;
import com.yongwang.service.agri.AgriTaskService;
import com.yongwang.service.agri.SolarTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序农技服务接口
 */
@Tag(name = "小程序-农技服务")
@RestController
@RequestMapping("/mini/agri")
@RequiredArgsConstructor
public class MiniAgriController {

    private final AgriArticleService agriArticleService;
    private final AgriQaService agriQaService;
    private final AgriTaskService agriTaskService;
    private final SolarTermService solarTermService;

    // ========== 农技文章 ==========

    @Operation(summary = "获取文章列表")
    @GetMapping("/article/list")
    public Result<Page<AgriArticle>> articleList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryUid) {
        return Result.success(agriArticleService.pagePublished(current, size, categoryUid));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/article/{uid}")
    public Result<AgriArticle> articleDetail(@PathVariable String uid) {
        // 增加阅读量
        agriArticleService.incrementViewCount(uid);
        return Result.success(agriArticleService.getByUid(uid));
    }

    @Operation(summary = "获取热门文章")
    @GetMapping("/article/hot")
    public Result<List<AgriArticle>> hotArticles(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(agriArticleService.listHot(limit));
    }

    @Operation(summary = "获取推荐文章")
    @GetMapping("/article/recommend")
    public Result<List<AgriArticle>> recommendArticles(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(agriArticleService.listRecommend(limit));
    }

    // ========== 农技问答 ==========

    @Operation(summary = "获取问答列表")
    @GetMapping("/qa/list")
    public Result<Page<AgriQa>> qaList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(agriQaService.pagePublished(current, size));
    }

    @Operation(summary = "获取问答详情")
    @GetMapping("/qa/{uid}")
    public Result<AgriQa> qaDetail(@PathVariable String uid) {
        return Result.success(agriQaService.getByUid(uid));
    }

    @Operation(summary = "提交问题")
    @PostMapping("/qa/ask")
    public Result<AgriQa> askQuestion(@RequestBody AgriQa qa) {
        return Result.success(agriQaService.create(qa));
    }

    @Operation(summary = "获取我的问题列表")
    @GetMapping("/qa/my")
    public Result<List<AgriQa>> myQuestions(@RequestParam String userUid) {
        return Result.success(agriQaService.listByUserUid(userUid));
    }

    @Operation(summary = "获取热门问答")
    @GetMapping("/qa/hot")
    public Result<List<AgriQa>> hotQa(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(agriQaService.listHot(limit));
    }

    // ========== 农事日历 ==========

    @Operation(summary = "获取月度农事任务")
    @GetMapping("/calendar/tasks")
    public Result<List<AgriTask>> getTasksByMonth(@RequestParam int month) {
        return Result.success(agriTaskService.listByMonth(month));
    }

    @Operation(summary = "获取指定日期的农事任务")
    @GetMapping("/calendar/tasks/date")
    public Result<List<AgriTask>> getTasksByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(agriTaskService.listByDate(date));
    }

    @Operation(summary = "获取节气信息")
    @GetMapping("/calendar/solar-terms")
    public Result<List<SolarTerm>> getSolarTerms(
            @RequestParam int year,
            @RequestParam int month) {
        return Result.success(solarTermService.listByYearMonth(year, month));
    }

    @Operation(summary = "获取当前节气")
    @GetMapping("/calendar/current-solar-term")
    public Result<SolarTerm> getCurrentSolarTerm() {
        return Result.success(solarTermService.getCurrentSolarTerm(LocalDate.now()));
    }

    @Operation(summary = "获取下一个节气")
    @GetMapping("/calendar/next-solar-term")
    public Result<SolarTerm> getNextSolarTerm() {
        return Result.success(solarTermService.getNextSolarTerm(LocalDate.now()));
    }

    @Operation(summary = "获取月度概览")
    @GetMapping("/calendar/overview")
    public Result<Map<String, Object>> getMonthOverview(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> overview = new HashMap<>();
        // 获取当月节气
        overview.put("solarTerms", solarTermService.listByYearMonth(year, month));
        // 获取当月任务
        overview.put("tasks", agriTaskService.listByMonth(month));
        // 获取有任务的日期列表
        overview.put("taskDates", agriTaskService.listTaskDatesInMonth(year, month));
        return Result.success(overview);
    }
}
