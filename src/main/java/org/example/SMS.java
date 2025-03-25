package org.example;

public class SMS {
    private final String phone;
    private final String message;

    public SMS(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SMS to " + phone + ": \"" + message + "\"";
    }
}
