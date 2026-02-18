package com.sinch.sms.routing.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.sinch.sms.routing.beans.SMSMessage;
import com.sinch.sms.routing.entity.SMSMessageEntity;
import com.sinch.sms.routing.repository.SMSRepository;
import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import com.sinch.sms.routing.util.PhoneNumberUtility;
import com.sinch.sms.routing.util.SMSStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageRoutingService {

    private static final Logger logger = LoggerFactory.getLogger(MessageRoutingService.class);
    private SMSMessage smsMessage;
    @Autowired
    private PhoneNumberUtility phoneNumberUtility;
    @Autowired
    private SMSRepository smsRepository;
    private SMSMessageEntity saveSMSMessageEntity;
    private Optional<SMSMessageEntity> smsMessageEntityfindById;
    private NumberOptoutService optoutService;
    private SMSMessageEntity smsMessageEntity;
    @Autowired
    private CarrierService carrierService;


    public MessageRoutingService() {
    }


    public MessageRoutingService(PhoneNumberUtility phoneNumberUtility, CarrierService carrierService) {
        this.phoneNumberUtility = phoneNumberUtility;
        this.carrierService = carrierService;
    }

    public MessageRoutingService(SMSMessage smsMessage, Optional<SMSMessageEntity> smsMessageEntityfindById,
                                 SMSMessageEntity smsMessageEntity, SMSRepository smsRepository,
                                 SMSMessageEntity saveSMSMessageEntity, NumberOptoutService optoutService,
                                 CarrierService carrierService, PhoneNumberUtility phoneNumberUtility) {
        this.smsMessage = smsMessage;
        this.smsMessageEntityfindById = smsMessageEntityfindById;
        this.smsMessageEntity = smsMessageEntity;
        // this.smsRepository = smsRepository;
        this.saveSMSMessageEntity = saveSMSMessageEntity;
        this.optoutService = optoutService;
        this.carrierService = carrierService;

    }


    @Transactional
    public SMSMessage routedMessageStatusById(int id) {

        if (Optional.of(id).isPresent()) {
            smsMessageEntityfindById = smsRepository.findById(id);
            smsMessage = smsBeanMapping(smsMessageEntityfindById.get());
        }
        return smsMessage;

    }


    @Transactional
    public SMSMessage saveMessage(SMSMessage smsMessage) throws NumberParseException {

        boolean isValidPhoneNumber = isValidPhoneNumber(smsMessage.getReceiverPhoneNumber());
        logger.info(smsMessage.getReceiverPhoneNumber() + " is valid Number: " + isValidPhoneNumber);
        if (!isValidPhoneNumber) {
            logger.info(smsMessage.getReceiverPhoneNumber() + ": The number has been optedout suffessfully from Message Service");
            optoutService.saveOptOutNumber(smsMessage.getReceiverPhoneNumber());
        }
        smsMessage.setAreaCode(getAreaCode(smsMessage.getReceiverPhoneNumber()));// Saving Message entity to DataBase
        smsMessage.setCarreir(getCarrier(smsMessage.getReceiverPhoneNumber()));
        smsMessageEntity = smsRepository.save(saveRoutMessage(smsMessage));
        smsMessageEntity.setSmsStatus(getMessageStatus(smsMessageEntity));

        logger.info(smsMessageEntity.toString() + "has been saved successfully");

        return smsBeanMapping(smsMessageEntity);
    }


    public SMSStatus getMessageStatus(SMSMessageEntity smsMessageEntity) {
        if (!Optional.of(smsMessageEntity).isPresent()) {
            return SMSStatus.BLOCKED;
        }
        return SMSStatus.SENT;
    }

    public AreaCode getAreaCode(String phoneNumber) {
        return carrierService.isValiadateFormatToE164(phoneNumber);
    }

    public Carreir getCarrier(String phoneNumber) {
        return carrierService.isValiadateRegion(phoneNumber);
    }

    private SMSMessage smsBeanMapping(SMSMessageEntity smsMessageEntity) {
        return SMSMessage.builder()// Building SMSMessage Bean from SMSMessage Entity
                .messageId(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getMessageId)
                        .orElse(null))
                .messageBody(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getMessageBody)
                        .orElse(null))
                .senderPhoneNumber(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getSenderPhoneNumber)
                        .orElse(null))
                .receiverPhoneNumber(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getReceiverPhoneNumber)
                        .orElse(null))
                .smsStatus(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getSmsStatus)
                        .orElse(null))
                .areaCode(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getAreaCode)
                        .orElse(null))
                .carreir(Optional.ofNullable(smsMessageEntity)
                        .map(SMSMessageEntity::getCarreir)
                        .orElse(null)).build();

    }


    private SMSMessageEntity saveRoutMessage(SMSMessage smsMessage) {


        return SMSMessageEntity.builder()
                .messageId(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getMessageId)
                        .orElse(null))
                .messageBody(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getMessageBody)
                        .orElse("Message Body can not be null"))
                .senderPhoneNumber(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getSenderPhoneNumber)
                        .orElse("Sender number must be valid"))
                .receiverPhoneNumber(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getReceiverPhoneNumber)
                        .orElse("Receiver number must be valid"))
                .carreir(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getCarreir)
                        .orElse(null))
                .smsStatus(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getSmsStatus)
                        .orElse(null))
                .areaCode(Optional.ofNullable(smsMessage)
                        .map(SMSMessage::getAreaCode)
                        .orElse(null))
                .build();

    }

    public boolean isValidPhoneNumber(String phoneNumber) throws NumberParseException {
        return Optional.of(phoneNumberUtility.validateAndNormalize(phoneNumber)).isPresent();
    }


    @Transactional
    public List<SMSMessage> getAll() {
        List<SMSMessage> smsMessageList = new ArrayList<>();
        smsRepository.findAll().forEach(e -> {
            smsMessageList.add(smsBeanMapping(e));
        });
        return smsMessageList;
    }
}
