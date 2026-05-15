package com.quizai.history.controller;

import com.quizai.domain.R;
import com.quizai.history.domain.dto.HistorySubmitRequest;
import com.quizai.history.service.PracticeHistoryService;
import com.quizai.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/history")
public class PracticeHistoryController {

    @Autowired
    private PracticeHistoryService practiceHistoryService;

    @PostMapping("submit")
    public R submit(@RequestBody HistorySubmitRequest request) {
        Integer userId = SecurityUtils.getCurrentUserId().intValue();
        return practiceHistoryService.submitHistory(request, userId);
    }

    @GetMapping("list")
    public R list() {
        Integer userId = SecurityUtils.getCurrentUserId().intValue();
        return practiceHistoryService.getHistoryList(userId);
    }

    @GetMapping("detail/{recordId}")
    public R detail(@PathVariable Long recordId) {
        Integer userId = SecurityUtils.getCurrentUserId().intValue();
        return practiceHistoryService.getHistoryDetail(recordId, userId);
    }
}
