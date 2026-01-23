package com.sinch.sms.routing.entity;

import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import com.sinch.sms.routing.util.SMSStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "SMSMessage")
public class SMSMessageEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageId;
    @Column(name = "massageBody")
    private String messageBody;
    @Column(name = "senderPhoneNumber")
    private String senderPhoneNumber;
    @Column(name = "receiverPhoneNumber")
    private String receiverPhoneNumber;
    @Column(name = "areaCode")
    private AreaCode areaCode;
    @Column(name = "carrier")
    private Carreir carreir;
    @Column(name = "smsStatus")
    private SMSStatus smsStatus;


}
