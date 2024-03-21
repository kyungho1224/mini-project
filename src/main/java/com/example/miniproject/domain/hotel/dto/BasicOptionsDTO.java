package com.example.miniproject.domain.hotel.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BasicOptionsDTO {

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

//    public static BasicOptionsDTO of(BasicOptions entity) {
//        return new BasicOptionsDTO(
//          entity.isSwimmingPool(), entity.isBreakFast(), entity.isWirelessInternet(),
//          entity.isDryCleaning(), entity.isStorageService(), entity.isConvenienceStore(),
//          entity.isIroningTools(), entity.isWakeupCall(), entity.isMiniBar(),
//          entity.isShowerRoom(), entity.isAirConditioner(), entity.isTable(),
//          entity.isTv(), entity.isSafetyDepositBox()
//        );
//    }

}
