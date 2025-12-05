package knu.database.musicbase.controller;

import jakarta.servlet.http.HttpSession;
import knu.database.musicbase.dto.MyInfoDto;
import knu.database.musicbase.dto.MyInfoUpdateDto;
import knu.database.musicbase.repository.MyInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MyInfoController {

    @Autowired
    MyInfoRepository myInfoRepository;

    // 내 정보 조회
    @GetMapping("/my")
    public ResponseEntity<MyInfoDto> getMyInfo(HttpSession session) {
        MyInfoDto myInfo = myInfoRepository.getMyInfo(session);

        if (myInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(myInfo);
    }

    // 매니저용 정보 조회
    @GetMapping("/manager/me")
    public ResponseEntity<MyInfoDto> getManagerInfo(HttpSession session) {
        MyInfoDto managerInfo = myInfoRepository.getManagerInfo(session);

        if (managerInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(managerInfo);
    }

    // 내 정보 수정
    @PatchMapping("/my")
    public ResponseEntity<MyInfoDto> updateMyInfo(@RequestBody MyInfoUpdateDto myInfoUpdateDto, HttpSession session) {
        String newUsername = myInfoUpdateDto.getUsername();

        MyInfoDto updatedInfo = myInfoRepository.updateMyInfo(newUsername, session);

        if (updatedInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(updatedInfo);
    }
}