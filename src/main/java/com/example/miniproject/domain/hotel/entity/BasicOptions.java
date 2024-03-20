package com.example.miniproject.domain.hotel.entity;

import com.example.miniproject.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "options")
public class BasicOptions extends BaseEntity {

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private boolean swimmingPool = false;

    private boolean breakFast = false;

    private boolean wirelessInternet = false;

    private boolean dryCleaning = false;

    private boolean storageService = false;

    private boolean convenienceStore = false;

    private boolean ironingTools = false;

    private boolean wakeupCall = false;

    private boolean miniBar = false;

    private boolean showerRoom = false;

    private boolean airConditioner = false;

    private boolean table = false;

    private boolean tv = false;

    private boolean safetyDepositBox = false;

}
