package knu.database.musicbase.controller;


import knu.database.musicbase.dto.SongDto;
import knu.database.musicbase.dto.SongViewDto;
import knu.database.musicbase.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/songs")
@RestController
public class SongController {

    @Autowired
    SongRepository songRepository;

    @GetMapping("/search")
    public List<SongDto> searchSongs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "false") boolean exactTitle,
            @RequestParam(required = false) String artistName,
            @RequestParam(required = false, defaultValue = "false") boolean exactArtist,
            @RequestParam(required = false) Integer minTime,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(required = false) String songName,
            @RequestParam(required = false, defaultValue = "false") boolean exactsong,
            @RequestParam(required = false) String minDate,
            @RequestParam(required = false) String maxDate,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder
    ) {
        // 실제 DB 조회 로직 호출
        return songRepository.searchSongs(
                title, exactTitle,
                artistName, exactArtist,
                minTime, maxTime,
                songName, exactsong,
                minDate, maxDate,
                sortBy, sortOrder
        );
    }


    // 전체 음원 조회?
    @GetMapping("")
    public List<SongDto> getAllSongs(){
        return songRepository.getAllSongs();
    }

    // 제공원 정보 조회
    @GetMapping("/{id}")
    public SongDto getSongDetails(@PathVariable Long id) {
        return songRepository.getSongDetails(id);
    }

    // 제공원 추가
    @PostMapping("")
    public SongDto addSong(@RequestBody SongDto songDto) {
        return songRepository.addSong(songDto);
    }

    // 제공원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<SongDto> deleteSong(@PathVariable Long id) {
        SongDto deletedSong = songRepository.deleteSong(id);

        if (deletedSong != null) {
            // 성공 시: 200 OK
            return ResponseEntity.ok(deletedSong);
        } else {
            // 실패 시: 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
    
}
