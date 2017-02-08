package com.periscope.sso.idp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDatabase {
    private static final Logger  LOGGER     = Logger.getLogger(UserDatabase.class.getCanonicalName());
    private Map<String, IDPUser> knownUsers = new HashMap<String, IDPUser>();

    public IDPUser lookupUser(String username) {
        IDPUser foundUser = knownUsers.get(username);

        return foundUser;
    }

    public boolean isEmpty() {
        return knownUsers.isEmpty();
    }

    public void loadUsers(String fileName) {
        // attempt to open the file
        File inputFile = new File(fileName);
        InputStream is = null;

        if (inputFile.exists()) {
            try {
                is = new FileInputStream(inputFile);
            } catch (FileNotFoundException e) {
                // the file does not exist, try to open it as a resource
                LOGGER.info("Userdatabase file cannot be found.");
            }
        } else {
            LOGGER.info("File does not exist");
            // file does not exist, is it a resource?
            is = UserDatabase.class.getResourceAsStream(fileName);
        }

        if (is == null) {
          LOGGER.log(Level.WARNING, "No file exists with the name {0}.  No users loaded.", fileName);
            return;
        }

        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
        int lineNumber;

        try {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#")) {
                    line = reader.readLine();
                    continue;
                }

                lineNumber = reader.getLineNumber();

                LOGGER.log(Level.INFO, "Line {0} : {1}", new Object[]{lineNumber, line});

                try {
                    IDPUser user = IDPUser.factory(line);
                    if (user != null) {
                        LOGGER.info(user.toString());
                        knownUsers.put(user.getUsername(), user);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getMessage(), e);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while reading from input file.", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                LOGGER.warning("Unexpected exception while closing input stream : " + e.getMessage());
            }
        }
    }
}
