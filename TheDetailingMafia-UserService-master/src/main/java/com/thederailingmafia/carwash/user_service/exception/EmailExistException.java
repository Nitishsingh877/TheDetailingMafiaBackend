package com.thederailingmafia.carwash.user_service.exception;

public class EmailExistException extends  RuntimeException{
    public EmailExistException(String message){
        super(message);
    }
}
