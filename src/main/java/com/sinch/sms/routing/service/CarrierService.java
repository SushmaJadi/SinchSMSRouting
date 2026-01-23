package com.sinch.sms.routing.service;

import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {
    private AreaCode areaCode;

    public Carreir getLocalCarrier(Carreir carreir, Carreir carreir1) {
        return carreir;
    }

    public AreaCode isValiadateFormatToE164(String phoneNumber) {

        boolean au = phoneNumber.substring(0, 3).matches("^.[0]|[+][6][1].?$");
        boolean nz = phoneNumber.substring(0, 3).matches("^.[0]|[+][6][4].?$");

        if (au) {
            return AreaCode.AUSTRALIA;
        } else if (nz) {
            return AreaCode.NewZealand;
        } else {
            return AreaCode.UNKNOWN;
        }

    }


    public Carreir isValiadateRegion(String phoneNumber) {

        this.areaCode = isValiadateFormatToE164(phoneNumber);

        return switch (areaCode) {
            case AUSTRALIA ->  getLocalCarrier(Carreir.TELSTRA,Carreir.OPTUS);
            case NewZealand -> Carreir.SPARK;
            case UNKNOWN -> Carreir.GLOBAL;
        };
    }

}
