package knu.database.musicbase.controller;

import knu.database.musicbase.dto.ProviderDto;
import knu.database.musicbase.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/providers")
@RestController
public class ProviderController {

    @Autowired
    ProviderRepository providerRepository;

    // 1. 제공원 검색
    @GetMapping("/search")
    public List<ProviderDto> searchProviders(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String link,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder
    ) {
        return providerRepository.searchProviders(name, link, sortBy, sortOrder);
    }

    // 제공원 정보 조회
    @GetMapping("/{id}")
    public ProviderDto getProviderDetails(@PathVariable Long id) {
        return providerRepository.getProviderDetails(id);
    }

    // 제공원 추가
    @PostMapping("")
    public ProviderDto addProvider(@RequestBody ProviderDto providerDto) {
        return providerRepository.addProvider(providerDto);
    }

    // 제공원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ProviderDto> deleteProvider(@PathVariable Long id) {
        ProviderDto deletedProvider = providerRepository.deleteProvider(id);

        if (deletedProvider != null) {
            // 성공 시: 200 OK
            return ResponseEntity.ok(deletedProvider);
        } else {
            // 실패 시: 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
}
