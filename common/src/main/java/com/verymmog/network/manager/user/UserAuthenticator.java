package com.verymmog.network.manager.user;

import com.verymmog.model.UserInterface;

public interface UserAuthenticator {

    public boolean authenticate(UserInterface user, String password);
}
