package com.quizai.history.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quizai.history.domain.PracticeRecordEntity;
import com.quizai.history.domain.dto.HistoryListVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface HistoryPracticeRecordMapper extends BaseMapper<PracticeRecordEntity> {

    @Select("SELECT id, subject, total_count, correct_count, accuracy_rate, create_time " +
            "FROM practice_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<HistoryListVO> selectHistoryList(@Param("userId") Integer userId);
}