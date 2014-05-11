package com.verymmog.network.message.messages;

public class AuthenticationMessage implements MessageInterface {
    private String login;
    private String password;

    public AuthenticationMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
