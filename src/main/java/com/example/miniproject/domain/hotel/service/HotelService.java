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
import com.example.miniproject.domain.member.entity.Member;
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
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class HotelService {

    private final MemberService memberService;
    private final HotelRepository hotelRepository;
    private final HotelThumbnailRepository hotelThumbnailRepository;
    private final ImageService imageService;
    private final FavoriteRepository favoriteRepository;

    public void create(String email, HotelDTO.Request request) {
        memberService.getMasterMemberOrThrow(email);
        hotelRepository.save(Hotel.saveAs(request));
    }

    public void create(String email, HotelDTO.Request request, MultipartFile[] files) {
        memberService.getMasterMemberOrThrow(email);
        Hotel savedHotel = hotelRepository.save(Hotel.saveAs(request));
        uploadThumbnail(email, savedHotel.getId(), files);
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
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        return HotelDTO.Response.of(hotel);
    }

    public void unregister(String email, Long hotelId) {
        memberService.getValidMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        hotel.delete();
    }

    public void updateData(String email, Long hotelId, HotelDTO.Request request) {
        memberService.getMasterMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        hotel.updateData(request);
    }

    public void uploadThumbnail(String email, Long hotelId, MultipartFile[] files) {
        memberService.getMasterMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        for (MultipartFile file : files) {
            try {
                String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                HotelThumbnail thumbnail = hotelThumbnailRepository.save(HotelThumbnail.saveAs(hotel, imgUrl));
                hotel.addThumbnail(thumbnail);
            } catch (IOException e) {
                throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION.getDescription());
            }
        }
    }

    public void updateThumbnail(String email, Long hotelId, Long thumbnailId, MultipartFile file) {
        memberService.getMasterMemberOrThrow(email);
        getVisibleHotelOrThrow(hotelId);
        hotelThumbnailRepository.findById(thumbnailId)
          .map(thumbnail -> {
              try {
                  String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                  thumbnail.updateThumbnail(imgUrl);
              } catch (IOException e) {
                  throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION.getDescription());
              }
              return thumbnail;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_IMAGE.getDescription()));
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

    public Hotel getVisibleHotelOrThrow(Long hotelId) {
        return hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL.getDescription()));
    }

}
