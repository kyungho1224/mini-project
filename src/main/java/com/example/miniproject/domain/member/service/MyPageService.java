package com.example.miniproject.domain.member.service;

import com.example.miniproject.domain.member.dto.MemberDTO;
import com.example.miniproject.domain.member.repository.MyPageRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MyPageService {

    private final MyPageRepository myPageRepository;

    public MemberDTO.MyPageResponse getMyPageInfo(String email) {
        return myPageRepository.findByEmail(email)
                .map(MemberDTO.MyPageResponse::of)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER));
    }

}
