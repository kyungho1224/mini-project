package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.entity.RoomThumbnail;
import com.example.miniproject.domain.hotel.repository.HotelRepository;
import com.example.miniproject.domain.hotel.repository.RoomRepository;
import com.example.miniproject.domain.hotel.repository.RoomThumbnailRepository;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
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
public class RoomService {

    private final HotelService hotelService;
    private final ImageService imageService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomThumbnailRepository roomThumbnailRepository;

    public Room create(String email, Long hotelId, RoomDTO.Request request) {
        hotelService.validMasterMemberOrThrow(email);
        return hotelRepository.findById(hotelId)
          .map(hotel -> {
              Room newRoom = Room.saveAs(hotel, request);
              roomRepository.save(newRoom);
              hotel.addRoom(newRoom);
              return newRoom;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void create(String email, Long hotelId, RoomDTO.Request request, MultipartFile[] files) {
        Room room = create(email, hotelId, request);
        uploadThumbnail(email, hotelId, room.getId(), files);
    }

    public void uploadThumbnail(String email, Long hotelId, Long roomId, MultipartFile[] files) {
        hotelService.validMasterMemberOrThrow(email);
        hotelRepository.findById(hotelId)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
        roomRepository.findById(roomId)
          .map(room -> {

              Arrays.stream(files).peek(file -> {
                  try {
                      String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                      RoomThumbnail thumbnail = roomThumbnailRepository.save(RoomThumbnail.saveAs(room, imgUrl));
                      room.addThumbnail(thumbnail);
                  } catch (IOException e) {
                      throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION, e.getMessage());
                  }
              }).collect(Collectors.toList());

              return room;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM));
    }

    public void unregister(String email, Long hotelId, Long roomId) {
        hotelService.validMasterMemberOrThrow(email);
        hotelRepository.findByIdAndRegisterStatus(hotelId, RegisterStatus.VISIBLE)
          .map(hotel -> roomRepository.findByIdAndHotelIdAndRegisterStatus(roomId, hotelId, RegisterStatus.VISIBLE)
            .map(room -> {
                room.delete();
                return room;
            })
            .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM)))
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_HOTEL));
    }

    public void updateData(String email, Long hotelId, Long roomId, RoomDTO.Request request) {
        hotelService.validMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        roomRepository.findByIdAndRegisterStatus(roomId, RegisterStatus.VISIBLE)
          .map(room -> {
              room.updateData(request);
              return room;
          })
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM));
    }

    public void updateThumbnail(String email, Long hotelId, Long roomId, Long thumbnailId, MultipartFile file) {
        hotelService.validMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        checkVisibleRoomOrThrow(roomId);

        roomThumbnailRepository.findById(thumbnailId)
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
    }

    public void checkVisibleRoomOrThrow(Long roomId) {
        roomRepository.findByIdAndRegisterStatus(roomId, RegisterStatus.VISIBLE)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM));
    }

}
