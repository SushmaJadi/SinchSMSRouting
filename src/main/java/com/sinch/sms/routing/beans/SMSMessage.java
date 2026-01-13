package com.sinch.sms.routing.beans;

import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import com.sinch.sms.routing.util.SMSStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SMSMessage {

    @JsonProperty
    @Nullable
    private int messageId;
    @JsonProperty
    private String messageBody;
    @JsonProperty
    @Min(10)
    private String senderPhoneNumber;
    @JsonProperty
    @Min(10)
    private String receiverPhoneNumber;
    @JsonProperty
    private AreaCode areaCode;
    @JsonProperty
    private Carreir carreir;
    @JsonProperty
    private SMSStatus smsStatus;


}
