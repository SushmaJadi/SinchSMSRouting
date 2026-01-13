package com.sinch.sms.routing.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "Optout")
public class NumberOptoutEntity {

    @Id
    @Nullable
    @Column(name = "id")
    private int id;
    @Column(name = "phone_number")
    private String phoneNumber;

    public NumberOptoutEntity(String normalizedPhoneNumber) {
    }
}
