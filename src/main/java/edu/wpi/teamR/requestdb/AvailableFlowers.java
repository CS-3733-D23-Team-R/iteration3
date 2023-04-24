package edu.wpi.teamR.requestdb;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AvailableFlowers {
    private String itemName, imageReference, description;
    private double itemPrice;
    private boolean isBouqet, hasCard;

    public AvailableFlowers(String itemName, String imageReference, String description, double itemPrice, boolean isBouqet, boolean hasCard) {
        this.itemName = itemName;
        this.imageReference = imageReference;
        this.description = description;
        this.itemPrice = itemPrice;
        this.isBouqet = isBouqet;
        this.hasCard = hasCard;
    }
}