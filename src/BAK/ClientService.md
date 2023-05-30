package tls.sofoste.service;

import tls.sofoste.model.Client;
import tls.sofoste.utils.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientService {
private Map<String, Client> clients = new HashMap<>();

    public Client registerClient(String firstName, String lastName) {
        String id = IdGenerator.generateId() + firstName.substring(0, 2);
        Client newClient = new Client(id, firstName, lastName);
        clients.put(id, newClient);
        return newClient;
    }

    public Client getClient(String id) {
        return clients.get(id);
    }

    public void deleteClient(String id) {
        clients.remove(id);
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }
}

