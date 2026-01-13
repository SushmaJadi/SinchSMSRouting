package com.sinch.sms.routing;

import com.sinch.sms.routing.beans.SMSMessage;
import com.sinch.sms.routing.entity.SMSMessageEntity;
import com.sinch.sms.routing.service.MessageRoutingService;
import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import com.sinch.sms.routing.util.SMSStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class SMSRoutingMVCTest {
    ObjectMapper objectMapper;

    SMSMessage smsMessage;
    MessageRoutingService messageService;
    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    @Test
    public void testPostRequest() throws Exception {


        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        smsMessage = new SMSMessage(0, "Test Message", "+617747471882", "617747471882", null, null, null);

        String response = mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(smsMessage)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();


        String messageId = objectMapper.readTree(response).get("messageId").asText();
        String status = objectMapper.readTree(response).get("status").asText();

        mockMvc.perform(get("/messages/" + messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("PENDING"));

    }
    @Test
    public void testSendToOptedOutNumberGetsBlocked() throws Exception {
        String optedOutNumber = "+61487654321";

        SMSMessage request = new SMSMessage(2,"Test message","+617634783873",optedOutNumber,null,null,null);
        SMSMessageEntity response = new SMSMessageEntity(2,"Test message","+617634783873",optedOutNumber, AreaCode.AUSTRALIA, Carreir.TELSTRA, SMSStatus.BLOCKED);

        // Mock that the number is opted out
        when(messageService.isOptedOut(optedOutNumber)).thenReturn(true);

        // Create a mock saved message with BLOCKED status

        // Mock the service route method
        when(messageService.saveMessage(any(SMSMessage.class))).thenReturn(smsBeanMapping(response));

        // Mock the service to return the message when queried by ID
        when(messageService.routedMessageStatusById(2)).thenReturn(smsBeanMapping(response));

        // Perform the POST request - should return FORBIDDEN
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isForbidden())
                .andExpect(content().string("Cannot send message to this phone number as it is opted out"));

        // Check the message status via GET endpoint - should be BLOCKED
        mockMvc.perform(get("/messages/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("BLOCKED"));
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

}
