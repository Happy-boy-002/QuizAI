package com.quizai.history.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quizai.domain.R;
import com.quizai.history.domain.PracticeRecordDetailEntity;
import com.quizai.history.domain.PracticeRecordEntity;
import com.quizai.history.domain.dto.*;
import com.quizai.history.mapper.HistoryPracticeRecordMapper;
import com.quizai.history.mapper.PracticeRecordDetailMapper;
import com.quizai.history.service.PracticeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PracticeHistoryServiceImpl implements PracticeHistoryService {

    @Autowired
    private HistoryPracticeRecordMapper historyPracticeRecordMapper;

    @Autowired
    private PracticeRecordDetailMapper practiceRecordDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitHistory(HistorySubmitRequest request, Integer userId) {
        if (request.getTotalCount() == null || request.getTotalCount() == 0) {
            return R.error("总题数不能为0");
        }
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            return R.error("题目详情不能为空");
        }

        BigDecimal b1 = BigDecimal.valueOf(request.getCorrectCount());
        BigDecimal b2 = BigDecimal.valueOf(request.getTotalCount());
        BigDecimal rate = b1.multiply(BigDecimal.valueOf(100)).divide(b2, 0, RoundingMode.HALF_UP);

        PracticeRecordEntity record = new PracticeRecordEntity();
        record.setUserId(userId);
        record.setSubject(request.getSubject());
        record.setTotalCount(request.getTotalCount());
        record.setCorrectCount(request.getCorrectCount());
        record.setAccuracyRate(rate);
        historyPracticeRecordMapper.insert(record);

        for (HistorySubmitRequest.QuestionAnswer qa : request.getQuestions()) {
            PracticeRecordDetailEntity detail = new PracticeRecordDetailEntity();
            detail.setPracticeRecordId(record.getId().longValue());
            detail.setQuestionId(qa.getQuestionId());
            detail.setQuestionContent(qa.getQuestionContent());
            detail.setOptionA(qa.getOptionA());
            detail.setOptionB(qa.getOptionB());
            detail.setOptionC(qa.getOptionC());
            detail.setOptionD(qa.getOptionD());
            detail.setUserAnswer(qa.getUserAnswer());
            detail.setCorrectAnswer(qa.getCorrectAnswer());
            detail.setIsCorrect(qa.getIsCorrect());
            practiceRecordDetailMapper.insert(detail);
        }

        return R.success("提交成功");
    }

    @Override
    public R getHistoryList(Integer userId) {
        List<HistoryListVO> list = historyPracticeRecordMapper.selectHistoryList(userId);
        return R.success(list);
    }

    @Override
    public R getHistoryDetail(Long recordId, Integer userId) {
        PracticeRecordEntity record = historyPracticeRecordMapper.selectById(recordId);
        if (record == null) {
            return R.error("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            return R.error("无权访问此记录");
        }

        List<PracticeRecordDetailEntity> details = practiceRecordDetailMapper.selectByRecordId(recordId);

        HistoryDetailVO vo = new HistoryDetailVO();
        vo.setId(record.getId());
        vo.setSubject(record.getSubject());
        vo.setTotalCount(record.getTotalCount());
        vo.setCorrectCount(record.getCorrectCount());
        vo.setAccuracyRate(record.getAccuracyRate());
        vo.setCreateTime(record.getCreateTime());

        List<HistoryDetailVO.QuestionDetail> questionDetails = details.stream().map(d -> {
            HistoryDetailVO.QuestionDetail qd = new HistoryDetailVO.QuestionDetail();
            qd.setQuestionId(d.getQuestionId());
            qd.setQuestionContent(d.getQuestionContent());
            qd.setOptionA(d.getOptionA());
            qd.setOptionB(d.getOptionB());
            qd.setOptionC(d.getOptionC());
            qd.setOptionD(d.getOptionD());
            qd.setUserAnswer(d.getUserAnswer());
            qd.setCorrectAnswer(d.getCorrectAnswer());
            qd.setIsCorrect(d.getIsCorrect());
            return qd;
        }).collect(Collectors.toList());

        vo.setQuestions(questionDetails);
        return R.success(vo);
    }
}
