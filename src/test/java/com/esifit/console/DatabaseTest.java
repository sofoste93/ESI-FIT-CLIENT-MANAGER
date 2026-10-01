package com.esifit.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    @Test void managesMemberAndAttendanceLifecycle() {
        var database = new Database("jdbc:h2:mem:lifecycle;DB_CLOSE_DELAY=-1");
        Member member = database.addMember("Enrico", "Dück", "enrico@example.test", "Core");
        assertEquals(1, database.memberCount());
        assertTrue(database.checkIn(member.id()));
        assertFalse(database.checkIn(member.id()));
        assertEquals(1, database.metrics().checkedIn());
        assertTrue(database.checkOut(member.id()));
        assertFalse(database.checkOut(member.id()));
        assertEquals(0, database.metrics().checkedIn());
    }

    @Test void searchesAndPausesMembers() {
        var database = new Database("jdbc:h2:mem:search;DB_CLOSE_DELAY=-1");
        Member member = database.addMember("Islam", "Nasif", "islam@example.test", "Unlimited");
        assertEquals(1, database.members("nas").size());
        database.setActive(member.id(), false);
        assertFalse(database.member(member.id()).active());
        assertFalse(database.checkIn(member.id()));
    }

    @Test void migratesLegacyTextArchive(@TempDir Path directory) throws Exception {
        Files.writeString(directory.resolve("clients.txt"), "224EnDü | Enrico | Dück\n289IsNa | Islam | Nasif\n");
        Files.writeString(directory.resolve("sessions.txt"),
                "224EnDü | 2023-06-12T12:15 | 2023-06-12T14:13\n");
        var database = new Database("jdbc:h2:mem:migration;DB_CLOSE_DELAY=-1");
        assertEquals(2, database.importLegacy(directory));
        assertEquals(2, database.memberCount());
        assertEquals(1, database.visits(20).size());
        assertEquals(0, database.importLegacy(directory));
    }
}
