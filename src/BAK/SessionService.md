package tls.sofoste.service;

import tls.sofoste.model.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private List<Session> sessions = new ArrayList<>();

    public Session startSession(String clientId) {
        LocalDateTime now = LocalDateTime.now();
        Session newSession = new Session(clientId, now, null);
        sessions.add(newSession);
        return newSession;
    }

    public void endSession(String clientId) {
        for (int i = sessions.size() - 1; i >= 0; i--) {
            Session session = sessions.get(i);
            if (session.getClientId().equals(clientId) && session.getLogoutTime() == null) {
                session.setLogoutTime(LocalDateTime.now());
                break;
            }
        }
    }

    public List<Session> getClientSessions(String clientId) {
        List<Session> clientSessions = new ArrayList<>();
        for (Session session : sessions) {
            if (session.getClientId().equals(clientId)) {
                clientSessions.add(session);
            }
        }
        return clientSessions;
    }
}
