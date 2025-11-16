package knu.database.musebase.dao;

import knu.database.musebase.data.Artist;

import knu.database.musebase.data.Song;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CREATE TABLE ARTISTS (
 * Artist_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 * Name VARCHAR2(30) NOT NULL,
 * Gender VARCHAR2(10)
 * );
 */
@Slf4j
public class ArtistDAO extends BasicDataAccessObjectImpl<Artist, Long> {

    @Override
    public Artist save(Artist entity) {
        String sql = "INSERT INTO ARTISTS (Name, Gender) VALUES (?, ?)";
        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, entity.getName()); // Name
                pstmt.setString(2, entity.getGender()); // Gender

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating artist failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1); // 생성된 Artist_id
                        connection.commit();

                        // (가정) ID가 포함된 새 객체 반환
                        return new Artist(id, entity.getName(), entity.getGender());
                    } else {
                        throw new SQLException("Creating artist failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("Error saving artist: " + ex.getMessage(), ex);
            if (connection != null) {
                try {
                    connection.rollback();
                    log.info("Transaction rolled back for artist save.");
                } catch (SQLException e) {
                    log.error("Error during transaction rollback: " + e.getMessage(), e);
                }
            }
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    log.error("Error closing connection: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public Optional<Artist> findById(Long id) {
        String sql = "SELECT * FROM ARTISTS WHERE Artist_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToArtist(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException ex) {
            log.error("Error finding artist by id {}: {}", id, ex.getMessage(), ex);
            return Optional.empty();
        }
    }



    @Override
    public List<Artist> findAll() {
        String sql = "SELECT * FROM ARTISTS";
        List<Artist> artists = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                artists.add(mapToArtist(rs));
            }

        } catch (SQLException ex) {
            log.error("Error finding all artists: " + ex.getMessage(), ex);
        }
        return artists;
    }

    public long deleteById(long id) {
        String sql = "DELETE FROM ARTISTS WHERE Artist_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            // executeUpdate()는 영향을 받은 행의 수를 반환합니다.
            // ID로 삭제하는 경우 0 (삭제 실패) 또는 1 (삭제 성공)이 됩니다.
            return pstmt.executeUpdate();

        } catch (SQLException ex) {
            log.error("Error deleting artist by id {}: {}", id, ex.getMessage(), ex);
            // 오류 발생 시 0을 반환
            return 0;
        }
    }

    /**
     * ResultSet -> Artist 객체 매핑 헬퍼
     */
    private Artist mapToArtist(ResultSet rs) throws SQLException {
        return new Artist(
                rs.getLong("Artist_id"),
                rs.getString("Name"),
                rs.getString("Gender")
        );
    }
}