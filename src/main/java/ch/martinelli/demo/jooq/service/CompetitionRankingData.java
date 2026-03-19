package ch.martinelli.demo.jooq.service;

import java.time.LocalDate;
import java.util.List;

public record CompetitionRankingData(String name, LocalDate competitionDate, boolean alwaysFirstThreeMedals,
                                     int medalPercentage, List<Category> categories) {

    public record Category(String abbreviation, String name, int yearFrom, int yearTo, List<Athlete> athletes) {

        public record Athlete(String firstName, String lastName, int yearOfBirth, String club,
                              List<Result> results) {

            public record Result(String eventAbbreviation, String result, int points) {
            }

        }

    }

}
