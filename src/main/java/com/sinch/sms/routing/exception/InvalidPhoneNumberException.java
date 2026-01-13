package com.sinch.sms.routing.exception;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class InvalidPhoneNumberException extends Exception {

    public InvalidPhoneNumberException(String message) {
        super(message);
    }

    public InvalidPhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }


}
