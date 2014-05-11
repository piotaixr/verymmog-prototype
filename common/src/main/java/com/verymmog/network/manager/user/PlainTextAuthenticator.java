package com.verymmog.network.manager.user;

import com.verymmog.model.UserInterface;
import org.apache.log4j.Logger;

public class PlainTextAuthenticator implements UserAuthenticator {
    private Logger logger;

    public PlainTextAuthenticator() {
        logger = Logger.getLogger(this.getClass());
    }

    @Override
    public boolean authenticate(UserInterface user, String password) {
        boolean ret = user.getPassword().equals(password);

        String logmessage = user.getLogin() + " authentication ";

        if (ret) {
            logger.trace(logmessage + "ok");
        } else {
            logger.trace(logmessage + "nok: " + user.getPassword() + " " + password);
        }

        return ret;
    }
}