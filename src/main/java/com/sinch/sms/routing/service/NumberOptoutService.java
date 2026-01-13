package com.sinch.sms.routing.service;

import com.sinch.sms.routing.beans.NumberOptoutBean;
import com.sinch.sms.routing.entity.NumberOptoutEntity;
import com.sinch.sms.routing.repository.OptoutRepository;
import com.sinch.sms.routing.util.PhoneNumberUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NumberOptoutService {
    private static final Logger logger = LoggerFactory.getLogger(NumberOptoutService.class);
    private final OptoutRepository optoutRepository;
    private final PhoneNumberUtility phoneNumberUtility;
    private NumberOptoutEntity numberOptoutEntity;
    private Optional<NumberOptoutEntity> numberOptoutEntitybyId;
    private NumberOptoutBean numberOptoutBean;

    public NumberOptoutService(OptoutRepository optoutRepository, NumberOptoutEntity numberOptoutEntity, PhoneNumberUtility phoneNumberUtility) {
        this.optoutRepository = optoutRepository;
        this.numberOptoutEntity = numberOptoutEntity;
        this.phoneNumberUtility = phoneNumberUtility;
    }


    @Transactional
    public NumberOptoutBean saveOptOutNumber(String phoneNumber) {
        logger.info("Saving optout number in DB "+ phoneNumber);
        String normalizedPhoneNumber = phoneNumberUtility.validateAndNormalize(phoneNumber);
        numberOptoutBean = new NumberOptoutBean(normalizedPhoneNumber);
        numberOptoutEntity = optoutRepository.save(getOptOutNumberMapping(numberOptoutBean));
    return  getOptOutNumberMapping(Optional.of(numberOptoutEntity));
    }

    @Transactional
    public NumberOptoutBean optOutNumberfindbyId(String phoneNumber) {
        String validatedNumber = phoneNumberUtility.validateAndNormalize(phoneNumber);
        numberOptoutEntitybyId = optoutRepository.findById(validatedNumber);
        return getOptOutNumberMapping(numberOptoutEntitybyId);
    }

    @Transactional
    public void optoutNumberRemove(String phoneNumber) {

        String validatedNumber = phoneNumberUtility.validateAndNormalize(phoneNumber);
        numberOptoutEntitybyId = optoutRepository.findById(validatedNumber);
        if (phoneNumber.matches(numberOptoutEntity.getPhoneNumber())) {
            optoutRepository.deleteById(phoneNumber);
        }

    }

    public NumberOptoutBean getOptOutNumberMapping(Optional<NumberOptoutEntity> numberOptoutEntity) {

        numberOptoutBean.setId(numberOptoutEntity
                .map(NumberOptoutEntity::getId)
                .get());
        numberOptoutBean.setPhoneNumber(numberOptoutEntity
                .map(NumberOptoutEntity::getPhoneNumber)
                .orElse(null));
        return numberOptoutBean;


    }

    public NumberOptoutEntity getOptOutNumberMapping(NumberOptoutBean numberOptoutBean) {

        numberOptoutEntity.setId(Optional.ofNullable(numberOptoutBean)
                .map(NumberOptoutBean::getId)
                .orElseThrow());
        numberOptoutEntity.setPhoneNumber(Optional.ofNullable(numberOptoutBean)
                .map(NumberOptoutBean::getPhoneNumber)
                .orElse(null));
        return numberOptoutEntity;


    }


}
