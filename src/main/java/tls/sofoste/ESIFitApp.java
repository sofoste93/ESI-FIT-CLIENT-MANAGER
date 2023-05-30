package tls.sofoste;

import tls.sofoste.controller.ClientController;
import tls.sofoste.controller.SessionController;
import tls.sofoste.model.Client;
import tls.sofoste.model.Session;
import tls.sofoste.service.ClientService;
import tls.sofoste.service.SessionService;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ESIFitApp {
    private static final int MAX_CHAR = 50;
    private static final ClientController clientController = new ClientController();
    private static final SessionController sessionController = new SessionController();
    private static ClientService clientService = new ClientService();
    private static SessionService sessionService = new SessionService();
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
                        System.out.println("Ungültige Option. Bitte versuchen Sie es erneut.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
                fetchUserInput.nextLine(); // consume newline left-over
            }
        }
    }

    private static void registerClient() {
        System.out.print("Vornamen eingeben: ");
        String firstName = fetchUserInput.nextLine();
        System.out.print("Nachname eingeben: ");
        String lastName = fetchUserInput.nextLine();

        Client newClient = clientController.registerClient(firstName, lastName);
        System.out.println("Kunde registriert mit ID: " + newClient.getId());
    }

    private static void logIn() {
        System.out.print("Kunden-ID eingeben: ");
        String clientId = fetchUserInput.nextLine();
        if (clientController.getClient(clientId) != null) {
            Session newSession = sessionController.startSession(clientId);
            System.out.println("Sitzung für Kunden mit der ID: " + clientId + " gestartet!");
        } else {
            System.out.println("Der Kunde mit der ID: " + clientId + " existiert nicht.\n " +
                    "Bitte registrieren Sie den Kunden zuerst.");
        }
    }

    private static void logOut() {
        System.out.print("Kunden-ID eingeben: ");
        String clientId = fetchUserInput.nextLine();
        Client client = clientController.getClient(clientId);
        if (client != null) {
            List<Session> sessions = sessionController.getClientSessions(clientId);
            if (sessions != null && !sessions.isEmpty() && sessions.get(sessions.size() - 1).getLogoutTime() == null) {
                sessionController.endSession(clientId);
                System.out.println("Sitzung für Kunde: " + clientId + " beendet");
            } else {
                System.out.println("Der Kunde mit der ID: " + clientId + " ist derzeit nicht eingeloggt.");
            }
        } else {
            System.out.println("Der Kunde mit der ID: " + clientId + " existiert nicht.\n " +
                    "Bitte registrieren Sie den Kunden zuerst.");
        }
    }

    private static void listAllClients() {
        List<Client> clients = clientController.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("┌──────────────────────────────────────────────────┐" +
                    "\n\tEs sind noch keine Kunden registriert.\n" +
                    "└──────────────────────────────────────────────────┘");
        } else {
            for (Client client : clients) {
                System.out.println("Kunden-ID: " + client.getId()
                        + " | " + client.getFirstName()
                        + " " + client.getLastName());
            }
        }
    }

    private static void displayClientInformation() {
        System.out.println("┌──────────────────────────────────────────────────────────────────────────┐");
        System.out.print(" [i]-Die Kunden-ID eingeben, über die Sie Informationen anzeigen möchten: ");
        String clientId = fetchUserInput.nextLine();
        Client client = clientController.getClient(clientId);
        if (client != null) {
            System.out.println("┌" + "─".repeat(MAX_CHAR * 2) + "┐");
            System.out.printf("| %-10s: %-80s |\n", "Kunden-ID", client.getId());
            System.out.printf("| %-10s: %-80s |\n", "Vorname", client.getFirstName());
            System.out.printf("| %-10s: %-80s |\n", "Nachname", client.getLastName());
            System.out.println("└" + "─".repeat(MAX_CHAR * 2) + "┘");
        } else {
            System.out.println("┌──────────────────────────────────────────────────┐");
            System.out.println("\t[!]-Kein Kunde mit ID: " + clientId + " gefunden");
            System.out.println("└──────────────────────────────────────────────────┘");
        }
        System.out.println("└──────────────────────────────────────────────────────────────────────────┘");
    }

    private static void displayClientSessions() {
        System.out.print("Benutzer-ID eingeben, um Sitzungsinformationen zu sehen: ");
        String clientId = fetchUserInput.nextLine();
        List<Session> sessions = sessionController.getClientSessions(clientId);
        if (sessions == null || sessions.isEmpty()) {
            System.out.println("┌────────────────────────────────────────────────────────────────┐");
            System.out.println("\t[!]Keine Sitzungen für Client mit ID : " + clientId + " gefunden." +
                    "\n Ist der Kunde gerade im Fitnessstudio, melden Sie ihn bitte an.");
            System.out.println("└────────────────────────────────────────────────────────────────┘");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            for (Session session : sessions) {
                String loginTime = (session.getLoginTime() != null) ? session.getLoginTime().format(formatter) : "N/A";
                String logoutTime = (session.getLogoutTime() != null) ? session.getLogoutTime().format(formatter) : "N/A";

                System.out.println("┌──────────────────────────────────────────────────┐");
                System.out.println("| Anmeldezeit : " + loginTime);
                System.out.println("| Abmeldezeit: " + logoutTime);
                System.out.println("└──────────────────────────────────────────────────┘");
                System.out.println("----");
            }
        }
    }

    private static void updateClientSession() {
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.print("- Kunden-ID eingeben: ");
        String clientId = fetchUserInput.nextLine();
        if (clientController.getClient(clientId) != null) {
            System.out.print("- Datum und Uhrzeit des Sitzungsbeginns eingeben (Format tt.MM.jjjj HH:mm): ");
            String startTime = fetchUserInput.nextLine();
            System.out.print("- Datum und Uhrzeit des Sitzungsendes eingeben (Format tt.MM.jjjj HH:mm): ");
            String endTime = fetchUserInput.nextLine();
            sessionController.updateSession(clientId, startTime, endTime);
            System.out.println("\tSitzung für den Kunden " + clientId + "aktualisiert.");
            System.out.println("└──────────────────────────────────────────────────┘");
        } else {
            System.out.println("┌──────────────────────────────────────────────────┐");
            System.out.println("[!] - Kunde mit ID: " + clientId + " existiert nicht." +
                    "\n Bitte den Kunden zuerst registrieren.");
            System.out.println("└──────────────────────────────────────────────────┘");
        }
    }

    private static void deleteClient() {
        System.out.println("┌────────────────────────────────────────────────────┐");
        System.out.println("[!-WARNUNG: SIE SIND DABEI, EINEN KUNDEN ZU LÖSCHEN-!]");
        System.out.print("- Enter id of the client you want to delete: ");
        String clientId = fetchUserInput.nextLine();
        if (clientController.getClient(clientId) != null) {
            clientController.deleteClient(clientId);
            sessionController.deleteClientSessions(clientId);
            System.out.println("\tKunde mit ID: " + clientId + " wurde gelöscht");
        } else {
            System.out.println("\tKunde mit ID: " + clientId + " existiert nicht.");
        }
        System.out.println("└─────────────────────────────────────────────────────┘");
    }

    private static void displayEsiLogo() {
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
        System.out.print("Bitte wählen Sie eine Option aus: ");
    }
}