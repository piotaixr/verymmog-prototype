package com.verymmog.network.manager.user.provider;

import com.verymmog.model.UserInterface;

public interface UserProviderInterface {

    public UserInterface getUser(String login);
}
