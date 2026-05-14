package com.quizai.controller;

import com.quizai.domain.R;
import com.quizai.domain.WrongQuestion;
import com.quizai.security.SecurityUtils;
import com.quizai.service.WrongQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/wrong")
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;

    @PostMapping("add")
    public R addWrongQuestion(@RequestBody WrongQuestion wrongQuestion) {
        wrongQuestion.setUserId(SecurityUtils.getCurrentUserId().intValue());
        boolean flag = wrongQuestionService.save(wrongQuestion);
        return flag ? R.success("添加成功") : R.error("添加失败");
    }

    @GetMapping("list")
    public R selectUserWrongQuestion(@RequestParam(defaultValue = "全部") String subject) {
        return wrongQuestionService.selectUserWrongQuestion(SecurityUtils.getCurrentUserId().intValue(), subject);
    }

    @PostMapping("remove")
    public R removeWrongQuestion(@RequestBody WrongQuestion wrongQuestion) {
        wrongQuestion.setUserId(SecurityUtils.getCurrentUserId().intValue());
        boolean flag = wrongQuestionService.removeWrongQuestion(wrongQuestion);
        return flag ? R.success("移除成功") : R.error("移除失败");
    }

    @GetMapping("random")
    public R getRandomWrongQuestions(
            @RequestParam(defaultValue = "全部") String subject,
            @RequestParam(defaultValue = "10") Integer count) {
        return wrongQuestionService.getRandomWrongQuestions(
                SecurityUtils.getCurrentUserId().intValue(), subject, count);
    }
}
