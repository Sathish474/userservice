package com.sathish.userservice.exceptions;

public class UserDoesnotExistException extends Exception {
    public UserDoesnotExistException(String message) {
        super(message);
    }
}
