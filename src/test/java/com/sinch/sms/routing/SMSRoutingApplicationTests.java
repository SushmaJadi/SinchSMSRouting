package com.sinch.sms.routing;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sinch.sms.routing.beans.SMSMessage;
import com.sinch.sms.routing.controller.SMSRoutingController;
import com.sinch.sms.routing.entity.SMSMessageEntity;
import com.sinch.sms.routing.repository.SMSRepository;
import com.sinch.sms.routing.service.MessageRoutingService;
import com.sinch.sms.routing.util.PhoneNumberUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SMSRoutingApplicationTests {

    @Mock
    private SMSRoutingController smsRoutingController;
    @Mock
    @Autowired
    private MessageRoutingService messageRoutingService;
    @Mock
    private SMSRepository smsRepository;
    @Mock
    private PhoneNumberUtility phoneNumberUtility;


    @Mock
    private PhoneNumberUtil phoneNumberUtil;

    @InjectMocks
    private SMSRoutingController smsRoutingMockController;
    @InjectMocks
    @Autowired
    private MessageRoutingService messageRoutingServiceMock;

    @InjectMocks
    private SMSMessage smsMessage;
    @InjectMocks
    private SMSMessageEntity smsMessageEntity;
    @InjectMocks
    PhoneNumberUtility phoneNumberUtilityMock;


    @Test
    void contextLoads() throws NumberParseException {
        this.smsRoutingController = new SMSRoutingController(messageRoutingService);
        this.messageRoutingService = new MessageRoutingService();
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
        // this.phoneNumberUtility = new PhoneNumberUtility(phoneNumberUtil);
        // this. phoneNumberUtilityMock = new PhoneNumberUtility(phoneNumberUtil);
        this.smsMessage = getSMSBean();

        Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(smsMessage.getReceiverPhoneNumber(), "null");


        int messageId = 1;
        //Mockito.when(phoneNumberUtility.validateAndNormalize(smsMessage.getReceiverPhoneNumber())).thenReturn(phoneNumberUtility.normalizePhoneNumber(smsMessage.getReceiverPhoneNumber()));
        Mockito.when(messageRoutingService.isValidPhoneNumber(smsMessage.getReceiverPhoneNumber())).thenReturn(Optional.of(phoneNumberUtility.validateAndNormalize(smsMessage.getReceiverPhoneNumber())).isPresent());
        Mockito.when(messageRoutingService.isValidPhoneNumber(smsMessage.getReceiverPhoneNumber()) == false).thenReturn(Optional.of(messageRoutingServiceMock.saveMessage(getSmsMessage())).isPresent() == true);
        Mockito.when(Optional.of(messageRoutingService.saveMessage(getSMSBean())))
                .thenReturn(Optional.ofNullable(getSmsMessage()));
        Mockito.when(smsRoutingController.sendMessage(getSMSBean())).thenReturn(ResponseEntity.ok(messageRoutingServiceMock.saveMessage(getSmsMessage())));
    }


    private SMSMessageEntity getMessageEntityMapping(SMSMessage message) {
        return SMSMessageEntity.builder()
                .messageId(smsMessage.getMessageId())
                .messageBody(smsMessage.getMessageBody())
                .senderPhoneNumber(smsMessage.getSenderPhoneNumber())
                .receiverPhoneNumber(smsMessage.getReceiverPhoneNumber())
                .areaCode(smsMessage.getAreaCode())
                .carreir(smsMessage.getCarreir())
                .smsStatus(smsMessage.getSmsStatus())
                .build();
    }

    private SMSMessage getSMSBean() {
        return SMSMessage.builder()
                .messageId(1)
                .messageBody("Hi This is Test Message")
                .senderPhoneNumber("+61837483478")
                .receiverPhoneNumber("+61233372763")
                .areaCode(null)
                .carreir(null)
                .smsStatus(null)
                .build();
    }

    private SMSMessage getMessageBeanMapping(SMSMessageEntity messageEntity) {
        return SMSMessage.builder()
                .messageId(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getMessageId)
                        .orElse(0))
                .messageBody(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getMessageBody)
                        .orElse(null))
                .senderPhoneNumber(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getSenderPhoneNumber)
                        .orElse(null))
                .receiverPhoneNumber(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getReceiverPhoneNumber)
                        .orElse("reciever number must need"))
                .areaCode(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getAreaCode)
                        .orElse(null))
                .carreir(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getCarreir)
                        .orElse(null))
                .smsStatus(Optional.of(messageEntity)
                        .map(SMSMessageEntity::getSmsStatus)
                        .orElse(null))
                .build();
    }

    public SMSMessage getSmsMessage() throws NumberParseException {
        return smsMessage = messageRoutingService.saveMessage(getMessageBeanMapping(getMessageEntityMapping(getSMSBean())));
    }
}
