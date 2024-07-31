package com.choiminseon.fletterapp.model;

public class OrderRequset {
    public int cartId;
    public int totalPrice;
    public String paymentMethod;
    public String comment;
    public String address;
    public String status;
    public String receive;
    public String reservationDate;

    public OrderRequset(int cartId, int totalPrice, String paymentMethod, String comment, String address, String status, String receive, String reservationDate) {
        this.cartId = cartId;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.comment = comment;
        this.address = address;
        this.status = status;
        this.receive = receive;
        this.reservationDate = reservationDate;
    }
}
