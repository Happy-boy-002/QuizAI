package com.quizai.history.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("practice_record")
public class PracticeRecordEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String subject;
    private Long totalCount;
    private Long correctCount;
    private BigDecimal accuracyRate;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private String createTime;
}
