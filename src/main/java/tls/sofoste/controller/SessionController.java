package tls.sofoste.controller;

import tls.sofoste.model.Session;
import tls.sofoste.service.SessionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionController {
    private SessionService sessionService = new SessionService();

    public Session startSession(String clientId) {
        return sessionService.startSession(clientId);
    }

    public void endSession(String clientId) {
        sessionService.endSession(clientId);
    }

    public List<Session> getClientSessions(String clientId) {
        return sessionService.getClientSessions(clientId);
    }

    public void updateSession(String clientId, String startTime, String endTime) {
        sessionService.updateSession(clientId, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
    }
    public void deleteClientSessions(String clientId) {
        sessionService.deleteClientSessions(clientId);
    }
}