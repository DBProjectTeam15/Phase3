package knu.database.musicbase.controller;

import jakarta.servlet.http.HttpSession;
import knu.database.musicbase.dto.PlaylistDto;
import knu.database.musicbase.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/playlists")
@RestController
public class PlaylistController {

    @Autowired // 의존성 주입 추가
    private PlaylistRepository playlistRepository;

    // 음원 수가 많은 플리 10개 조회
    @GetMapping("/top10")
    public List<PlaylistDto> getTop10BySongCounts() {
        return playlistRepository.getTop10BySongCounts();
    }

    // 특정 플리 조회
    @GetMapping("/{id}")
    public PlaylistDto getPlaylistDetails(@PathVariable Long id) {
        return playlistRepository.getPlaylistDetails(id);
    }

    // 음악 포함 플리 조회 (sodId 오타 수정 -> songId)
    @GetMapping("/containing-song/{songId}")
    public List<PlaylistDto> getPlaylistBySong(@PathVariable Long songId) {
        return playlistRepository.getPlaylistBySong(songId);
    }

    // 내가 소유한 플리 조회 (세션은 Repository 내부에서 처리)
    @GetMapping("/my")
    public List<PlaylistDto> getMyPlaylists(HttpSession session) {
        return playlistRepository.getMyPlaylists(session);
    }

    // 공유된 플리 조회
    @GetMapping("/shared")
    public List<PlaylistDto> getSharedPlaylists() {
        return playlistRepository.getSharedPlaylists();
    }

    // 편집 가능한 플리 조회 (세션은 Repository 내부에서 처리)
    @GetMapping("/editable")
    public List<PlaylistDto> getEditablePlaylists(HttpSession session) {
        return playlistRepository.getEditablePlaylists(session);
    }

    // 플리 검색
    @GetMapping("/search")
    public List<PlaylistDto> searchPlaylists(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer songCount,      // 최소 수록곡 수
            @RequestParam(required = false) Integer commentCount,   // 최소 댓글 수
            @RequestParam(required = false) String owner,           // 소유자 닉네임
            @RequestParam(required = false) Integer totalLength,    // 최소 총 재생 시간(초)
            @RequestParam(required = false, defaultValue = "title") String sortBy, // 정렬 기준
            @RequestParam(required = false, defaultValue = "asc") String sortOrder // 정렬 순서
    ) {
        return playlistRepository.searchPlaylists(title, songCount, commentCount, owner, totalLength, sortBy, sortOrder);
    }
}