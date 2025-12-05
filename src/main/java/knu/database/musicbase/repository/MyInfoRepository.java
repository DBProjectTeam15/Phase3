package knu.database.musicbase.repository;

import jakarta.servlet.http.HttpSession;
import knu.database.musicbase.dto.MyInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MyInfoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 일반 사용자 매퍼
    private final RowMapper<MyInfoDto> userRowMapper = (rs, rowNum) ->
            MyInfoDto.builder()
                    .id(rs.getLong("USER_ID"))
                    .username(rs.getString("USERNAME"))
                    .build();

    // 관리자 매퍼 (NICKNAME -> username)
    private final RowMapper<MyInfoDto> managerRowMapper = (rs, rowNum) ->
            MyInfoDto.builder()
                    .id(rs.getLong("MANAGER_ID"))
                    .username(rs.getString("NICKNAME"))
                    .build();

    // [세션에서 ID 추출하여 조회]
    public MyInfoDto getMyInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) return null; // 로그인 안 됨

        try {
            String sql = "SELECT USER_ID, USERNAME FROM USERS WHERE USER_ID = ?";
            return jdbcTemplate.queryForObject(sql, userRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            return null; // DB에 정보 없음
        }
    }

    // [세션에서 관리자 ID 추출하여 조회]
    public MyInfoDto getManagerInfo(HttpSession session) {
        Long managerId = (Long) session.getAttribute("managerId");

        if (managerId == null) return null; // 관리자 로그인 안 됨

        try {
            String sql = "SELECT MANAGER_ID, NICKNAME FROM MANAGERS WHERE MANAGER_ID = ?";
            return jdbcTemplate.queryForObject(sql, managerRowMapper, managerId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // [세션 ID로 정보 수정]
    public MyInfoDto updateMyInfo(String newUsername, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) return null; // 로그인 안 됨

        String updateSql = "UPDATE USERS SET USERNAME = ? WHERE USER_ID = ?";
        jdbcTemplate.update(updateSql, newUsername, userId);

        // 수정된 정보 반환 (재조회 로직 재사용)
        return getMyInfo(session);
    }
}