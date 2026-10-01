package com.esifit.console;

import java.time.Duration;
import java.time.LocalDateTime;

public record Visit(long id, String memberId, String memberName, LocalDateTime checkIn, LocalDateTime checkOut) {
    public boolean open() { return checkOut == null; }
    public Duration duration() { return Duration.between(checkIn, open() ? LocalDateTime.now() : checkOut); }
}
