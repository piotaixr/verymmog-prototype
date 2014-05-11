package com.verymmog.network.manager.user.provider;

import com.verymmog.model.UserInterface;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProviderChain implements UserProviderInterface {

    private Logger logger = Logger.getLogger(getClass());
    private List<UserProviderInterface> providers = new ArrayList<>();

    public UserProviderChain(UserProviderInterface... providers) {
        this(Arrays.asList(providers));
    }

    public UserProviderChain(List<UserProviderInterface> providers) {
        this.providers.addAll(providers);
    }

    @Override
    public UserInterface getUser(String login) {
        logger.debug("Loading user " + login);
        for (UserProviderInterface provider : providers) {
            logger.trace("Asking to " + provider.getClass().getCanonicalName());
            UserInterface user = provider.getUser(login);

            if (user != null) {
                logger.debug("User " + login + " found");
                return user;
            }

        }

        logger.debug("User " + login + " NOT found");
        return null;
    }
}
