package com.sinch.sms.routing.exception;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class MessageformatException extends Exception {
    public MessageformatException(String message) {
        super(message);
    }
}
