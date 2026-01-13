package com.sinch.sms.routing.service;

import com.sinch.sms.routing.util.AreaCode;
import com.sinch.sms.routing.util.Carreir;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {
    private AreaCode areaCode;
    private String Teslra;
    private String Optus;


    public Carreir getLocalCarrier(Carreir carreir, Carreir carreir1) {
        return carreir;
    }

    public AreaCode isValiadateFormatToE164(String phoneNumber) {

        var areaCode = switch (phoneNumber) {
            case "^.[6][1]|[1-9]d{1,8}$":
                yield AreaCode.AUSTRALIA;
            case "^.[6][4]|[1-9]d{1,8}$":
                yield AreaCode.NewZealand;
            default:
                yield AreaCode.UNKNOWN;
        };
        return areaCode;
    }

    public Carreir isValiadateRegion(String phoneNumber) {

        areaCode = isValiadateFormatToE164(phoneNumber);

        return switch (areaCode) {
            case AUSTRALIA ->  getLocalCarrier(Carreir.TELSTRA,Carreir.OPTUS);
            case NewZealand -> Carreir.SPARK;
            case UNKNOWN -> Carreir.GLOBAL;
        };
    }

}
