package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.domain.hotel.dto.NoticeDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Notice;
import com.example.miniproject.domain.hotel.repository.NoticeRepository;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberService memberService;
    private final HotelService hotelService;

    public void create(String email, Long hotelId, NoticeDTO.Request request) {
        Member member = hotelService.getMemberOrThrow(email);
        Hotel hotel = hotelService.getVisibleHotelOrThrow(hotelId);
        Notice savedNotice = noticeRepository.save(Notice.saveAs(member, hotel, request));
        hotel.addNotice(savedNotice);
    }

    public void update(String email, Long hotelId, Long noticeId, NoticeDTO.Request request) {
        hotelService.validMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        Notice notice = getNoticeOrThrow(noticeId);
        notice.update(request);
    }

    public void delete(String email, Long hotelId, Long noticeId) {
        hotelService.validMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        Notice notice = getNoticeOrThrow(noticeId);
        notice.delete();
    }

    public void validNoticeOrThrow(Long noticeId) {
        noticeRepository.findById(noticeId)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_NOTICE));
    }

    public Notice getNoticeOrThrow(Long noticeId) {
        return noticeRepository.findById(noticeId)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_NOTICE));
    }

}
