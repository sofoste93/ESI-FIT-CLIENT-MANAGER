package tls.sofoste.service;

import tls.sofoste.model.Client;
import tls.sofoste.utils.IdGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

        saveClientData();

        return newClient;
    }

    public Client getClient(String id) {
        return clients.get(id);
    }

    public void deleteClient(String id) {
        clients.remove(id);

        saveClientData();
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }

    // auto-save client data to file
    public void saveClientData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clients.txt"))) {
            for (Client client : clients.values()) {
                writer.write(client.getId() + " | " + client.getFirstName() + " | " + client.getLastName());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


