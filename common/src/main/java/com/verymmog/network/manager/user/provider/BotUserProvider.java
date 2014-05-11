package com.verymmog.network.manager.user.provider;

import com.verymmog.model.User;
import com.verymmog.model.UserInterface;
import org.apache.log4j.Logger;

import java.util.HashMap;

public class BotUserProvider implements UserProviderInterface {

    private Logger logger = Logger.getLogger(getClass());
    private HashMap<String, UserInterface> bots = new HashMap<>();

    @Override
    public synchronized UserInterface getUser(String login) {
        if(login.matches("^Bot[0-9]+$")){
            logger.debug("Connexion d'un bot");
            if(!bots.containsKey(login)){
                bots.put(login, new User(login, "password"));
                logger.trace("Bot inconnu, creation...");
            }

            return bots.get(login);
        }else {
            return null;
        }


    }
}
