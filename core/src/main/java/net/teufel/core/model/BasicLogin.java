package net.teufel.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicLogin {

    private String user;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String db;
    private String token;
    private Boolean loginSuccessful;
    private String logonTime;

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getDb() {
        return db;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setLoginSuccessful(Boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }

    public Boolean isLoginSuccessful() {
        return loginSuccessful;
    }


    public String getLogonTime() {
        return logonTime;
    }

    public void setLogonTime(String logonTime) {
        this.logonTime = logonTime;
    }

    @Override
    public String toString() {
        return this.user + "/" + this.password;
    }

}
