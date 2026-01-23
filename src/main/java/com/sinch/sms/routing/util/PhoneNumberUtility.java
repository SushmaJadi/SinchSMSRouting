package com.sinch.sms.routing.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sinch.sms.routing.exception.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;


@Component
public class PhoneNumberUtility {

    String phoneNumber;
    private final PhoneNumberUtil phoneNumberUtil;

    public PhoneNumberUtility() {
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public PhoneNumberUtility(PhoneNumberUtil phoneNumberUtil) {
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public boolean isValidCountryCode(String phoneNumber) throws Throwable {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Invalid Phone Number: " + phoneNumber).getCause();
        }
    }

    public boolean isValidPhoneNumber(String phoneNumber) throws Throwable {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "Invalid Phone Number: " + phoneNumber).getCause();
        }
    }

    public String normalizePhoneNumber(String phoneNumber) throws NumberParseException {
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);

            if (!isValidPhoneNumber(phoneNumber)) {
                this.phoneNumber = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                return this.phoneNumber;
            }
            // Return exception if invalid
        } catch (NumberParseException e) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "invalid phone format"); // Return original if parsing fails
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return this.phoneNumber;
    }

    public String validateAndNormalize(String phoneNumber) throws NumberParseException {
        this.phoneNumber = normalizePhoneNumber(phoneNumber);
        return this.phoneNumber;

    }

    public void validatePhoneNumber(String phoneNumber) throws Throwable {
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



}
