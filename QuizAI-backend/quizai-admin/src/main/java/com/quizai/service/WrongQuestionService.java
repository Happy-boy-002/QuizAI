package com.quizai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.quizai.domain.R;
import com.quizai.domain.WrongQuestion;

public interface WrongQuestionService extends IService<WrongQuestion> {
    R selectUserWrongQuestion(Integer userId, String subject);
    boolean removeWrongQuestion(WrongQuestion wrongQuestion);
    R getRandomWrongQuestions(Integer userId, String subject, Integer count);
}