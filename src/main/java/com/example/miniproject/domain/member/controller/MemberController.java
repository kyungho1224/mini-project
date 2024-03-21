package com.example.miniproject.domain.member.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResponse<MemberDTO.JoinResponse> join(
      @Validated
      @RequestBody MemberDTO.JoinRequest request
    ) throws Exception {
        return ApiResponse.ok(HttpStatus.CREATED.value(), memberService.create(request));
    }

    @GetMapping("/verify")
    public ApiResponse<Void> verify(
      @RequestParam String uuid
    ) {
        memberService.updateCertificate(uuid);
        return ApiResponse.ok(HttpStatus.OK.value());
    }

    @PostMapping("/login")
    public ApiResponse<MemberDTO.LoginResponse> login(
      @Validated
      @RequestBody MemberDTO.LoginRequest request
    ) {
        return ApiResponse.ok(memberService.login(request));
    }

    @PostMapping("/upload")
    public ApiResponse<Void> uploadProfile(
      Authentication authentication,
      @RequestParam(name = "file") MultipartFile file
    ) {
        memberService.uploadProfile(authentication.getName(), file);
        return ApiResponse.ok(HttpStatus.CREATED.value());
    }

    @GetMapping("/my-info")
    public ApiResponse<MemberDTO.MyPageResponse> getProfile(Principal principal) {
        String email = principal.getName();
        MemberDTO.MyPageResponse myPage = memberService.getMyPageInfo(email);
        return ApiResponse.ok(HttpStatus.OK.value(), myPage);
    }

    @PostMapping("/my-info")
    public ApiResponse<?> updateMemberInfo(@RequestBody MemberDTO.UpdateMemberRequest updateRequest) {
        memberService.updateMemberInfo(updateRequest);
        return ApiResponse.ok(HttpStatus.OK.value());
    }

}
