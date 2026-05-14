package com.quizai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quizai.domain.Question;
import com.quizai.domain.R;
import com.quizai.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/question")
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("list")
    public R<Page<Question>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String subject,
            @RequestParam(defaultValue = "") String keyword) {

        Page<Question> pageParam = new Page<>(page, size);
        QueryWrapper<Question> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(subject)) {
            wrapper.eq("subject", subject);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like("content", keyword);
        }
        wrapper.orderByDesc("id");

        questionService.page(pageParam, wrapper);
        return R.success(pageParam);
    }

    @PostMapping("add")
    public R add(@RequestBody Question question) {
        boolean flag = questionService.save(question);
        return flag ? R.success("添加成功") : R.error("添加失败");
    }

    @PostMapping("update")
    public R update(@RequestBody Question question) {
        boolean flag = questionService.updateById(question);
        return flag ? R.success("更新成功") : R.error("更新失败");
    }

    @PostMapping("delete")
    public R delete(@RequestBody Question question) {
        boolean flag = questionService.removeById(question.getId());
        return flag ? R.success("删除成功") : R.error("删除失败");
    }
}
