package com.periscope.sso.idp;

public class UserAttribute {
    private String name;
    private String value;

    public UserAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getBame() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

}
