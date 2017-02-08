package com.periscope.sso.idp;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class FileLoginModule implements LoginModule {
    private final static Logger LOGGER          = Logger.getLogger(FileLoginModule.class.getCanonicalName());
    private CallbackHandler     callbackHandler = null;
    private UserDatabase        userdb          = new UserDatabase();
    private Subject             subject         = null;
    private boolean             validUser       = false;

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        return validUser;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler handler, Map<String, ? > sharedState, Map<String, ? > options) {
        LOGGER.info("File Login Module initialze method started");

        this.callbackHandler = handler;
        this.subject = subject;
        String dbFileName = (String)options.get("userdb");

        // load the user database
        userdb.loadUsers(dbFileName);

        LOGGER.info(subject.toString());
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("name");
        PasswordCallback pwCallback = new PasswordCallback("password", false);

        try {
            this.callbackHandler.handle(new Callback[]{nameCallback, pwCallback});

            String username = nameCallback.getName();
            String password = new String(pwCallback.getPassword());

            // lookup user
            IDPUser user = userdb.lookupUser(username);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    validUser = true;

                    for (String attrName : user.getAttributeNames()) {
                        UserAttribute attribute = new UserAttribute(attrName, user.getAttribute(attrName));

                        subject.getPublicCredentials().add(attribute);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unexpected error while logging in.", e);
        }

        return validUser;
    }

    @Override
    public boolean logout() throws LoginException {
        // TODO Auto-generated method stub
        return false;
    }
}
