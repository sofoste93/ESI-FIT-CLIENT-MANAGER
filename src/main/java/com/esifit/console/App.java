package com.esifit.console;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class App {
    private static final String VERSION = "2.0.0";
    private static final TextColor GREEN = new TextColor.RGB(142, 255, 90);
    private static final TextColor CYAN = new TextColor.RGB(85, 233, 255);
    private static final TextColor MUTED = new TextColor.RGB(104, 145, 137);
    private static final TextColor INK = new TextColor.RGB(5, 12, 9);
    private static final TextColor PANEL = new TextColor.RGB(10, 25, 18);
    private static final TextColor TEXT = new TextColor.RGB(205, 229, 211);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", Locale.ENGLISH);

    private final Database database;
    private WindowBasedTextGUI gui;
    private BasicWindow mainWindow;
    private String notice = "READY · select an operation with arrows or Tab";
    private boolean exiting;

    private App(Database database) { this.database = database; }

    private void run() throws IOException {
        var terminalFactory = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(108, 34))
                .setTerminalEmulatorTitle("ESI-FIT Console Orbit");
        Screen screen = terminalFactory.createScreen();
        try {
            screen.startScreen();
            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(INK));
            gui.setTheme(SimpleTheme.makeTheme(true, TEXT, INK, CYAN, PANEL, INK, GREEN, INK));
            mainWindow = new BasicWindow(" ESI-FIT // CONSOLE MISSION CONTROL ");
            mainWindow.setHints(List.of(Window.Hint.FULL_SCREEN));
            refresh();
            gui.addWindowAndWait(mainWindow);
        } finally {
            screen.close();
        }
    }

    private void refresh() { mainWindow.setComponent(dashboard()); }

    private Component dashboard() {
        Metrics metrics = database.metrics();
        var root = new Panel(new LinearLayout(Direction.VERTICAL));
        root.addComponent(title());
        root.addComponent(new EmptySpace(new com.googlecode.lanterna.TerminalSize(1, 1)));
        root.addComponent(metrics(metrics));
        root.addComponent(new EmptySpace(new com.googlecode.lanterna.TerminalSize(1, 1)));

        var body = new Panel(new GridLayout(2));
        body.addComponent(actions().withBorder(Borders.singleLine(" OPERATIONS ")));
        body.addComponent(recentActivity().withBorder(Borders.singleLine(" RECENT ACTIVITY ")));
        root.addComponent(body.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        root.addComponent(new EmptySpace(new com.googlecode.lanterna.TerminalSize(1, 1)));
        root.addComponent(new Label(" " + notice).setForegroundColor(GREEN));
        root.addComponent(new Label(" v" + VERSION + "  ·  PRIVATE / LOCAL / OFFLINE  ·  [TAB] navigate  [ENTER] select  [ESC] close")
                .setForegroundColor(MUTED));
        return root;
    }

    private Component title() {
        var header = new Panel(new LinearLayout(Direction.VERTICAL));
        header.addComponent(new Label("  E S I - F I T   //   C O N S O L E   O R B I T")
                .setForegroundColor(GREEN).addStyle(SGR.BOLD));
        header.addComponent(new Label("  STRONGER PEOPLE. SMARTER CLUB. ZERO CLOUD.")
                .setForegroundColor(CYAN));
        header.addComponent(new Label("  " + LocalDateTime.now().format(DATE_TIME).toUpperCase(Locale.ROOT))
                .setForegroundColor(MUTED));
        return header.withBorder(Borders.doubleLine());
    }

    private Panel metrics(Metrics metrics) {
        var panel = new Panel(new GridLayout(4));
        panel.addComponent(metric("ACTIVE MEMBERS", Integer.toString(metrics.activeMembers()), GREEN));
        panel.addComponent(metric("IN THE CLUB", Integer.toString(metrics.checkedIn()), CYAN));
        panel.addComponent(metric("VISITS TODAY", Integer.toString(metrics.visitsToday()), TextColor.ANSI.YELLOW));
        panel.addComponent(metric("AVG SESSION", formatMinutes(metrics.averageMinutes()), TextColor.ANSI.MAGENTA));
        return panel;
    }

    private Component metric(String title, String value, TextColor color) {
        var box = new Panel(new LinearLayout(Direction.VERTICAL));
        box.addComponent(new Label(" " + value + " ").setForegroundColor(color).addStyle(SGR.BOLD));
        return box.withBorder(Borders.singleLine(" " + title + " "));
    }

    private Panel actions() {
        var panel = new Panel(new GridLayout(2));
        panel.addComponent(button("01  + ADD MEMBER", this::addMember));
        panel.addComponent(button("02  > CHECK IN", () -> attendance(true)));
        panel.addComponent(button("03  < CHECK OUT", () -> attendance(false)));
        panel.addComponent(button("04  MEMBERS", this::showMembers));
        panel.addComponent(button("05  VISIT ARCHIVE", this::showVisits));
        panel.addComponent(button("06  MANAGE MEMBER", this::manageMember));
        panel.addComponent(button("07  EXPORT CSV", this::exportCsv));
        panel.addComponent(button("08  HELP / ABOUT", this::showHelp));
        panel.addComponent(button("09  REFRESH", () -> notice = "DASHBOARD REFRESHED · " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        panel.addComponent(button("00  EXIT", () -> { exiting = true; mainWindow.close(); }));
        return panel;
    }

    private Panel recentActivity() {
        var panel = new Panel(new LinearLayout(Direction.VERTICAL));
        List<Visit> visits = database.visits(8);
        if (visits.isEmpty()) {
            panel.addComponent(new Label(" No visits recorded yet.").setForegroundColor(MUTED));
            panel.addComponent(new Label(" Add a member, then check them in."));
        } else {
            for (Visit visit : visits) {
                String state = visit.open() ? "LIVE" : formatMinutes(visit.duration().toMinutes());
                TextColor color = visit.open() ? GREEN : MUTED;
                panel.addComponent(new Label(String.format(Locale.ROOT, " %-18s  %-5s  %s",
                        trim(visit.memberName(), 18), state, visit.checkIn().format(DateTimeFormatter.ofPattern("dd MMM HH:mm"))))
                        .setForegroundColor(color));
            }
        }
        return panel;
    }

    private Button button(String label, Runnable action) {
        return new Button(label, () -> {
            try { action.run(); }
            catch (RuntimeException error) { message("Operation failed", error.getMessage()); }
            if (!exiting) refresh();
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER, true, false));
    }

    private void addMember() {
        var dialog = new BasicWindow(" NEW MEMBER ");
        dialog.setHints(List.of(Window.Hint.CENTERED));
        var form = new Panel(new GridLayout(2));
        var first = new TextBox();
        var last = new TextBox();
        var email = new TextBox();
        var plan = new ComboBox<>("Flex", "Core", "Unlimited", "Student");
        form.addComponent(new Label("First name")); form.addComponent(first);
        form.addComponent(new Label("Last name")); form.addComponent(last);
        form.addComponent(new Label("Email")); form.addComponent(email);
        form.addComponent(new Label("Plan")); form.addComponent(plan);
        form.addComponent(new Button("CREATE", () -> {
            try {
                Member member = database.addMember(first.getText(), last.getText(), email.getText(), plan.getSelectedItem());
                notice = "MEMBER CREATED · " + member.fullName() + " · " + member.id();
                dialog.close();
            } catch (IllegalArgumentException error) { message("Check the form", error.getMessage()); }
        }));
        form.addComponent(new Button("CANCEL", dialog::close));
        dialog.setComponent(form.withBorder(Borders.singleLine(" PROFILE ")));
        gui.addWindowAndWait(dialog);
    }

    private void attendance(boolean entering) {
        Member member = chooseMember(entering ? "CHECK IN" : "CHECK OUT", "");
        if (member == null) return;
        boolean changed = entering ? database.checkIn(member.id()) : database.checkOut(member.id());
        if (changed) notice = member.fullName().toUpperCase(Locale.ROOT) + (entering ? " ENTERED THE CLUB" : " LEFT THE CLUB");
        else message("No change", entering ? "Member is paused or already checked in." : "No open visit was found.");
    }

    private Member chooseMember(String title, String filter) {
        List<Member> members = database.members(filter);
        if (members.isEmpty()) { message(title, "No matching members."); return null; }
        var selected = new Member[1];
        var dialog = new BasicWindow(" " + title + " ");
        dialog.setHints(List.of(Window.Hint.CENTERED));
        var list = new ActionListBox(new com.googlecode.lanterna.TerminalSize(64, Math.min(16, members.size() + 2)));
        for (Member member : members) {
            String state = database.isCheckedIn(member.id()) ? "IN CLUB" : member.active() ? "ACTIVE" : "PAUSED";
            list.addItem(String.format(Locale.ROOT, "%-10s  %-25s  %-9s  %s",
                    member.id(), trim(member.fullName(), 25), member.plan(), state), () -> {
                selected[0] = member;
                dialog.close();
            });
        }
        var panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(list);
        panel.addComponent(new Button("CANCEL", dialog::close));
        dialog.setComponent(panel);
        gui.addWindowAndWait(dialog);
        return selected[0];
    }

    private void showMembers() {
        String search = TextInputDialog.showDialog(gui, "Member directory", "Search name, ID or email (empty = all)", "");
        if (search == null) return;
        var dialog = new BasicWindow(" MEMBER DIRECTORY ");
        dialog.setHints(List.of(Window.Hint.CENTERED));
        var table = new Table<String>("ID", "MEMBER", "PLAN", "STATUS");
        for (Member member : database.members(search)) {
            String status = database.isCheckedIn(member.id()) ? "IN CLUB" : member.active() ? "ACTIVE" : "PAUSED";
            table.getTableModel().addRow(member.id(), trim(member.fullName(), 28), member.plan(), status);
        }
        var panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(table);
        panel.addComponent(new Button("CLOSE", dialog::close));
        dialog.setComponent(panel);
        gui.addWindowAndWait(dialog);
    }

    private void showVisits() {
        var dialog = new BasicWindow(" VISIT ARCHIVE ");
        dialog.setHints(List.of(Window.Hint.CENTERED));
        var table = new Table<String>("MEMBER", "CHECK IN", "CHECK OUT", "DURATION");
        for (Visit visit : database.visits(200)) {
            table.getTableModel().addRow(trim(visit.memberName(), 22), visit.checkIn().format(DATE_TIME),
                    visit.open() ? "IN PROGRESS" : visit.checkOut().format(DATE_TIME),
                    visit.open() ? "LIVE" : formatMinutes(visit.duration().toMinutes()));
        }
        var panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(table);
        panel.addComponent(new Button("CLOSE", dialog::close));
        dialog.setComponent(panel);
        gui.addWindowAndWait(dialog);
    }

    private void manageMember() {
        String search = TextInputDialog.showDialog(gui, "Manage member", "Search name, ID or email", "");
        if (search == null) return;
        Member member = chooseMember("SELECT MEMBER", search);
        if (member == null) return;
        var dialog = new BasicWindow(" " + member.fullName().toUpperCase(Locale.ROOT) + " ");
        dialog.setHints(List.of(Window.Hint.CENTERED));
        var panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("ID      " + member.id()).setForegroundColor(CYAN));
        panel.addComponent(new Label("PLAN    " + member.plan()));
        panel.addComponent(new Label("EMAIL   " + (member.email().isBlank() ? "—" : member.email())));
        panel.addComponent(new Label("JOINED  " + member.joinedOn()));
        panel.addComponent(new EmptySpace(new com.googlecode.lanterna.TerminalSize(1, 1)));
        panel.addComponent(new Button(member.active() ? "PAUSE MEMBERSHIP" : "RESUME MEMBERSHIP", () -> {
            database.setActive(member.id(), !member.active());
            notice = "MEMBERSHIP UPDATED · " + member.fullName();
            dialog.close();
        }));
        panel.addComponent(new Button("DELETE MEMBER", () -> {
            if (MessageDialog.showMessageDialog(gui, "Confirm deletion",
                    "Delete " + member.fullName() + " and all visit history?",
                    MessageDialogButton.Yes, MessageDialogButton.No) == MessageDialogButton.Yes) {
                database.deleteMember(member.id());
                notice = "MEMBER DELETED · " + member.id();
                dialog.close();
            }
        }));
        panel.addComponent(new Button("CLOSE", dialog::close));
        dialog.setComponent(panel.withBorder(Borders.singleLine(" PROFILE ")));
        gui.addWindowAndWait(dialog);
    }

    private void exportCsv() {
        Path file = dataDirectory().resolve("exports").resolve("attendance-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".csv");
        try {
            Files.createDirectories(file.getParent());
            StringBuilder csv = new StringBuilder("member_id,member,check_in,check_out,duration_minutes\n");
            for (Visit visit : database.visits(10_000)) {
                csv.append(csv(visit.memberId())).append(',').append(csv(visit.memberName())).append(',')
                        .append(visit.checkIn()).append(',').append(visit.checkOut() == null ? "" : visit.checkOut())
                        .append(',').append(visit.duration().toMinutes()).append('\n');
            }
            Files.writeString(file, csv.toString(), StandardCharsets.UTF_8);
            notice = "CSV EXPORTED · " + file;
            message("Export complete", file.toString());
        } catch (IOException error) { throw new IllegalStateException("Could not export attendance.", error); }
    }

    private void showHelp() {
        message("ESI-FIT Console " + VERSION, """
                KEYBOARD
                  Tab / arrows   move between controls
                  Enter          activate selection
                  Escape         close a dialog

                DATA
                  Local H2 database in the application-data folder
                  Legacy clients.txt and sessions.txt import automatically
                  No cloud, analytics, account or network service

                CREW
                  Enrico · Islam · Stephane

                THOR // console orbit stable.""");
    }

    private void message(String title, String text) {
        MessageDialog.showMessageDialog(gui, title, text == null ? "Unknown error." : text, MessageDialogButton.OK);
    }

    private static Path dataDirectory() {
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            String local = System.getenv("LOCALAPPDATA");
            return Path.of(local == null ? home : local, "ESI-FIT Console");
        }
        if (os.contains("mac")) return Path.of(home, "Library", "Application Support", "ESI-FIT Console");
        String xdg = System.getenv("XDG_DATA_HOME");
        return Path.of(xdg == null || xdg.isBlank() ? Path.of(home, ".local", "share").toString() : xdg, "esi-fit-console");
    }

    private static String formatMinutes(long minutes) {
        if (minutes <= 0) return "—";
        return minutes >= 60 ? (minutes / 60) + "h " + (minutes % 60) + "m" : minutes + "m";
    }
    private static String trim(String text, int length) { return text.length() <= length ? text : text.substring(0, length - 1) + "…"; }
    private static String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }

    public static void main(String[] args) throws Exception {
        if (List.of(args).contains("--diagnostics")) {
            var probe = new Database("jdbc:h2:mem:esi_console_diagnostics;DB_CLOSE_DELAY=-1");
            Member member = probe.addMember("Thor", "Console", "", "Core");
            if (!probe.checkIn(member.id()) || probe.metrics().checkedIn() != 1)
                throw new IllegalStateException("Attendance diagnostic failed.");
            System.out.println("ESI-FIT Console " + VERSION + " · systems nominal");
            return;
        }
        Database database = new Database(dataDirectory());
        int imported = database.importLegacy(Path.of("").toAbsolutePath());
        var app = new App(database);
        if (imported > 0) app.notice = "LEGACY ARCHIVE RECOVERED · " + imported + " member(s)";
        app.run();
    }
}
