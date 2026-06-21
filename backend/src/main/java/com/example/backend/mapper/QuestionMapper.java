package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 根据知识点列表和难度推荐题目
     */
    @Select("<script>" +
            "SELECT * FROM question WHERE review_status = 'approved'" +
            "<if test='knowledgePoints != null and knowledgePoints.size > 0'>" +
            " AND knowledge_point IN " +
            "<foreach collection='knowledgePoints' item='kp' open='(' separator=',' close=')'>#{kp}</foreach>" +
            "</if>" +
            "<if test='difficulty != null'> AND difficulty = #{difficulty}</if>" +
            " ORDER BY RAND() LIMIT #{limit}" +
            "</script>")
    List<Question> findRecommendedQuestions(
            @Param("knowledgePoints") List<String> knowledgePoints,
            @Param("difficulty") String difficulty,
            @Param("limit") int limit);

    /**
     * 随机获取题目
     */
    @Select("SELECT * FROM question WHERE review_status = 'approved' ORDER BY RAND() LIMIT #{limit}")
    List<Question> findRandomQuestions(@Param("limit") int limit);
}
