package com.hizencode.excelphoneresolver.resolver;

public record PhoneResult(String mainResult, String secondaryResult) {
    @Override
    public String toString() {
        return mainResult + " | " + secondaryResult;
    }
}
