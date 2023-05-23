package tls.sofoste.service;

import tls.sofoste.model.Client;
import tls.sofoste.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private List<Client> clients = new ArrayList<>();

    public Client registerClient(String firstName, String lastName) {
        String id = IdGenerator.generateId() + firstName.substring(0, 2);
        Client newClient = new Client(id, firstName, lastName);
        clients.add(newClient);
        return newClient;
    }

    public Client getClient(String id) {
        for (Client client : clients) {
            if (client.getId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    public void deleteClient(String id) {
        clients.removeIf(client -> client.getId().equals(id));
    }
}

