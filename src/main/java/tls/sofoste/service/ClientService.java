package tls.sofoste.service;

import tls.sofoste.model.Client;
import tls.sofoste.utils.IdGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientService {
    private Map<String, Client> clients = new HashMap<>();

    public ClientService() {
        loadClientData();
    }

    public Client registerClient(String firstName, String lastName) {
        String id = IdGenerator.generateId() + firstName.substring(0, 2) + lastName.substring(0, 2);
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

    // Auto-save client data to file
    private void saveClientData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clients.txt"))) {
            for (Client client : clients.values()) {
                writer.write(client.getId() + " | " + client.getFirstName() + " | " + client.getLastName());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load client data from file
    private void loadClientData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("clients.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String id = parts[0].trim();
                String firstName = parts[1].trim();
                String lastName = parts[2].trim();
                Client client = new Client(id, firstName, lastName);
                clients.put(id, client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}