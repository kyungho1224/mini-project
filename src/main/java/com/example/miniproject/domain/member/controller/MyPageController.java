package com.example.miniproject.domain.member.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/my-info")
    public ApiResponse<MemberDTO.MyPageResponse> getProfile(Principal principal) {
        String email = principal.getName();
        MemberDTO.MyPageResponse myPage = myPageService.getMyPageInfo(email);
        return ApiResponse.ok(HttpStatus.OK.value(), myPage);
    }

}
