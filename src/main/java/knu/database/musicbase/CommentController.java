package knu.database.musicbase.controller;

import jakarta.servlet.http.HttpSession;
import knu.database.musicbase.dto.CommentDto;
import knu.database.musicbase.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/comments")
@RestController
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    // 1. 내가 작성한 댓글 보기
    // GET /api/comments/my
    @GetMapping("/my")
    public List<CommentDto> getMyComments(HttpSession session) {
        return commentRepository.getMyComments(session);
    }

    // 2. 플레이리스트에 작성된 댓글 보기
    // GET /api/comments/playlist/{playlistId}
    @GetMapping("/playlist/{playlistId}")
    public List<CommentDto> getPlaylistComments(@PathVariable long playlistId) {
        return commentRepository.getPlaylistComments(playlistId);
    }
}