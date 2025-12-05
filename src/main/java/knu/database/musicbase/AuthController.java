package knu.database.musicbase.controller;

import knu.database.musicbase.dto.ManagerLoginDto;
import knu.database.musicbase.dto.UserLoginDto;
import knu.database.musicbase.repository.AuthRepository;
import jakarta.servlet.http.HttpSession; // Spring Boot 버전 확인 필요
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
public class AuthController { //

    @Autowired
    private AuthRepository authRepository;

    // 유저 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto, HttpSession session) {
        boolean success = authRepository.login(userLoginDto, session);
        if (success) {
            return ResponseEntity.ok("Login Success"); // 200
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed"); // 401
        }
    }

    // 관리자 로그인
    @PostMapping("/manager/login")
    public ResponseEntity<String> managerLogin(@RequestBody ManagerLoginDto managerLoginDto, HttpSession session) {
        boolean success = authRepository.managerLogin(managerLoginDto, session);
        if (success) {
            return ResponseEntity.ok("Manager Login Success"); // 200
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed"); // 401
        }
    }

    // 유저 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        boolean success = authRepository.logout(session);
        if (success) {
            return ResponseEntity.ok("Logout Success");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Logged In"); // 403
        }
    }

    // 관리자 로그아웃
    @PostMapping("/manager/logout")
    public ResponseEntity<String> managerLogout(HttpSession session) {
        boolean success = authRepository.managerLogout(session);
        if (success) {
            return ResponseEntity.ok("Logout Success");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Logged In");
        }
    }

    // 계정 삭제
    @DeleteMapping("/accounts")
    public ResponseEntity<String> deleteAccount(HttpSession session) {
        boolean success = authRepository.deleteAccount(session);
        if (success) {
            return ResponseEntity.ok("Account Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Logged In or Not a User"); // 403
        }
    }
}
