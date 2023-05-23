package tls.sofoste;

import tls.sofoste.controller.ClientController;
import tls.sofoste.controller.SessionController;
import tls.sofoste.model.Client;
import tls.sofoste.model.Session;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ESIFitApp {
    private static final int MAX_CHAR = 50;
    private static final ClientController clientController = new ClientController();
    private static final SessionController sessionController = new SessionController();
    private static final Scanner fetchUserInput = new Scanner(System.in);

    public static void main(String[] args) {
        displayEsiLogo();
        boolean exit = false;
        while (!exit) {
            // Display menu and read user userChoice
            displayMainMenu();
            try {
                int userChoice = fetchUserInput.nextInt();
                fetchUserInput.nextLine();

                // Perform action based on user userChoice
                switch (userChoice) {
                    case 1:
                        registerClient();
                        break;
                    case 2:
                        logIn();
                        break;
                    case 3:
                        logOut();
                        break;
                    case 4:
                        displayClientInformation();
                        break;
                    case 5:
                        displayClientSessions();
                        break;
                    case 6:
                        deleteClient();
                        break;
                    case 7:
                        listAllClients();
                        break;
                    case 8:
                        updateClientSession();
                        break;
                    case 9:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice. Please try again.");
                fetchUserInput.nextLine();
            }
        }
    }

    private static void registerClient() {
        System.out.print("Enter first name: ");
        String firstName = fetchUserInput.nextLine();
        System.out.print("Enter last name: ");
        String lastName = fetchUserInput.nextLine();

        Client newClient = clientController.registerClient(firstName, lastName);
        System.out.println("Client registered with ID: " + newClient.getId());
    }

    private static void logIn() {
        System.out.print("Enter client id: ");
        String clientId = fetchUserInput.nextLine();
        if (clientController.getClient(clientId) != null) {
            Session newSession = sessionController.startSession(clientId);
            System.out.println("Session started for client " + clientId);
        } else {
            System.out.println("Client with ID: " + clientId + " does not exist." +
                    "\n Please register the client first.");
        }
    }

    private static void logOut() {
        System.out.print("Enter client id: ");
        String clientId = fetchUserInput.nextLine();
        Client client = clientController.getClient(clientId);
        if (client != null) {
            List<Session> sessions = sessionController.getClientSessions(clientId);
            if (sessions != null && !sessions.isEmpty() && sessions.get(sessions.size() - 1).getLogoutTime() == null) {
                sessionController.endSession(clientId);
                System.out.println("Session ended for client " + clientId);
            } else {
                System.out.println("Client with ID: " + clientId + " is not currently logged in.");
            }
        } else {
            System.out.println("Client with ID: " + clientId + " does not exist. Please register the client first.");
        }
    }

    private static void listAllClients() {
        List<Client> clients = clientController.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("┌──────────────────────────────────────────────────┐" +
                    "\n\tNo clients registered yet.\n" +
                    "└──────────────────────────────────────────────────┘");
        } else {
            for (Client client : clients) {
                System.out.println("Client ID: " + client.getId() + ", Name: " + client.getFirstName() + " " + client.getLastName());
            }
        }
    }

    private static void displayClientInformation() {
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.print(" [i] - Enter the user id you want to see information: ");
        String clientId = fetchUserInput.nextLine();
        Client client = clientController.getClient(clientId);
        System.out.println("└──────────────────────────────────────────────────┘");
        if (client != null) {
            System.out.println("┌" + "─".repeat(MAX_CHAR * 2) + "┐");
            System.out.printf("| %-10s: %-40s |\n", "ID", client.getId());
            System.out.printf("| %-10s: %-40s |\n", "First Name", client.getFirstName());
            System.out.printf("| %-10s: %-40s |\n", "Last Name", client.getLastName());
            System.out.println("└" + "─".repeat(MAX_CHAR * 2) + "┘");
        } else {
            System.out.println("┌──────────────────────────────────────────────────┐");
            System.out.println("\t[!] - No client found with ID: " + clientId);
            System.out.println("└──────────────────────────────────────────────────┘");
        }
    }

    private static void displayClientSessions() {
        System.out.print("Enter the user id you want to see session info: ");
        String clientId = fetchUserInput.nextLine();
        List<Session> sessions = sessionController.getClientSessions(clientId);
        if (sessions == null || sessions.isEmpty()) {
            System.out.println("┌──────────────────────────────────────────────────┐");
            System.out.println("\t[!] - No sessions found for client with ID: " + clientId + "." +
                    "\n If the client is currently in the gym, please log them in.");
            System.out.println("└──────────────────────────────────────────────────┘");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            for (Session session : sessions) {
                String loginTime = (session.getLoginTime() != null) ? session.getLoginTime().format(formatter) : "N/A";
                String logoutTime = (session.getLogoutTime() != null) ? session.getLogoutTime().format(formatter) : "N/A";

                System.out.println("┌──────────────────────────────────────────────────┐");
                System.out.println("| Login Time : " + loginTime);
                System.out.println("| Logout Time: " + logoutTime);
                System.out.println("└──────────────────────────────────────────────────┘");
                System.out.println("----");
            }
        }
    }

    private static void updateClientSession() {
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.print("- Enter client id: ");
        String clientId = fetchUserInput.nextLine();
        if (clientController.getClient(clientId) != null) {
            System.out.print("- Enter session start date and time (format dd.MM.yyyy HH:mm): ");
            String startTime = fetchUserInput.nextLine();
            System.out.print("- Enter session end date and time (format dd.MM.yyyy HH:mm): ");
            String endTime = fetchUserInput.nextLine();
            sessionController.updateSession(clientId, startTime, endTime);
            System.out.println("\tSession updated for client " + clientId);
            System.out.println("└──────────────────────────────────────────────────┘");
        } else {
            System.out.println("┌──────────────────────────────────────────────────┐");
            System.out.println(" [!] - Client with ID: " + clientId + " does not exist." +
                    "\n Please register the client first.");
            System.out.println("└──────────────────────────────────────────────────┘");
        }
    }

    private static void deleteClient() {
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println(" [!] - WARNING: YOU ARE ABOUT TO DELETE A CLIENT [!]");
        System.out.print("- Enter id of the client you want to delete: ");
        String clientId = fetchUserInput.nextLine();
        if (clientController.getClient(clientId) != null) {
            clientController.deleteClient(clientId);
            sessionController.deleteClientSessions(clientId);
            System.out.println("\tClient with ID: " + clientId + " has been deleted");
        } else {
            System.out.println("\tClient with ID: " + clientId + " does not exist.");
        }
        System.out.println("└──────────────────────────────────────────────────┘");
    }

    private static void displayEsiLogo(){
        System.out.print("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("\n" +
                "| ███████╗ ██████╗██╗      ███████╗██╗████████╗    █████╗ ██╗     ██╗███████╗███╗  ██╗████████╗   ███╗   ███╗ █████╗ ███╗  ██╗ █████╗  ██████╗ ███████╗██████╗  |\n" +
                "| ██╔════╝██╔════╝██║      ██╔════╝██║╚══██╔══╝   ██╔══██╗██║     ||║██╔════╝████╗ ██║╚══██╔══╝   ████╗ ████║██╔══██╗████╗ ██║██╔══██╗██╔════╝ ██╔════╝██╔══██╗ |\n" +
                "| █████╗  ╚█████╗ ██║█████╗█████╗  ██║   ██║      ██║  ╚═╝██║     ██║█████╗  ██╔██╗██║   ██║      ██╔████╔██║███████║██╔██╗██║███████║██║  ██╗ █████╗  ██████╔╝ |\n" +
                "| ██╔══╝   ╚═══██╗██║╚════╝██╔══╝  ██║   ██║      ██║  ██╗██║     ██║██╔══╝  ██║╚████║   ██║      ██║╚██╔╝██║██╔══██║██║╚████║██╔══██║██║  ╚██╗██╔══╝  ██╔══██╗ |\n" +
                "| ███████╗██████╔╝██║      ██║     ██║   ██║      ╚█████╔╝███████╗██║███████╗██║ ╚███║   ██║      ██║ ╚═╝ ██║██║  ██║██║ ╚███║██║  ██║╚██████╔╝███████╗██║  ██║ |\n" +
                "| ╚══════╝╚═════╝ ╚═╝      ╚═╝     ╚═╝   ╚═╝       ╚════╝ ╚══════╝╚═╝╚══════╝╚═╝  ╚══╝   ╚═╝      ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝ |");
        System.out.println("└───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘");
    }

    private static void displayMainMenu() {
        System.out.println("\n" +
                "\t\t\t\t\t█▀▄▀█ ▄▀▄ █ █▄ █ ▄ █▀▄▀█ █▀▀ █▄ █ █ █\n" +
                "\t\t\t\t\t█ ▀ █ █▀█ █ █ ▀█   █ ▀ █ ██▄ █ ▀█ █▄█");
        System.out.println("\t\t\t┌" + "─".repeat(MAX_CHAR) + "┐");
        System.out.println("\t\t\t| \t\t\t\tESI-Fit Client Manager" + " ".repeat(MAX_CHAR - 37)+ "|");
        System.out.println("\t\t\t├" + "─".repeat(MAX_CHAR) + "┤");
        System.out.println("\t\t\t| 1. Register Client" + " ".repeat(MAX_CHAR - 20) + " "+ "|");
        System.out.println("\t\t\t| 2. Log In" + " ".repeat(MAX_CHAR - 11) + " "+"|");
        System.out.println("\t\t\t| 3. Log Out" + " ".repeat(MAX_CHAR - 11) + "|");
        System.out.println("\t\t\t| 4. Display Client Information" + " ".repeat(MAX_CHAR - 30) + "|");
        System.out.println("\t\t\t| 5. Display Client Sessions" + " ".repeat(MAX_CHAR - 27) + "|");
        System.out.println("\t\t\t| 6. Delete Client" + " ".repeat(MAX_CHAR - 17) + "|");
        System.out.println("\t\t\t| 7. List All Clients" + " ".repeat(MAX_CHAR - 20) + "|");
        System.out.println("\t\t\t| 8. Update Client Session" + " ".repeat(MAX_CHAR - 25) + "|");
        System.out.println("\t\t\t| 9. Exit" + " ".repeat(MAX_CHAR - 8) + "|");
        System.out.println("\t\t\t└" + "─".repeat(MAX_CHAR) + "┘");
        System.out.println("Enter your choice: ");
        System.out.println("\t\t\t| \t\t\t\tESI-Fit Client Manager" + " ".repeat(MAX_CHAR - 37) + "|");
        System.out.println("\t\t\t├" + "─".repeat(MAX_CHAR) + "┤");
        System.out.println("\t\t\t| 1. Kunde registrieren" + " ".repeat(MAX_CHAR - 22) + "|");
        System.out.println("\t\t\t| 2. Kunde anmelden" + " ".repeat(MAX_CHAR - 18) + "|");
        System.out.println("\t\t\t| 3. Kunde abmelden" + " ".repeat(MAX_CHAR - 18) + "|");
        System.out.println("\t\t\t| 4. Kunde Info anzeigen" + " ".repeat(MAX_CHAR - 24) + " |");
        System.out.println("\t\t\t| 5. Kundensitzungen anzeigen" + " ".repeat(MAX_CHAR - 28) + "|");
        System.out.println("\t\t\t| 6. Kunde löschen/entfernen" + " ".repeat(MAX_CHAR - 27) + "|");
        System.out.println("\t\t\t| 7. Alle Kunden anzeigen" + " ".repeat(MAX_CHAR - 24) + "|");
        System.out.println("\t\t\t| 8. Kundensitzung anpassen" + " ".repeat(MAX_CHAR - 26) + "|");
        System.out.println("\t\t\t| 9. Beenden" + " ".repeat(MAX_CHAR - 11) + "|");
        System.out.println("\t\t\t└" + "─".repeat(MAX_CHAR) + "┘");
        System.out.println("Geben Sie einen Befehl ein: ");
    }
}