package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Training;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 护理培训 Mapper
 */
public interface TrainingMapper extends BaseMapper<Training> {

    @Select("SELECT * FROM training WHERE id = #{id}")
    Training selectTrainingById(Long id);

    List<Training> selectTrainingListForStudent(@Param("studentId") Long studentId,
                                                 @Param("searchText") String searchText,
                                                 @Param("trainingType") String trainingType,
                                                 @Param("status") String status);
}

