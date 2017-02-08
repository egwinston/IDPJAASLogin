package com.periscope.sso.idp;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.Is.*;

import org.junit.Test;

public class UserDatabaseTest {

    @Test
    public void testUserDataBaseLoad() throws Exception {
        UserDatabase db = new UserDatabase();
        db.loadUsers("/sampleusers.txt");

        assertThat(db, notNullValue());
        assertThat(db.isEmpty(), is(false));
    }

    @Test
    public void testUserDataBaseLoadNoFile() throws Exception {
        UserDatabase db = new UserDatabase();
        db.loadUsers("/sampleusers-does-not-exist.txt");

        assertThat(db, notNullValue());
        assertThat(db.isEmpty(), is(true));
    }

    @Test
    public void testLookupValidUser() throws Exception {
        UserDatabase db = new UserDatabase();
        db.loadUsers("/sampleusers.txt");

        assertThat(db, notNullValue());
        assertThat(db.isEmpty(), is(false));

        IDPUser user = db.lookupUser("user2");
        assertThat(user, notNullValue());
        assertThat(user.getUsername(), is("user2"));
        assertThat(user.getPassword(), is("password2"));
        assertThat(user.getAttribute("attrName1"), is("value1-2"));
        assertThat(user.getAttribute("attrName2"), is("value2-2"));
        assertThat(user.getAttribute("attrName3"), is("value3-2"));
        assertThat(user.getAttribute("attrName4"), is("value4-2"));
    }

    @Test
    public void testLookupInvalidUser() throws Exception {
        UserDatabase db = new UserDatabase();
        db.loadUsers("/sampleusers.txt");

        assertThat(db, notNullValue());
        assertThat(db.isEmpty(), is(false));

        IDPUser user = db.lookupUser("user2-nope");
        assertThat(user, nullValue());
    }

    @Test
    public void testLookupOnEmptyDB() throws Exception {
        UserDatabase db = new UserDatabase();
        db.loadUsers("/sampleusers-not-exist.txt");

        assertThat(db, notNullValue());
        assertThat(db.isEmpty(), is(true));

        IDPUser user = db.lookupUser("user2-nope");
        assertThat(user, nullValue());
    }
}
