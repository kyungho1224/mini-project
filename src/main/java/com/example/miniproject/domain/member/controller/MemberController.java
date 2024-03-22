package com.example.miniproject.domain.member.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<MemberDTO.JoinResponse>> join(
      @Validated
      @RequestBody MemberDTO.JoinRequest request
    ) throws Exception {
        return ResponseEntity
          .status(CREATED)
          .body(ApiResponse.ok(memberService.create(request)));
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verify(
      @RequestParam String uuid
    ) {
        memberService.updateCertificate(uuid);
        return ResponseEntity.status(ACCEPTED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberDTO.LoginResponse>> login(
      @Validated
      @RequestBody MemberDTO.LoginRequest request
    ) {
        return ResponseEntity.status(OK).body(ApiResponse.ok(memberService.login(request)));
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadProfile(
      Authentication authentication,
      @RequestParam(name = "file") MultipartFile file
    ) {
        memberService.uploadProfile(authentication.getName(), file);
        return ResponseEntity.status(OK).build();
    }

    @GetMapping("/my-info")
    public ResponseEntity<ApiResponse<MemberDTO.MyPageResponse>> getProfile(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.status(OK).body(ApiResponse.ok(memberService.getMyPageInfo(email)));
    }

    @PatchMapping("/my-info")
    public ResponseEntity<Void> updateMemberInfo(
      Authentication authentication,
      @RequestBody MemberDTO.UpdateMemberRequest updateRequest
    ) {
        memberService.updateMemberInfo(authentication.getName(), updateRequest);
        return ResponseEntity.status(OK).build();

    }

}
