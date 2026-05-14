package com.quizai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quizai.domain.PracticeRecord;
import com.quizai.domain.R;
import com.quizai.domain.User;
import com.quizai.service.PracticeRecordService;
import com.quizai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping("api/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PracticeRecordService practiceRecordService;

    @GetMapping("list")
    public R<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("username", keyword).or().like("nickname", keyword));
        }
        wrapper.orderByDesc("id");

        userService.page(pageParam, wrapper);
        // 清空密码字段
        pageParam.getRecords().forEach(u -> u.setPassword(null));
        return R.success(pageParam);
    }

    @PostMapping("status")
    public R updateStatus(@RequestBody User user) {
        User u = new User();
        u.setId(user.getId());
        u.setStatus(user.getStatus());
        boolean flag = userService.updateById(u);
        return flag ? R.success("状态更新成功") : R.error("状态更新失败");
    }

    @GetMapping("stats")
    public R<Map<String, Object>> getUserStats(@RequestParam Long userId) {
        // 查询该用户所有练习记录
        QueryWrapper<PracticeRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByAsc("create_time");
        List<PracticeRecord> records = practiceRecordService.list(wrapper);

        Map<String, Object> result = new HashMap<>();

        if (records.isEmpty()) {
            result.put("totalPractice", 0);
            result.put("totalCorrect", 0);
            result.put("accuracy", 0);
            result.put("trendLabels", Collections.emptyList());
            result.put("trendData", Collections.emptyList());
            result.put("subjectStats", Collections.emptyList());
            return R.success(result);
        }

        // 累计统计
        int totalPractice = 0;
        int totalCorrect = 0;
        for (PracticeRecord r : records) {
            totalPractice += r.getTotalCount();
            totalCorrect += r.getCorrectCount();
        }
        int accuracy = totalPractice > 0
                ? BigDecimal.valueOf(totalCorrect).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalPractice), 0, RoundingMode.HALF_UP).intValue()
                : 0;

        // 近期正确率趋势（取最近10条）
        List<String> trendLabels = new ArrayList<>();
        List<Integer> trendData = new ArrayList<>();
        int size = records.size();
        int start = Math.max(0, size - 10);
        for (int i = start; i < size; i++) {
            PracticeRecord r = records.get(i);
            trendLabels.add(r.getCreateTime());
            int rate = r.getTotalCount() > 0
                    ? BigDecimal.valueOf(r.getCorrectCount()).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(r.getTotalCount()), 0, RoundingMode.HALF_UP).intValue()
                    : 0;
            trendData.add(rate);
        }

        // 各科目练习分布
        Map<String, Integer> subjectCountMap = new LinkedHashMap<>();
        for (PracticeRecord r : records) {
            subjectCountMap.merge(r.getSubject(), r.getTotalCount().intValue(), Integer::sum);
        }
        List<Map<String, Object>> subjectStats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : subjectCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            subjectStats.add(item);
        }

        result.put("totalPractice", totalPractice);
        result.put("totalCorrect", totalCorrect);
        result.put("accuracy", accuracy);
        result.put("trendLabels", trendLabels);
        result.put("trendData", trendData);
        result.put("subjectStats", subjectStats);
        return R.success(result);
    }
}
