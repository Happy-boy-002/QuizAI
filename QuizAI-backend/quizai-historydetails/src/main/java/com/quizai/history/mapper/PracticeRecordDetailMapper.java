package com.quizai.history.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quizai.history.domain.PracticeRecordDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface PracticeRecordDetailMapper extends BaseMapper<PracticeRecordDetailEntity> {

    @Select("SELECT * FROM practice_record_detail WHERE practice_record_id = #{recordId} ORDER BY id ASC")
    List<PracticeRecordDetailEntity> selectByRecordId(@Param("recordId") Long recordId);
}
