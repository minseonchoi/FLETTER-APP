package com.choiminseon.fletterapp.model;

import java.util.ArrayList;

public class ProfileRes {
    public ArrayList<Item> user;
    public String result;

    public static class Item {
        public String email;
        public String userName;
        public String phone;
        public String createdAt;
    }
}
