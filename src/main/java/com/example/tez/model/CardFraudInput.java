package com.example.tez.model;

import lombok.Data;

@Data
public class CardFraudInput {
    private double distanceFromHome;
    private double distanceFromLastTransaction;
    private double ratioToMedianPurchasePrice;
    private double repeatRetailer;
    private double usedChip;
    private double usedPinNumber;
    private double onlineOrder;

}
