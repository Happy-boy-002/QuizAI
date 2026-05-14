package com.quizai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quizai.domain.R;
import com.quizai.domain.VO.WrongQuestionVO;
import com.quizai.domain.WrongQuestion;
import com.quizai.mapper.WrongQuestionMapper;
import com.quizai.service.WrongQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WrongQuestionServiceImpl extends ServiceImpl<WrongQuestionMapper, WrongQuestion> implements WrongQuestionService {

    @Autowired
    private WrongQuestionMapper wrongQuestionMapper;

    @Override
    public R selectUserWrongQuestion(Integer userId, String subject) {
        List<WrongQuestionVO> list = wrongQuestionMapper.selectWrongQuestionByUid(userId, subject);
        return R.success(list);
    }

    @Override
    public boolean removeWrongQuestion(WrongQuestion wrongQuestion) {
        QueryWrapper<WrongQuestion> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", wrongQuestion.getUserId());
        wrapper.eq("question_id", wrongQuestion.getQuestionId());
        return this.remove(wrapper);
    }

    @Override
    public R getRandomWrongQuestions(Integer userId, String subject, Integer count) {
        // 获取用户错题列表
        List<WrongQuestionVO> wrongList = wrongQuestionMapper.selectWrongQuestionByUid(userId, subject);

        if (wrongList.isEmpty()) {
            return R.success(Collections.emptyList());
        }

        // 随机打乱并取指定数量
        Collections.shuffle(wrongList);
        if (wrongList.size() > count) {
            wrongList = wrongList.subList(0, count);
        }

        // WrongQuestionVO 继承了 Question，直接返回
        return R.success(wrongList);
    }
}