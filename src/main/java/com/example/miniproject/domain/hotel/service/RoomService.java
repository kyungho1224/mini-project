package com.example.miniproject.domain.hotel.service;

import com.example.miniproject.common.service.ImageService;
import com.example.miniproject.domain.hotel.constant.ActiveStatus;
import com.example.miniproject.domain.hotel.constant.RegisterStatus;
import com.example.miniproject.domain.hotel.dto.RoomDTO;
import com.example.miniproject.domain.hotel.entity.Hotel;
import com.example.miniproject.domain.hotel.entity.Room;
import com.example.miniproject.domain.hotel.entity.RoomThumbnail;
import com.example.miniproject.domain.hotel.repository.RoomRepository;
import com.example.miniproject.domain.hotel.repository.RoomThumbnailRepository;
import com.example.miniproject.domain.member.service.MemberService;
import com.example.miniproject.exception.ApiErrorCode;
import com.example.miniproject.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final MemberService memberService;
    private final HotelService hotelService;
    private final ImageService imageService;
    private final RoomRepository roomRepository;
    private final RoomThumbnailRepository roomThumbnailRepository;

    public Room create(String email, Long hotelId, RoomDTO.Request request) {
        memberService.getMasterMemberOrThrow(email);

        Hotel hotel = hotelService.getVisibleHotelOrThrow(hotelId);

        Room savedRoom = roomRepository.save(Room.saveAs(hotel, request));
        hotel.addRoom(savedRoom);

        return savedRoom;
    }

    public void create(String email, Long hotelId, RoomDTO.Request request, MultipartFile[] files) {
        Room room = create(email, hotelId, request);
        uploadThumbnail(email, hotelId, room.getId(), files);
    }

    public void uploadThumbnail(String email, Long hotelId, Long roomId, MultipartFile[] files) {
        memberService.getMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);

        Room room = getVisibleRoomOrThrow(roomId);
        for (MultipartFile file : files) {
            try {
                String imgUrl = imageService.upload(file, UUID.randomUUID().toString());
                RoomThumbnail thumbnail = roomThumbnailRepository.save(RoomThumbnail.saveAs(room, imgUrl));
                room.addThumbnail(thumbnail);
            } catch (IOException e) {
                throw new ApiException(ApiErrorCode.FIREBASE_EXCEPTION.getDescription());
            }
        }
    }

    public void unregister(String email, Long hotelId, Long roomId) {
        memberService.getMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        Room room = getVisibleRoomOrThrow(roomId);
        room.delete();
    }

    public void updateData(String email, Long hotelId, Long roomId, RoomDTO.Request request) {
        memberService.getMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        Room room = getVisibleRoomOrThrow(roomId);
        room.updateData(request);
    }

    public void updateThumbnail(String email, Long hotelId, Long roomId, Long thumbnailId, MultipartFile file) {
        memberService.getMasterMemberOrThrow(email);
        hotelService.getVisibleHotelOrThrow(hotelId);
        checkVisibleRoomOrThrow(roomId);
        roomThumbnailRepository.findById(thumbnailId)
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

    public Room getVisibleRoomOrThrow(Long roomId) {
        return roomRepository.findByIdAndRegisterStatus(roomId, RegisterStatus.VISIBLE)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM.getDescription()));
    }

    public void checkVisibleRoomOrThrow(Long roomId) {
        roomRepository.findByIdAndRegisterStatus(roomId, RegisterStatus.VISIBLE)
          .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_ROOM.getDescription()));
    }

    public Room getVisibleAndActiveRoomOrThrow(Long roomId) {
        return roomRepository.findByIdAndRegisterStatusAndActiveStatus(
          roomId, RegisterStatus.VISIBLE, ActiveStatus.ACTIVE
        ).orElseThrow(() -> new ApiException(ApiErrorCode.NOT_AVAILABLE_ROOM.getDescription()));
    }

}
