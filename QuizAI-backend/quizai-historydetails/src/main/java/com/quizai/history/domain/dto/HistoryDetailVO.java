package com.quizai.history.domain.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HistoryDetailVO {
    private Integer id;
    private String subject;
    private Long totalCount;
    private Long correctCount;
    private BigDecimal accuracyRate;
    private String createTime;
    private List<QuestionDetail> questions;

    @Data
    public static class QuestionDetail {
        private Long questionId;
        private String questionContent;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String userAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
    }
}
