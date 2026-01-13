package com.sinch.sms.routing.service;

import com.sinch.sms.routing.beans.SMSMessage;
import com.sinch.sms.routing.entity.SMSMessageEntity;
import com.sinch.sms.routing.repository.SMSRepository;
import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import com.sinch.sms.routing.util.PhoneNumberUtility;
import com.sinch.sms.routing.util.SMSStatus;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageRoutingService {

    private static final Logger logger = LoggerFactory.getLogger(MessageRoutingService.class);
    private SMSMessage smsMessage;
    @Autowired
    private  PhoneNumberUtility phoneNumberUtility;
    private SMSRepository smsRepository;
    private SMSMessageEntity saveSMSMessageEntity;
    private Optional<SMSMessageEntity> smsMessageEntityfindById;
    private NumberOptoutService optoutService;
    private SMSMessageEntity smsMessageEntity;
    private CarrierService carrierService;

    private AreaCode areaCode;

    public MessageRoutingService() {
    }

    public MessageRoutingService(SMSMessage smsMessage, Optional<SMSMessageEntity> smsMessageEntityfindById, SMSMessageEntity smsMessageEntity, SMSRepository smsRepository, SMSMessageEntity saveSMSMessageEntity, NumberOptoutService optoutService, CarrierService carrierService) {
        this.smsMessage = smsMessage;
        this.smsMessageEntityfindById = smsMessageEntityfindById;
        this.smsMessageEntity = smsMessageEntity;
        this.smsRepository = smsRepository;
        this.saveSMSMessageEntity = saveSMSMessageEntity;
        this.optoutService = optoutService;
        this.carrierService = carrierService;
    }


    @Transactional
    public SMSMessage routedMessageStatusById(int id)  {
        logger.info("SMS Message routed suffessfully from Service" + id);
        try {
            if (smsRepository.existsById(id)) {
                smsMessageEntityfindById = smsRepository.findById(id);
                smsMessage = smsBeanMapping(smsMessageEntityfindById.get());
                logger.info("SMS Message routed suffessfully from Service" + smsMessage.toString());
            }
            return smsMessage;
        } catch (Exception e) {
            new JDBCConnectionException("error", new SQLException(), e.getMessage());
        }
        return smsMessage ;
    }


    @Transactional
    public SMSMessage saveMessage(SMSMessage smsMessage) {
        boolean isOptout = isOptedOut(smsMessage.getReceiverPhoneNumber());

        if (isOptout) {
            optoutService.saveOptOutNumber(smsMessage.getReceiverPhoneNumber());
            //  smsMessageEntity.setSmsStatus(SMSStatus.PENDING);

        }
        smsMessage.setAreaCode(getAreaCode(smsMessageEntity.getReceiverPhoneNumber()));// Saving Message entity to DataBase
        smsMessage.setCarreir(getCarrier(smsMessage.getReceiverPhoneNumber()));
        smsMessageEntity = smsRepository.save(saveRoutMessage(smsMessage));
        smsMessageEntity.setSmsStatus(getMessageStatus(smsMessageEntity));


        logger.info(smsMessageEntity.toString() + "Has been saved successfully");
        return smsBeanMapping(smsMessageEntity);
    }


    public SMSStatus getMessageStatus(SMSMessageEntity smsMessageEntity) {
        if (!Optional.of(smsMessageEntity).isPresent()) {
            return SMSStatus.BLOCKED;
        }
        return SMSStatus.SENT;
    }

    public AreaCode getAreaCode(String phoneNumber) {
        return areaCode = carrierService.isValiadateFormatToE164(phoneNumber);
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

    public boolean isOptedOut(String phoneNumber) {
        String bean = phoneNumberUtility.validateAndNormalize(phoneNumber);
        return Optional.of(bean).isPresent();
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
