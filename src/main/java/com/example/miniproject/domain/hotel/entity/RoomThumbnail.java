package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "room_thumbnails")
public class RoomThumbnail extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String imgUrl;

    public static RoomThumbnail saveAs(Room room, String imgUrl) {
        return new RoomThumbnail(room, imgUrl);
    }

}
