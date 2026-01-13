package com.sinch.sms.routing.controller;

import com.sinch.sms.routing.beans.NumberOptoutBean;
import com.sinch.sms.routing.exception.InvalidPhoneNumberException;
import com.sinch.sms.routing.service.NumberOptoutService;
import com.sinch.sms.routing.util.PhoneNumberUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/optout")
public class OptoutNumberController {

    private final PhoneNumberUtility phoneNumberUtility;
    private NumberOptoutBean numberOptoutBean;
    private NumberOptoutService numberOptoutService;

    public OptoutNumberController(NumberOptoutBean numberOptoutBean, PhoneNumberUtility phoneNumberUtility, NumberOptoutService numberOptoutService) {
        this.numberOptoutService = numberOptoutService;
        this.numberOptoutBean = numberOptoutBean;
        this.phoneNumberUtility = phoneNumberUtility;

    }

    @RequestMapping( method = RequestMethod.POST)
    public ResponseEntity<NumberOptoutBean> saveOptoutNumber( @RequestBody String phoneNumber) {
       try {
           numberOptoutBean = numberOptoutService.saveOptOutNumber(phoneNumber);
           if (!Optional.of(numberOptoutBean).isPresent()) {
               return ResponseEntity.status(500).build();
           }
           return ResponseEntity.ok(numberOptoutBean);
       }
       catch (RuntimeException e){
           new RuntimeException("Internal server Error");
           return ResponseEntity.status(500).build();
       }
    }
    @RequestMapping(path = "/{phoneNumber}", method = RequestMethod.GET)
    public ResponseEntity<NumberOptoutBean> optoutNumber(@PathVariable("phoneNumber") String phoneNumber) {
        try {
            numberOptoutBean = numberOptoutService.optOutNumberfindbyId(phoneNumber);
            return ResponseEntity.status(200).body(numberOptoutBean);
        }
        catch (RuntimeException e){
            new InvalidPhoneNumberException("No Number available");
            return ResponseEntity.notFound().build();
        }

    }

    @RequestMapping(path = "/{phoneNumber}", method = RequestMethod.DELETE)
    public ResponseEntity<NumberOptoutBean> optoutNumberRemove(@PathVariable("phoneNumber") String phoneNumber) {
        numberOptoutService.optoutNumberRemove(phoneNumber);
        return ResponseEntity.ok().build();
    }
}
