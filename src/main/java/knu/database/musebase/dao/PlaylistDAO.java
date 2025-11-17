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

    private Playlist mapResultSetToPlaylist(ResultSet rs) throws SQLException {
        return new Playlist(
                rs.getLong("Playlist_id"),
                rs.getString("Title"),
                rs.getString("Is_collaborative"),
                rs.getLong("User_id")
        );
    }
}