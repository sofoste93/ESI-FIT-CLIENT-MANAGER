package com.esifit.console;

import java.time.LocalDate;

public record Member(String id, String firstName, String lastName, String email, String plan,
                     LocalDate joinedOn, boolean active) {
    public String fullName() { return firstName + " " + lastName; }
}
