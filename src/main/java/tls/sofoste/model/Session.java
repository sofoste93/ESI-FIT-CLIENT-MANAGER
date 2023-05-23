package tls.sofoste.model;

import java.time.LocalDateTime;

public class Session {
    private String clientId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    public Session(String clientId, LocalDateTime loginTime, LocalDateTime logoutTime) {
        this.clientId = clientId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    // getters and setters here

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }
}
