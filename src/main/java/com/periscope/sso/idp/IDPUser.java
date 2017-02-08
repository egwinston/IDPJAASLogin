package com.periscope.sso.idp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDPUser {
    private String              userName;
    private String              password;
    private Map<String, String> attributes = new HashMap<String, String>();

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getAttribute(String attrName) {
        return attributes.get(attrName);
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<String> getAttributeNames() {
        return attributes.keySet();
    }

    public String setAttribute(String attrName, String attrValue) {
        String oldValue = attributes.get(attrName);

        oldValue = this.attributes.put(attrName, attrValue);

        return oldValue;
    }

    public static final IDPUser factory(String line) throws Exception {
        if (line == null || line.length() == 0) {
            return null;
        }

        IDPUser newUser = new IDPUser();

        int index = 0;
        for (String part : line.split("\\s+", 3)) {
            part = part.trim();
            switch (index) {
                case 0: // username
                    newUser.setUsername(part);
                    break;
                case 1: // password
                    if (part == null || part.isEmpty()) {
                        throw new Exception("The user line is malformed : " + line);
                    }
                    newUser.setPassword(part);
                    break;
                default: // attribute string
                    newUser.processAttributeString(part);
                    //                    String[] attrPart = part.split(":", 2);
                    //                    newUser.setAttribute(attrPart[0], attrPart[1]);
                    break;
            }

            index++;
        }

        if (index < 2) {
            throw new Exception("The user line is malformed : " + line);
        }

        return newUser;
    }

    private void processAttributeString(String attributesString) {
        Pattern pattern = Pattern.compile("(.+?:.*?)(\\w*:.*)");
        String currentStr = attributesString;
        Matcher matcher = pattern.matcher(currentStr);
        String[] parts = null;

        while (matcher.matches()) {
            String attrAndValue = matcher.group(1);

            parts = attrAndValue.split(":", 2);
            setAttribute(parts[0].trim(), parts[1].trim());

            currentStr = matcher.group(2).trim();
            matcher = pattern.matcher(currentStr);
        }

        // process the final attribute
        if (!currentStr.isEmpty()) {
            parts = currentStr.split(":", 2);
            setAttribute(parts[0].trim(), parts[1].trim());
        }
    }

    public String getAttributesAsString() {
        StringBuilder attrsString = new StringBuilder();

        for (String key : attributes.keySet()) {
            attrsString.append("\"").append(key).append("\":::\"").append(attributes.get(key)).append("\" ");
        }

        return attrsString.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("IDPUser { ");
        builder.append("Username = ").append(userName).append(", ");
        builder.append("Password = ").append(password).append(", ");
        builder.append("Attributes : ").append(attributes);
        builder.append("}");

        return builder.toString().trim();
    }
}
