package ch.martinelli.demo.jooq.projection;

public record NestedAthleteDTO(String firstName, String lastName, ClubDTO club) {
}
