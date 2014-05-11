package com.verymmog.network.manager.user.provider;

import com.verymmog.model.UserInterface;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserProvider implements UserProviderInterface {
    Logger logger;
    private Map<String, UserInterface> users;

    public InMemoryUserProvider() {
        this(new HashMap<String, UserInterface>());
    }

    public InMemoryUserProvider(Map<String, UserInterface> users) {
        logger = Logger.getLogger(this.getClass());
        this.users = users;
    }

    public void addUser(UserInterface user){
        users.put(user.getLogin(), user);
    }

    @Override
    public UserInterface getUser(String login) {
        UserInterface u = users.get(login);

        if (u == null) {
            logger.trace("User " + login + " inconnu");
        } else {
            logger.trace("User " + login + " trouvé");
        }

        return u;
    }
}
