package knu.database.musicbase.repository;

import knu.database.musicbase.dto.ManagerLoginDto;
import knu.database.musicbase.dto.UserLoginDto;
import jakarta.servlet.http.HttpSession; // Spring Boot 3.x (2.x라면 javax.servlet...)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 일반 유저 로그인
    public boolean login(UserLoginDto userLoginDto, HttpSession session) {
        String sql = "SELECT User_id FROM USERS WHERE Email = ? AND Password = ?";
        try {
            // 이메일과 비밀번호가 일치하는 유저의 ID 조회
            Long userId = jdbcTemplate.queryForObject(sql, Long.class,
                    userLoginDto.getEmail(), userLoginDto.getPassword());

            // 로그인 성공 시 세션 저장
            session.setAttribute("id_type", "user");
            session.setAttribute("user_id", userId);
            return true;

        } catch (EmptyResultDataAccessException e) {
            return false; // 로그인 실패
        }
    }

    // 2. 관리자 로그인
    public boolean managerLogin(ManagerLoginDto managerLoginDto, HttpSession session) {
        String sql = "SELECT Manager_id FROM MANAGERS WHERE Manager_id = ? AND Password = ?";
        try {
            String managerId = jdbcTemplate.queryForObject(sql, String.class,
                    managerLoginDto.getId(), managerLoginDto.getPassword());

            // 로그인 성공 시 세션 저장
            session.setAttribute("id_type", "manager");
            session.setAttribute("manager_id", managerId);
            return true;

        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }


    // 3. 로그아웃 (공통)
    public boolean logout(HttpSession session) {
        if (session.getAttribute("id_type") == null) {
            return false; // 로그인 되어있지 않음
        }
        session.invalidate(); // 세션 날리기
        return true;
    }

    public boolean managerLogout(HttpSession session) {
        if (session.getAttribute("id_type") == null) {
            return false; // 로그인 되어있지 않음
        }
        session.invalidate(); // 세션 날리기
        return true;
    }

    // 4. 계정 삭제
    public boolean deleteAccount(HttpSession session) {
        String idType = (String) session.getAttribute("id_type");
        Long userId = (Long) session.getAttribute("user_id");

        // 로그인 안 했거나, 유저가 아닌 경우(관리자 등) 실패 처리
        if (idType == null || !"user".equals(idType) || userId == null) {
            return false;
        }

        // DB 삭제
        String sql = "DELETE FROM USERS WHERE User_id = ?";
        int updated = jdbcTemplate.update(sql, userId);

        if (updated > 0) {
            session.invalidate(); // 삭제 후 로그아웃 처리
            return true;
        }
        return false;
    }
}