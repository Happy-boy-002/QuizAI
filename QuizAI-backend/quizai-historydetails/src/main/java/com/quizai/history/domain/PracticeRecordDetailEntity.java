package com.quizai.history.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("practice_record_detail")
public class PracticeRecordDetailEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long practiceRecordId;
    private Long questionId;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private String createTime;
}
