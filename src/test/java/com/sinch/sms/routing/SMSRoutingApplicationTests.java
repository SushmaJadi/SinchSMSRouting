package com.sinch.sms.routing;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sinch.sms.routing.beans.SMSMessage;
import com.sinch.sms.routing.controller.SMSRoutingController;
import com.sinch.sms.routing.entity.SMSMessageEntity;
import com.sinch.sms.routing.repository.SMSRepository;
import com.sinch.sms.routing.service.CarrierService;
import com.sinch.sms.routing.service.MessageRoutingService;
import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import com.sinch.sms.routing.util.PhoneNumberUtility;
import com.sinch.sms.routing.util.SMSStatus;
import jakarta.annotation.sql.DataSourceDefinition;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@DataSourceDefinition(name = "mydatabase",
        className = "org.h2.Driver",
        url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        user = "sa",
        password = "",
        properties = {"databaseName= testdb"})
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class SMSRoutingApplicationTests {

    @Mock
    private SMSRoutingController smsRoutingController;
    @Mock
    // @Autowired
    private MessageRoutingService messageRoutingService;
    @Mock
    private SMSRepository smsRepository;
    @Mock
    private PhoneNumberUtility phoneNumberUtility;
    @Mock
    private CarrierService carrierService;
    @Mock
    private SMSMessageEntity smsMessageEntity;

    @Mock
    private PhoneNumberUtil phoneNumberUtil;
    @InjectMocks
    private SMSRoutingController smsRoutingMockController;
    @InjectMocks
    //  @Autowired
    private MessageRoutingService messageRoutingServiceMock;

    @InjectMocks
    private SMSMessage smsMessage;
    @InjectMocks
    private SMSMessageEntity smsMessageEntityMock;
    @InjectMocks
    PhoneNumberUtility phoneNumberUtilityMock;
    @InjectMocks
    private CarrierService carrierServiceMock;
    @InjectMocks
    private SMSMessage smsMessageMock;

    @Mock
    Phonenumber.PhoneNumber phoneNumber;


    @BeforeEach
    void contextLoads() throws Throwable {
        this.smsRoutingController = new SMSRoutingController(messageRoutingService);
        this.messageRoutingService = new MessageRoutingService();
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
        this.smsMessage = getSMSBean();
        this.smsMessageEntity = getMessageEntityMapping(smsMessage);

        phoneNumber = phoneNumberUtil.parse(smsMessage.getReceiverPhoneNumber(), "null");

        System.out.println(smsMessage.toString());
        int messageId = 1;


        phoneNumberValidityTest();
        //  messageRoutingServiceTest(smsMessage.getReceiverPhoneNumber(),phoneNumberUtilityMock);
    }

    @Test
    public void phoneNumberValidityTest() throws Throwable {
        phoneNumber = phoneNumberUtil.parse(smsMessage.getReceiverPhoneNumber(), "null");
        smsMessageMock = this.smsMessage;
        Mockito.when(carrierService.isValiadateFormatToE164(smsMessage.getReceiverPhoneNumber())).thenReturn(AreaCode.AUSTRALIA);
        Mockito.when(carrierService.isValiadateRegion(smsMessage.getReceiverPhoneNumber())).thenReturn(carrierServiceMock.getLocalCarrier(Carreir.TELSTRA, Carreir.OPTUS));
        smsMessageMock.setAreaCode(carrierService.isValiadateFormatToE164(smsMessage.getReceiverPhoneNumber()));
        smsMessageMock.setCarreir(carrierService.isValiadateRegion(smsMessage.getReceiverPhoneNumber()));
        System.out.println(getMessageEntityMapping(smsMessage));
        Mockito.when(smsRepository.save(getMessageEntityMapping(smsMessageMock))).thenReturn(getMessageEntityMapping(smsMessageMock));
        this.smsMessageEntityMock = smsRepository.save(getMessageEntityMapping(smsMessageMock));
        System.out.println(smsMessageEntityMock.toString() + "......." + smsMessageMock);
        Assertions.assertEquals(Optional.of(smsMessageEntityMock).isPresent(), messageRoutingServiceMock.getMessageStatus(smsMessageEntityMock).equals(SMSStatus.SENT));
        smsMessageEntityMock.setSmsStatus(messageRoutingServiceMock.getMessageStatus(smsMessageEntityMock));
        System.out.println(smsMessageEntityMock.toString());


    }
  /*  @Test
    public void messageRoutingServiceTest(String phoneNumber, PhoneNumberUtility utilMock) throws Throwable {
     //  System.out.println( utilMock.validateAndNormalize(phoneNumber));
        Phonenumber.PhoneNumber farmatedNumber = phoneNumberUtil.parse(smsMessage.getReceiverPhoneNumber(), "null");
        Mockito.when(phoneNumberUtility.normalizePhoneNumber(smsMessage.getReceiverPhoneNumber())).thenReturn(phoneNumberUtil.format(farmatedNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
        Mockito.when(phoneNumberUtility.isValidPhoneNumber(smsMessage.getReceiverPhoneNumber())).thenReturn(Optional.of(phoneNumber).isPresent());
        Mockito.when(phoneNumberUtility.validateAndNormalize(smsMessage.getReceiverPhoneNumber())).thenReturn(phoneNumberUtil.format(farmatedNumber, PhoneNumberUtil.PhoneNumberFormat.E164));

        Mockito.when(messageRoutingService.saveMessage(smsMessage)).thenReturn(getMessageBeanMapping(smsMessageEntityMock));
        Mockito.when(smsRoutingController.sendMessage(getSMSBean())).thenReturn(ResponseEntity.ok(messageRoutingServiceMock.saveMessage(smsMessage)));


        Assertions.assertEquals(messageRoutingServiceMock.saveMessage(smsMessageMock), getMessageBeanMapping(smsMessageEntityMock));

    }*/


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
