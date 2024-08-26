package com.winnguyen1905.notification.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.winnguyen1905.notification.service.NotificationMQProducer;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/noti")
@RequiredArgsConstructor
public class NotiController {

    private final NotificationMQProducer producer;

    @GetMapping("/{param}")
    public String getMethodName(@PathVariable String param) {
        this.producer.send(param);
        return param;
    }

}