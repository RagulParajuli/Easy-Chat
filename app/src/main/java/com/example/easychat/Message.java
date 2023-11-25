package com.example.easychat;

public class Message {
    public static String SENT_BY_ME="me";
    public static String SENT_BY_BOT="bot";
    String message;
    String sent_by;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSent_by() {
        return sent_by;
    }

    public void setSent_by(String sent_by) {
        this.sent_by = sent_by;
    }

    public Message(String message, String sent_by) {
        this.message = message;
        this.sent_by = sent_by;
    }
}
