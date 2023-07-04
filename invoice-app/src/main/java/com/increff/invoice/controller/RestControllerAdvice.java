package com.increff.invoice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.increff.invoice.model.data.MessageData;
import com.increff.invoice.service.ApiException;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageData handle(ApiException e) {
        MessageData data = new MessageData();
        data.setMessage(e.getMessage());
        return data;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageData handle(Throwable e) {
        MessageData data = new MessageData();
        data.setMessage("An unknown error has occurred - " + e.getMessage());
        return data;
    }
}