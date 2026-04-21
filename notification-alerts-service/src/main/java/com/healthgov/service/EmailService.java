package com.healthgov.service;

public interface EmailService {

    void sendSimpleEmail(String to, String subject, String body);
}