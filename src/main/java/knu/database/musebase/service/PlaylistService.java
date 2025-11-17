package knu.database.musebase.service;

import knu.database.musebase.dao.PlaylistDAO;
import knu.database.musebase.data.Playlist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 현재는 상태를 저장하기 위한 기능만을 수행합니다.
 */
@RequiredArgsConstructor
public class PlaylistService {
    @Getter
    private List<Playlist> playlists = List.of();

    @Getter
    private String playlistName = "";

    private final PlaylistDAO playlistDAO;

    public void updateForMainPage() {
        playlistName = "음원수가 많은 최상위 10개 플레이리스트";
        playlists = playlistDAO.top10BySongCountOrderByDESCAndSet();
    }

    public void updateEditablePlaylist(long userId) {
        playlistName = "편집 가능한 플레이리스트";
        playlists = playlistDAO.findEditable(userId);
    }

    public void updateMyPlaylist(long userId) {
        playlistName = "내가 소유한 플레이리스트";
        playlists = playlistDAO.findByUserId(userId);
    }

    public void updateMySharedPlaylist(long userId) {
        playlistName = "내가 소유한 공유 플레이리스트";
        playlists = playlistDAO.findByUserIdAndIsShared(userId);
    }
}
