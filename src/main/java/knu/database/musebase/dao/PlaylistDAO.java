package knu.database.musebase.dao;

import knu.database.musebase.data.Playlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaylistDAO extends BasicDataAccessObjectImpl<Playlist, Long> {

    @Override
    public Playlist save(Playlist entity) throws SQLException {
        String sql = "INSERT INTO PLAYLISTS (Title, Is_collaborative, User_id) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"Playlist_id"})) {

            pstmt.setString(1, entity.getTitle());
            pstmt.setString(2, entity.getIsCollaborative());
            pstmt.setLong(3, entity.getUserId()); // DTO에 userId가 포함됨

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating playlist failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    return new Playlist(generatedId, entity.getTitle(), entity.getIsCollaborative(), entity.getUserId());
                } else {
                    throw new SQLException("Creating playlist failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Optional<Playlist> findById(Long id) {
        String sql = "SELECT * FROM PLAYLISTS WHERE Playlist_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPlaylist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Playlist> findAll() {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM PLAYLISTS";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                playlists.add(mapResultSetToPlaylist(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }


    /**
     * 쿼리 9.2 활용
     * 수정 사항 : ROWNUM을 사용하기 위해 ORDER BY를 포함한 서브쿼리로 감쌉니다.
     */
    public List<Playlist> top10BySongCountOrderByDESCAndSet() {
        List<Playlist> playlists = new ArrayList<>();

        String sql = "SELECT * FROM ( " +
                "  SELECT P.Playlist_id, P.Title, P.Is_collaborative, P.User_id, COUNT(C.Song_id) AS Song_Count " +
                "  FROM PLAYLISTS P " +
                "  LEFT JOIN CONSISTED_OF C ON P.Playlist_id = C.Playlist_id " +
                "  GROUP BY P.Playlist_id, P.Title, P.Is_collaborative, P.User_id " +
                "  ORDER BY Song_Count DESC " +
                ") P_SORTED " +
                "WHERE ROWNUM <= 10";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                playlists.add(mapResultSetToPlaylist(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    // 쿼리 10.3 활용
    public List<Playlist> findEditable(long userId) {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT Playlist_id, Title, Is_collaborative, User_id FROM PLAYLISTS WHERE User_id = ? " +
                "UNION " +
                "SELECT P.Playlist_id, P.Title, P.Is_collaborative, P.User_id " +
                "FROM PLAYLISTS P " +
                "JOIN EDITS E ON P.Playlist_id = E.Playlist_id " +
                "WHERE E.User_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public List<Playlist> findByUserId(long userId) {
        // ... (기존 코드와 동일, 변경 없음) ...
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM PLAYLISTS WHERE User_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public List<Playlist> findByUserIdAndIsShared(long userId) {
        // ... (기존 코드와 동일, 변경 없음) ...
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM PLAYLISTS WHERE User_id = ? AND Is_collaborative = 'true'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public List<Playlist> searchPlaylists(String title, boolean titleExact,
                                          Integer songCountMin, Integer songCountMax,
                                          Integer commentCountMin, Integer commentCountMax,
                                          String owner, boolean ownerExact,
                                          Integer lenMin, Integer lenMax) {

        List<Playlist> playlists = new ArrayList<>();
        List<Object> params = new ArrayList<>(); // PreparedStatement에 바인딩할 파라미터 리스트

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "  p.Playlist_id, p.Title, p.Is_collaborative, p.User_id, " +
                        "  COUNT(DISTINCT co.Song_id) AS SongCount, " +
                        "  COUNT(DISTINCT c.Comment_id) AS CommentCount, " +
                        "  NVL(SUM(s.Length), 0) AS TotalLength " +
                        "FROM PLAYLISTS p " +
                        "LEFT JOIN USERS u ON p.User_id = u.User_id " +
                        "LEFT JOIN CONSISTED_OF co ON p.Playlist_id = co.Playlist_id " +
                        "LEFT JOIN SONGS s ON co.Song_id = s.Song_id " +
                        "LEFT JOIN COMMENTS c ON p.Playlist_id = c.Playlist_id "
        );

        // 2. 동적 WHERE 절 (DBManager 로직)
        List<String> whereConditions = new ArrayList<>();
        if (title != null) {
            whereConditions.add("UPPER(p.Title) " + (titleExact ? "= ?" : "LIKE ?"));
            params.add(titleExact ? title.toUpperCase() : "%" + title.toUpperCase() + "%");
        }
        if (owner != null) {
            whereConditions.add("UPPER(u.Nickname) " + (ownerExact ? "= ?" : "LIKE ?"));
            params.add(ownerExact ? owner.toUpperCase() : "%" + owner.toUpperCase() + "%");
        }
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        // 3. GROUP BY 절 (mapResultSetToPlaylist를 위해 SELECT한 컬럼들 추가)
        sql.append(" GROUP BY p.Playlist_id, p.Title, p.Is_collaborative, p.User_id, u.Nickname ");

        // 4. 동적 HAVING 절 (DBManager 로직)
        List<String> havingConditions = new ArrayList<>();
        if (songCountMin != null) { havingConditions.add("COUNT(DISTINCT co.Song_id) >= ?"); params.add(songCountMin); }
        if (songCountMax != null) { havingConditions.add("COUNT(DISTINCT co.Song_id) <= ?"); params.add(songCountMax); }
        if (commentCountMin != null) { havingConditions.add("COUNT(DISTINCT c.Comment_id) >= ?"); params.add(commentCountMin); }
        if (commentCountMax != null) { havingConditions.add("COUNT(DISTINCT c.Comment_id) <= ?"); params.add(commentCountMax); }
        if (lenMin != null) { havingConditions.add("NVL(SUM(s.Length), 0) >= ?"); params.add(lenMin); }
        if (lenMax != null) { havingConditions.add("NVL(SUM(s.Length), 0) <= ?"); params.add(lenMax); }
        if (!havingConditions.isEmpty()) {
            sql.append(" HAVING ").append(String.join(" AND ", havingConditions));
        }

        // 5. 정렬 (DBManager 로직)
        sql.append(" ORDER BY p.Title ASC");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // 파라미터 바인딩
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(mapResultSetToPlaylist(rs)); // 기존 매퍼 재사용
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 처리
        }

        return playlists;
    }


    private Playlist mapResultSetToPlaylist(ResultSet rs) throws SQLException {
        return new Playlist(
                rs.getLong("Playlist_id"),
                rs.getString("Title"),
                rs.getString("Is_collaborative"),
                rs.getLong("User_id")
        );
    }
}