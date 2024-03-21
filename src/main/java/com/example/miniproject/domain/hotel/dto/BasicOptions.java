package com.example.miniproject.domain.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BasicOptions implements Serializable {

    private boolean swimmingPool;

    private boolean breakFast;

    private boolean wirelessInternet;

    private boolean dryCleaning;

    private boolean storageService;

    private boolean convenienceStore;

    private boolean ironingTools;

    private boolean wakeupCall;

    private boolean miniBar;

    private boolean showerRoom;

    private boolean airConditioner;

    private boolean table;

    private boolean tv;

    private boolean safetyDepositBox;

}
