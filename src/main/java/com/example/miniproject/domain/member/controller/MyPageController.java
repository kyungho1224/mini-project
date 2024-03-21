package com.example.miniproject.domain.member.controller;

import com.example.miniproject.common.dto.ApiResponse;
import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MyPageController {

    private final MemberService memberService;

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
