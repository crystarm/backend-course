package com.karandashov.jbcd.client.captcha.response;

public class CheckCaptchaResponse {

    private Boolean valid;

    public CheckCaptchaResponse() {
    }

    public CheckCaptchaResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "CheckCaptchaResponse{" +
                "valid=" + valid +
                '}';
    }
}
