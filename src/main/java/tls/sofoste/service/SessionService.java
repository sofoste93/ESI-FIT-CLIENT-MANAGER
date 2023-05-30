package tls.sofoste.service;

import tls.sofoste.model.Session;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionService {
    private Map<String, List<Session>> clientSessions = new HashMap<>();

    public SessionService() {
        loadSessionData();
    }

    public Session startSession(String clientId) {
        LocalDateTime now = LocalDateTime.now();
        Session newSession = new Session(clientId, now, null);
        clientSessions.computeIfAbsent(clientId, k -> new ArrayList<>()).add(newSession);

        saveSessionData();

        return newSession;
    }

    public void endSession(String clientId) {
        List<Session> sessions = clientSessions.get(clientId);
        if (sessions != null && !sessions.isEmpty()) {
            Session lastSession = sessions.get(sessions.size() - 1);
            lastSession.setLogoutTime(LocalDateTime.now());

            saveSessionData();
        }
    }

    public List<Session> getClientSessions(String clientId) {
        return clientSessions.get(clientId);
    }

    public void deleteClientSessions(String clientId) {
        clientSessions.remove(clientId);

        saveSessionData();
    }

    public void updateSession(String clientId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Session> sessions = clientSessions.get(clientId);
        if (sessions != null && !sessions.isEmpty()) {
            Session lastSession = sessions.get(sessions.size() - 1);
            lastSession.setLoginTime(startTime);
            lastSession.setLogoutTime(endTime);

            saveSessionData();
        }
    }

    // Auto-save session data to file
    private void saveSessionData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sessions.txt"))) {
            for (List<Session> sessions : clientSessions.values()) {
                for (Session session : sessions) {
                    writer.write(session.getClientId() + " | " + session.getLoginTime() + " | " + session.getLogoutTime());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load session data from file
    private void loadSessionData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("sessions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String clientId = parts[0].trim();
                LocalDateTime loginTime = LocalDateTime.parse(parts[1].trim());
                LocalDateTime logoutTime = parts[2].trim().equals("null") ? null : LocalDateTime.parse(parts[2].trim());
                Session session = new Session(clientId, loginTime, logoutTime);
                clientSessions.computeIfAbsent(clientId, k -> new ArrayList<>()).add(session);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

