package edu.wpi.teamR.requestdb;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AvailableSupplies {
    private String itemName, imageReference, description;
    private double itemPrice;
    private boolean isPaper, isPen, isOrganization, isComputerAccessory;

    public AvailableSupplies(String itemName, String imageReference, String description, double itemPrice, boolean isPaper, boolean isPen, boolean isOrganization, boolean isComputerAccessory) {
        this.itemName = itemName;
        this.imageReference = imageReference;
        this.description = description;
        this.itemPrice = itemPrice;
        this.isPaper = isPaper;
        this.isPen = isPen;
        this.isOrganization = isOrganization;
        this.isComputerAccessory = isComputerAccessory;
    }
}