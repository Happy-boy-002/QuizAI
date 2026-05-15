package com.quizai.history.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HistoryListVO {
    private Integer id;
    private String subject;
    private Long totalCount;
    private Long correctCount;
    private BigDecimal accuracyRate;
    private String createTime;
}
