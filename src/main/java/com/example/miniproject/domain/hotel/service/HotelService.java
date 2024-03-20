package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.constant.HotelStatus;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RoomStatus;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.repository.HotelRepository;
import com.example.miniproject.domain.hotel.repository.RoomRepository;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class HotelService {

    private final MemberRepository memberRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ImageService imageService;

    public void create(String email, HotelDTO.Request request) {
        validMasterMemberOrThrow(email);
        hotelRepository.save(Hotel.saveAs(request));
    }

    public void create(String email, HotelDTO.Request request, MultipartFile file) {
        validMasterMemberOrThrow(email);
        Hotel savedHotel = hotelRepository.save(Hotel.saveAs(request));
        uploadThumbnail(email, savedHotel.getId(), file);
    }

    public void uploadThumbnail(String email, Long hotelId, MultipartFile file) {
        validMasterMemberOrThrow(email);
        hotelRepository.findById(hotelId)
          .map(hotel -> {
              String filename = String.format("hotel-%s", hotelId);
              try {
                  String upload = imageService.upload(file, filename);
                  hotel.updateThumbnail(upload);
              } catch (IOException e) {
                  throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION, e.getMessage());
              }
              return hotel;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void validMasterMemberOrThrow(String email) {
        Member member = memberRepository.findByEmailAndStatus(email, MemberStatus.CERTIFICATED)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER));
        if (member.getRole() != MemberRole.MASTER) {
            throw new ApiException(ApiErrorCode.NO_PERMISSION);
        }
    }

    public Page<Hotel> findAllVisibleHotels(Pageable pageable) {
        return hotelRepository.findAllByHotelStatus(pageable, HotelStatus.VISIBLE);
    }

    public Page<HotelDTO.Response> findByNation(Nation nation, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findAllByNationAndHotelStatus(pageable, nation, HotelStatus.VISIBLE);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<Hotel> findHotelsByNameAndVisible(String name, Pageable pageable) {
        return hotelRepository.findByNameContainingAndHotelStatus(name, HotelStatus.VISIBLE, pageable);
    }

    public List<RoomDTO.Response> findAllVisibleRoomsByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findAllByHotelIdAndRoomStatus(hotelId, RoomStatus.VISIBLE);
        return rooms.stream()
                .map(RoomDTO.Response::of)
                .collect(Collectors.toList());
    }

}
