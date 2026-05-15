package com.quizai.history.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class HistorySubmitRequest {
    private String subject;
    private Long totalCount;
    private Long correctCount;
    private List<QuestionAnswer> questions;

    @Data
    public static class QuestionAnswer {
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
