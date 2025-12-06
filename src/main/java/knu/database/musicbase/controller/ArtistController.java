package knu.database.musicbase.controller;


import knu.database.musicbase.dto.ArtistDto;
import knu.database.musicbase.repository.ArtistRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/artists")
@RestController
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;


    // 아티스트 검색
    @GetMapping("/search")
    public List<ArtistDto> searchArtists(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder
    ) {
        // Repository 호출
        return artistRepository.searchArtists(name, gender, role, sortBy, sortOrder);
    }

    @GetMapping("/{id}")
    public ArtistDto getArtistDetails(@PathVariable long id) {
        return artistRepository.getArtistDetails(id);
    }

    // 아티스트 생성
    @PostMapping("")
    public ArtistDto addArtist(@RequestBody ArtistDto artistDto) {
        return artistRepository.addArtist(artistDto);
    }

    
    // 아티스트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ArtistDto> deleteArtist(@PathVariable Long id) {
        ArtistDto deletedArtist = artistRepository.deleteArtist(id);

        if (deletedArtist != null) {
            // 성공 시: 200 OK와 함께 삭제된 정보 반환
            return ResponseEntity.ok(deletedArtist);
        } else {
            // 실패 시(대상이 없을 때): 404 Not Found 반환
            return ResponseEntity.notFound().build();
        }
    }

}
