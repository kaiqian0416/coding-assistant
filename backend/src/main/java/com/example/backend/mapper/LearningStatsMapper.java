package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.LearningStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LearningStatsMapper extends BaseMapper<LearningStats> {

    @Select("SELECT * FROM learning_stats WHERE user_id = #{userId}")
    List<LearningStats> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM learning_stats WHERE user_id = #{userId} AND knowledge_point = #{knowledgePoint}")
    Optional<LearningStats> findByUserIdAndKnowledgePoint(
            @Param("userId") Long userId,
            @Param("knowledgePoint") String knowledgePoint);

    /**
     * 找出掌握度最低的 N 个知识点（按正确率升序排列）
     */
    @Select("SELECT * FROM learning_stats WHERE user_id = #{userId} ORDER BY accuracy ASC LIMIT #{limit}")
    List<LearningStats> findWeakestKnowledgePoints(@Param("userId") Long userId, @Param("limit") int limit);
}
