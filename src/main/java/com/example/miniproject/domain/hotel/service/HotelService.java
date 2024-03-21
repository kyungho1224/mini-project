package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.constant.Nation;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.entity.Favorite;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.HotelThumbnail;
import com.example.miniproject.domain.hotel.repository.FavoriteRepository;
import com.example.miniproject.domain.hotel.repository.HotelRepository;
import com.example.miniproject.domain.hotel.repository.HotelThumbnailRepository;
import com.example.miniproject.domain.member.constant.MemberRole;
import com.example.miniproject.domain.member.constant.MemberStatus;
import com.example.miniproject.domain.member.entity.Member;
import com.example.miniproject.domain.member.repository.MemberRepository;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class HotelService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final HotelRepository hotelRepository;
    private final HotelThumbnailRepository hotelThumbnailRepository;
    private final ImageService imageService;
    private final FavoriteRepository favoriteRepository;

    public void create(String email, HotelDTO.Request request) {
        validMasterMemberOrThrow(email);
        hotelRepository.save(Hotel.saveAs(request));
    }

    public void create(String email, HotelDTO.Request request, MultipartFile[] files) {
        validMasterMemberOrThrow(email);
        Hotel savedHotel = hotelRepository.save(Hotel.saveAs(request));

        uploadThumbnail(email, savedHotel.getId(), files);
    }

    public void uploadThumbnail(String email, Long hotelId, MultipartFile[] files) {
        validMasterMemberOrThrow(email);
        hotelRepository.findById(hotelId)
          .map(hotel -> {
              Arrays.stream(files).peek(file -> {
                  try {
                      String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                      HotelThumbnail thumbnail = hotelThumbnailRepository.save(HotelThumbnail.saveAs(hotel, imgUrl));
                      hotel.addThumbnail(thumbnail);
                  } catch (IOException e) {
                      throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION, e.getMessage());
                  }
              }).collect(Collectors.toList());
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

    public Member getMemberOrThrow(String email) {
        return memberRepository.findByEmailAndStatus(email, MemberStatus.CERTIFICATED)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_MEMBER));
    }

    public Hotel getVisibleHotelOrThrow(Long hotelId) {
        return hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public Page<Hotel> findAllVisibleHotels(Pageable pageable) {
        return hotelRepository.findAllByRegisterStatus(pageable, RegisterStatus.VISIBLE);
    }

    public Page<HotelDTO.Response> findByNation(Nation nation, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findAllByNationAndRegisterStatus(pageable, nation, RegisterStatus.VISIBLE);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<Hotel> findHotelsByNameAndVisible(String name, Pageable pageable) {
        return hotelRepository.findByNameContainingAndRegisterStatus(name, RegisterStatus.VISIBLE, pageable);
    }

    public HotelDTO.Response findHotelById(Long hotelId) {
        return hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .map(HotelDTO.Response::of)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void unregister(String email, Long hotelId) {
        validMasterMemberOrThrow(email);
        hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .map(hotel -> {
              hotel.delete();
              return hotel;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void updateData(String email, Long hotelId, HotelDTO.Request request) {
        validMasterMemberOrThrow(email);
        hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .map(hotel -> {
              hotel.updateData(request);
              return hotel;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void updateThumbnail(String email, Long hotelId, Long thumbnailId, MultipartFile file) {
        validMasterMemberOrThrow(email);
        hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .map(hotel -> {
              hotelThumbnailRepository.findById(thumbnailId)
                .map(thumbnail -> {
                    try {
                        String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                        thumbnail.updateThumbnail(imgUrl);
                    } catch (IOException e) {
                        throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION, e.getMessage());
                    }
                    return thumbnail;
                })
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_IMAGE));
              return hotel;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void toggleFavorite(String email, Long hotelId) {
        Member validMember = memberService.getValidMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        favoriteRepository.findByMemberIdAndHotelId(validMember.getId(), hotel.getId())
          .ifPresentOrElse(hotel::removeFavorite, () -> {
              Favorite savedFavorite = favoriteRepository.save(Favorite.saveAs(validMember, hotel));
              hotel.addFavorite(savedFavorite);
          });
    }

}
