package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.constant.*;
import com.example.miniproject.domain.hotel.dto.HotelDTO;
import com.example.miniproject.domain.hotel.dto.SearchRequest;
import com.example.miniproject.domain.hotel.dto.ThumbnailDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

    public HotelDTO.Response create(String email, HotelDTO.Request request) {
        memberService.getMasterMemberOrThrow(email);
        Hotel savedHotel = hotelRepository.save(Hotel.saveAs(request));
        return HotelDTO.Response.of(savedHotel);
    }

    public HotelDTO.Response create(String email, HotelDTO.Request request, MultipartFile[] files) {
        memberService.getMasterMemberOrThrow(email);
        Hotel savedHotel = hotelRepository.save(Hotel.saveAs(request));
        uploadThumbnail(email, savedHotel.getId(), files);
        return HotelDTO.Response.of(savedHotel);
    }

    public Page<HotelDTO.Response> searchCollection(SearchRequest request) {
        SearchType type = request.getSearchType();
        if (type == SearchType.ALL_AUTHENTICATION) {
            return findAllVisibleHotelsWithFavorites(request.getEmail(), request.getPageable());
        } else if (type == SearchType.ALL_ANONYMOUS) {
            return findAllVisibleHotels(request.getPageable());
        } else if (type == SearchType.NATION_AUTHENTICATION) {
            return findByNationWithFavorite(request.getEmail(), request.getNation(), request.getPageable());
        } else if (type == SearchType.NATION_ANONYMOUS) {
            return findByNation(request.getNation(), request.getPageable());
        } else if (type == SearchType.NAME_AUTHENTICATION) {
            return findHotelsByNameAndVisibleWithFavorite(
              request.getEmail(), request.getName(), request.getPageable()
            );
        } else if (type == SearchType.NAME_ANONYMOUS) {
            return findHotelsByNameAndVisible(request.getName(), request.getPageable());
        } else if (type == SearchType.NAME_AND_NATION_AUTHENTICATION) {
            return findHotelsByNameAndNationAndVisibleWithFavorite(
              request.getEmail(), request.getName(), request.getNation(), request.getPageable()
            );
        } else if (type == SearchType.NAME_AND_NATION_ANONYMOUS) {
            return findHotelsByNameAndNationAndVisible(request.getName(), request.getNation(), request.getPageable());
        } else if (type == SearchType.SEARCH_AUTHENTICATION) {
            return findHotelsByNationAndTypeAndVisibleWithFavorite(
              request.getEmail(), request.getNation(),
              request.getRoomType(), request.getViewType(), request.getPageable()
            );
        } else if (type == SearchType.SEARCH_ANONYMOUS) {
            return findHotelsByNationAndTypeAndVisible(
              request.getNation(), request.getRoomType(), request.getViewType(), request.getPageable()
            );
        } else {
            return new PageImpl<>(List.of());
        }
    }

    public Page<HotelDTO.Response> findAllVisibleHotels(Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findAllByRegisterStatus(pageable, RegisterStatus.VISIBLE);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<HotelDTO.Response> findAllVisibleHotelsWithFavorites(String email, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findAllByRegisterStatus(pageable, RegisterStatus.VISIBLE);
        return getResponsesWithFavorite(email, pageable, hotels);
    }

    public Page<HotelDTO.Response> findByNation(Nation nation, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findAllByNationAndRegisterStatus(pageable, nation, RegisterStatus.VISIBLE);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<HotelDTO.Response> findByNationWithFavorite(String email, Nation nation, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findAllByNationAndRegisterStatus(pageable, nation, RegisterStatus.VISIBLE);
        return getResponsesWithFavorite(email, pageable, hotels);
    }

    public Page<HotelDTO.Response> findHotelsByNameAndVisible(String name, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findByNameContainingAndRegisterStatus(name, RegisterStatus.VISIBLE, pageable);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<HotelDTO.Response> findHotelsByNameAndVisibleWithFavorite(String email, String name, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findByNameContainingAndRegisterStatus(name, RegisterStatus.VISIBLE, pageable);
        return getResponsesWithFavorite(email, pageable, hotels);
    }

    public Page<HotelDTO.Response> findHotelsByNameAndNationAndVisible(String name, Nation nation, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findByNameAndNationContainingAndRegisterStatus(name, nation, RegisterStatus.VISIBLE, pageable);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<HotelDTO.Response> findHotelsByNameAndNationAndVisibleWithFavorite(String email, String name, Nation nation, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findByNameAndNationContainingAndRegisterStatus(name, nation, RegisterStatus.VISIBLE, pageable);
        return getResponsesWithFavorite(email, pageable, hotels);
    }

    public Page<HotelDTO.Response> findHotelsByNationAndTypeAndVisible(Nation nation, RoomType roomType, ViewType viewType, Pageable pageable) {
        Page<Hotel> hotels = hotelRepository.findByNationAndRoomTypeAndViewTypeAndRegisterStatus(nation, roomType, viewType, RegisterStatus.VISIBLE, pageable);
        return hotels.map(HotelDTO.Response::of);
    }

    public Page<HotelDTO.Response> findHotelsByNationAndTypeAndVisibleWithFavorite(
      String email, Nation nation, RoomType roomType, ViewType viewType, Pageable pageable
    ) {
        Page<Hotel> hotels = hotelRepository.findByNationAndRoomTypeAndViewTypeAndRegisterStatus(
          nation, roomType, viewType, RegisterStatus.VISIBLE, pageable
        );
        return getResponsesWithFavorite(email, pageable, hotels);
    }

    private Page<HotelDTO.Response> getResponsesWithFavorite(String email, Pageable pageable, Page<Hotel> hotels) {
        Page<HotelDTO.Response> favoriteList = memberService.getMyFavoriteList(email, pageable);
        return hotels.map(hotel -> {
            HotelDTO.Response response = HotelDTO.Response.of(hotel);
            favoriteList.forEach(favorite -> {
                if (Objects.equals(hotel.getId(), favorite.getId())) {
                    response.updateFavorite(true);
                }
            });
            return response;
        });
    }

    public HotelDTO.Response findHotelById(Long hotelId) {
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        hotelRepository.save(hotel);
        return HotelDTO.Response.of(hotel);
    }

    public HotelDTO.Response findHotelByIdWithFavorite(String email, Long hotelId) {
        Member member = memberService.getValidMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        hotelRepository.save(hotel);
        HotelDTO.Response response = HotelDTO.Response.of(hotel);
        response.updateFavorite(favoriteRepository.existsByMemberIdAndHotelId(member.getId(), hotelId));
        return response;
    }

    public void unregister(String email, Long hotelId) {
        memberService.getValidMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        hotel.delete();
    }

    public Hotel updateData(String email, Long hotelId, HotelDTO.Request request) {
        memberService.getMasterMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        hotel.updateData(request);
        return hotelRepository.save(hotel);
    }

    public Hotel uploadThumbnail(String email, Long hotelId, MultipartFile[] files) {
        memberService.getMasterMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        for (MultipartFile file : files) {
            try {
                String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                HotelThumbnail thumbnail = hotelThumbnailRepository.save(HotelThumbnail.saveAs(hotel, imgUrl));
                hotel.addThumbnail(thumbnail);
                hotelRepository.save(hotel);
            } catch (IOException e) {
                throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION.getDescription());
            }
        }
        return hotel;
    }

    public ThumbnailDTO.HotelThumbnailsResponse updateThumbnail(String email, Long hotelId, Long thumbnailId, MultipartFile file) {
        memberService.getMasterMemberOrThrow(email);
        Hotel hotel = getVisibleHotelOrThrow(hotelId);
        HotelThumbnail hotelThumbnail = hotelThumbnailRepository.findById(thumbnailId)
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
        HotelThumbnail savedThumbnail = hotelThumbnailRepository.save(hotelThumbnail);
        hotelRepository.save(hotel);
        return ThumbnailDTO.HotelThumbnailsResponse.of(savedThumbnail);
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
