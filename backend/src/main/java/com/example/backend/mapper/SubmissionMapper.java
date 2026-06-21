package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {

    @Select("SELECT * FROM submission WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Submission> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM submission WHERE question_id = #{questionId} ORDER BY created_at DESC")
    List<Submission> findByQuestionId(@Param("questionId") Long questionId);

    @Select("SELECT * FROM submission WHERE user_id = #{userId} AND question_id = #{questionId} ORDER BY created_at DESC")
    List<Submission> findByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Select("SELECT s.*, u.username FROM submission s LEFT JOIN user u ON s.user_id = u.id ORDER BY s.created_at DESC")
    List<Map<String, Object>> findAllWithUsername();

    /** 排行榜：按通过数降序（排除管理员） */
    @Select("SELECT u.id, u.username, u.nickname, " +
            "COUNT(CASE WHEN s.result = 'accepted' THEN 1 END) AS accepted_count, " +
            "COUNT(s.id) AS total_count " +
            "FROM user u LEFT JOIN submission s ON u.id = s.user_id " +
            "WHERE u.role = 'user' " +
            "GROUP BY u.id, u.username, u.nickname " +
            "ORDER BY accepted_count DESC, total_count ASC LIMIT 50")
    List<Map<String, Object>> leaderboard();

    /** 错题本：用户做错的题目 */
    @Select("SELECT s.*, q.title AS question_title FROM submission s " +
            "LEFT JOIN question q ON s.question_id = q.id " +
            "WHERE s.user_id = #{userId} AND s.result != 'accepted' " +
            "ORDER BY s.created_at DESC")
    List<Map<String, Object>> findWrongByUserId(@Param("userId") Long userId);

    /** 今日打卡数统计 */
    @Select("SELECT COUNT(*) FROM submission WHERE user_id = #{userId} " +
            "AND DATE(created_at) = CURDATE()")
    int todaySubmissionCount(@Param("userId") Long userId);
}
