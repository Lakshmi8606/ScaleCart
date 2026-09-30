package com.scalecart.auth.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ErrorResponse {

    private LocalDateTime timestamp = LocalDateTime.now(ZoneId.systemDefault());
    private int status;
    private String error;
    private List<String> messages;

    public ErrorResponse() {
        // Jackson binding
    }

    public ErrorResponse(int status, String error, List<String> messages) {
        this.status = status;
        this.error = error;
        this.messages = messages;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}