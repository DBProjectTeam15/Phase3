package knu.database.musicbase.controller;

import jakarta.servlet.http.HttpSession;
import knu.database.musicbase.dto.SongRequestViewDto;
import knu.database.musicbase.enums.AuthType;
import knu.database.musicbase.repository.SongRequestRepository;
import knu.database.musicbase.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/song-requests")
@RestController
public class SongRequestController {

    @Autowired
    SongRequestRepository songRequestRepository;

    @Autowired
    private AuthService authService;

    // 1. 요청 검색
    @GetMapping("/search")
    public List<SongRequestViewDto> searchRequests(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String manager, // 담당자 ID 검색
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder
    ) {
        return songRequestRepository.searchRequests(title, artist, manager, sortBy, sortOrder);
    }

    // 2. 내가 관리하는 요청 검색 (관리자용)
    // 세션에서 내 정보를 가져오기 위해 HttpSession 추가
    @GetMapping("")
    public ResponseEntity<List<SongRequestViewDto>> getManagingSongRequests(HttpSession session) {
        if (authService.getAuthType(session) == AuthType.MANAGER) {
            return ResponseEntity.ok(songRequestRepository.getManagingSongRequests(session));
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());
        }
    }

    // 3. 요청 삭제
    // 삭제된 정보를 반환하기 위해 ResponseEntity<SongRequestViewDto> 사용
    @DeleteMapping("/{id}")
    public ResponseEntity<SongRequestViewDto> deleteSongRequests(@PathVariable long id) {
        SongRequestViewDto deletedRequest = songRequestRepository.deleteSongRequest(id);

        if (deletedRequest != null) {
            return ResponseEntity.ok(deletedRequest);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}