package com.sinch.sms.routing.controller;

import com.sinch.sms.routing.beans.SMSMessage;
import com.sinch.sms.routing.service.MessageRoutingService;
import com.sinch.sms.routing.util.SMSStatus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class SMSRoutingController {
    private static final Logger logger = LoggerFactory.getLogger(MessageRoutingService.class);
    @Autowired
    private SMSMessage smsMessage;

    private MessageRoutingService messageRoutingService;

    public SMSRoutingController(MessageRoutingService messageRoutingService) {
        this.messageRoutingService = messageRoutingService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SMSMessage> sendMessage(@RequestBody SMSMessage smsMessage) {
       boolean isOptout= messageRoutingService.isOptedOut(smsMessage.getReceiverPhoneNumber());

        try {
            if (!isOptout) {
               try {
                   smsMessage = messageRoutingService.saveMessage(smsMessage);
                   if (smsMessage.getSmsStatus().equals(SMSStatus.BLOCKED)) {
                       return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(smsMessage);
                   } else if (smsMessage.getSmsStatus().equals(SMSStatus.PENDING)) {
                       return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(smsMessage);
                   }
                   return ResponseEntity.ok().body(smsMessage);
               }catch (RuntimeException e){
                   new RuntimeException("InternalServerError");
                   return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(smsMessage);
               }

            }
        }catch (NumberFormatException e) {
            new NumberFormatException("invalid phone number");
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(smsMessage);
        }
        return ResponseEntity.ok().body(smsMessage);
    }


    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public @Nullable ResponseEntity<SMSMessage> routedMessageGetById(@PathVariable("id") Integer id) {
        smsMessage = messageRoutingService.routedMessageStatusById(id);
        if (Optional.of(smsMessage).isPresent()) {
            return ResponseEntity.ok(smsMessage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(path = "/getAll",method = RequestMethod.GET)
    public ResponseEntity<List<SMSMessage>> getAllMessages() {
        List<SMSMessage> messages = messageRoutingService.getAll();
        return ResponseEntity.ok(messages);
    }


}
