package knu.database.musicbase.repository;

import jakarta.servlet.http.HttpSession;
import knu.database.musicbase.dto.CommentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper: DB 결과를 DTO로 변환
    private final RowMapper<CommentDto> commentMapper = (rs, rowNum) ->
            CommentDto.builder()
                    .commentedAt(rs.getTimestamp("CREATED_AT") != null ?
                            rs.getTimestamp("CREATED_AT").toLocalDateTime() : null)
                    .userId(rs.getLong("USER_ID"))
                    .playlistId(rs.getLong("PLAYLIST_ID"))
                    .content(rs.getString("CONTENT"))
                    .build();

    // 1. 내가 작성한 댓글 보기 (세션 활용)
    public List<CommentDto> getMyComments(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        // 로그인하지 않은 경우 빈 리스트 반환
        if (userId == null) {
            return List.of();
        }

        String sql = "SELECT * FROM PLAYLIST_COMMENT WHERE USER_ID = ? ORDER BY CREATED_AT DESC";
        return jdbcTemplate.query(sql, commentMapper, userId);
    }

    // 2. 특정 플레이리스트에 작성된 댓글 보기
    public List<CommentDto> getPlaylistComments(long playlistId) {
        String sql = "SELECT * FROM PLAYLIST_COMMENT WHERE PLAYLIST_ID = ? ORDER BY CREATED_AT ASC";
        return jdbcTemplate.query(sql, commentMapper, playlistId);
    }
}