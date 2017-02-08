package com.periscope.sso.idp;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.Is.*;

public class IDPUserTest {
    @Test
    public void testFactoryMethod() throws Exception {
        String line = "user1 password1 attrName1:value1-1  attrName2:value2-1  attrName3:value3-1  attrName4:value4-1";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is("user1"));
        assertThat(user.getPassword(), is("password1"));
        assertThat(user.getAttribute("attrName1"), is("value1-1"));
        assertThat(user.getAttribute("attrName2"), is("value2-1"));
        assertThat(user.getAttribute("attrName3"), is("value3-1"));
        assertThat(user.getAttribute("attrName4"), is("value4-1"));
    }

    @Test
    public void testFactoryMethodNoAttributes() throws Exception {
        String line = "user1       password1       ";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is("user1"));
        assertThat(user.getPassword(), is("password1"));
        assertThat(user.getAttribute("attrName1"), nullValue());
        assertThat(user.getAttribute("attrName2"), nullValue());
        assertThat(user.getAttribute("attrName3"), nullValue());
        assertThat(user.getAttribute("attrName4"), nullValue());
    }

    @Test
    public void testFactoryMethodAttributesWithSpaces() throws Exception {
        String line = "internal admin first_name:Edward IA last_name:Winston email:ewinston@periscopeholdings.com org:ADSPO ssoid:SSOID-00003    ";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is("internal"));
        assertThat(user.getPassword(), is("admin"));
        assertThat(user.getAttribute("first_name"), is("Edward IA"));
        assertThat(user.getAttribute("last_name"), is("Winston"));
        assertThat(user.getAttribute("email"), is("ewinston@periscopeholdings.com"));
        assertThat(user.getAttribute("ssoid"), is("SSOID-00003"));
        assertThat(user.getAttribute("org"), is("ADSPO"));
    }

    @Test
    public void testPrintFormattedAttributes() throws Exception {
        String line = "user1       password1    attrName1:value1-1  attrName2:value2-1  attrName3:value3-1  attrName4:value4-1   ";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is("user1"));
        assertThat(user.getPassword(), is("password1"));

        assertThat(user.getAttributesAsString(), notNullValue());
        assertFalse(user.getAttributesAsString().isEmpty());
    }

    @Test
    public void testPrintFormattedAttributesNoAttributes() throws Exception {
        String line = "user1       password1       ";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is("user1"));
        assertThat(user.getPassword(), is("password1"));

        assertThat(user.getAttributesAsString(), notNullValue());
        assertThat(user.getAttributesAsString().length(), is(0));
    }

    @Test(expected = Exception.class)
    public void testFactoryMalformedLine() throws Exception {
        String line = "user1          ";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, nullValue());
    }

    @Test(expected = Exception.class)
    public void testFactoryEmptyLine() throws Exception {
        String line = "          ";

        IDPUser user = IDPUser.factory(line);

        assertThat(user, nullValue());
    }


}
