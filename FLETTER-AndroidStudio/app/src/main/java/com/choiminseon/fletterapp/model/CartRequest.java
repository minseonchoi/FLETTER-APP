package com.choiminseon.fletterapp.model;

import java.util.ArrayList;


public class CartRequest {
    int packageId;
    int sizeId;
    int quantity;
    ArrayList<Integer> flowerIdList;

    public CartRequest(int packageId, int sizeId, int quantity, ArrayList<Integer> flowerIdList) {
        this.packageId = packageId;
        this.sizeId = sizeId;
        this.quantity = quantity;
        this.flowerIdList = flowerIdList;
    }
}
