package com.hizencode.phoneresolver;

public class PhoneResult<A extends String, B extends String> {
    private A mainResult;
    private B secondaryResult;

    public PhoneResult(A mainResult, B secondaryResult) {
        this.mainResult = mainResult;
        this.secondaryResult = secondaryResult;
    }

    public void setMainResult(A mainResult) {
        this.mainResult = mainResult;
    }

    public void setSecondaryResult(B secondaryResult) {
        this.secondaryResult = secondaryResult;
    }

    public A getMainResult() {
        return mainResult;
    }

    public B getSecondaryResult() {
        return secondaryResult;
    }

    @Override
    public String toString() {
        return mainResult + " | " + secondaryResult;
    }
}
