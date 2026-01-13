package com.sinch.sms.routing.util;

import com.sinch.sms.routing.exception.InvalidPhoneNumberException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


//@Component
public class PhoneNumberUtility {

    private PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public PhoneNumberUtility() {

    }
   /* public PhoneNumberUtility(PhoneNumberUtil phoneNumberUtil){
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }
*/
    public boolean isValidPhoneNumber(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }


    public String normalizePhoneNumber(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            if (!isValidPhoneNumber(phoneNumber)) {
                return phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
            return phoneNumber; // Return original if invalid
        } catch (NumberParseException e) {
            return phoneNumber; // Return original if parsing fails
        }
    }

    public String validateAndNormalize(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        /*isValidLength(phoneNumber);
        isValidAreaCode(phoneNumber);
        getCountryCode(phoneNumber);*/
        return normalizePhoneNumber(phoneNumber);

    }

    public void validatePhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            new InvalidPhoneNumberException("Invalid phone number format: " + phoneNumber);
        }
    }


    public int isValidLength(String phoneNumber) {
        int length = 0;
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            length = phoneNumberUtil.getLengthOfNationalDestinationCode(parsedNumber);
            return length;
        } catch (NumberParseException e) {
            new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN, " Invalid Destination Code length: " + phoneNumber);
        }
        return length;
    }

    public boolean isValidAreaCode(String phoneNumber) {

        String regeonCode = null;
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            regeonCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber);

        } catch (NumberParseException e) {
            new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, " Invalid Phone Number: " + phoneNumber);
        }
        return true;
    }

    public String getCountryCode(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            if (phoneNumberUtil.isValidNumber(parsedNumber)) {
                return phoneNumberUtil.getRegionCodeForNumber(parsedNumber);
            }
            return null;
        } catch (NumberParseException e) {
            return null;
        }
    }


}
