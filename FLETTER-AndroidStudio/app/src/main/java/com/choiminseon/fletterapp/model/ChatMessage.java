package com.choiminseon.fletterapp.model;

public class ChatMessage {
    private String message;
    private boolean isSentByUser;
    private String[] options;

    public ChatMessage(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
    }

    public ChatMessage(String message, boolean isSentByUser, String[] options) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.options = options;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean hasOptions() {
        return options != null && options.length > 0;
    }
}
