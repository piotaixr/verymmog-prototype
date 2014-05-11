package com.verymmog.network.manager.user;

import com.verymmog.model.UserInterface;

public interface TokenManager {
    public String generate(UserInterface user);

    public boolean verify(String id, String token);
}
