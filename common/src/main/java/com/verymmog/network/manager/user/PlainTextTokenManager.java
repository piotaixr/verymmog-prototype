package com.verymmog.network.manager.user;

import com.verymmog.model.UserInterface;
import org.apache.log4j.Logger;

public class PlainTextTokenManager implements TokenManager {
    private Logger logger;

    public PlainTextTokenManager() {
        logger = Logger.getLogger(this.getClass());
    }

    @Override
    public String generate(UserInterface user) {
        return "token";
    }

    @Override
    public boolean verify(String id, String token) {
        logger.trace("Verify token");
        return true;
    }
}
