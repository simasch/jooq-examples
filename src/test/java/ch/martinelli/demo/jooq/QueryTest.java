package ch.martinelli.demo.jooq;

import ch.martinelli.demo.jooq.database.tables.records.AthleteRecord;
import ch.martinelli.demo.jooq.database.tables.records.CompetitionRecord;
import ch.martinelli.demo.jooq.projection.*;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static ch.martinelli.demo.jooq.database.tables.Athlete.ATHLETE;
import static ch.martinelli.demo.jooq.database.tables.Club.CLUB;
import static ch.martinelli.demo.jooq.database.tables.Competition.COMPETITION;
import static ch.martinelli.demo.jooq.database.tables.Series.SERIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.*;

@Transactional
@JooqTest
public class QueryTest {

    @Autowired
    private DSLContext dsl;

    @Test
    void find_competitions() {
        Result<CompetitionRecord> competitions = dsl
                .selectFrom(COMPETITION)
                .fetch();

        assertThat(competitions).hasSize(1);
    }

    @Test
    void insert_athlete() {
        Long id = dsl.insertInto(ATHLETE)
                .columns(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME,
                        ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH,
                        ATHLETE.CLUB_ID, ATHLETE.ORGANIZATION_ID)
                .values("Mujinga", "Kambundji", "f", 1992, 1L, 1L)
                .returningResult(ATHLETE.ID)
                .fetchOneInto(Long.class);

        assertThat(id).isEqualTo(1);
    }

    @Test
    void updatable_record_insert() {
        AthleteRecord athlete = dsl.newRecord(ATHLETE);
        athlete.setFirstName("Mujinga");
        athlete.setLastName("Kambundji");
        athlete.setGender("f");
        athlete.setYearOfBirth(1992);
        athlete.setClubId(1L);
        athlete.setOrganizationId(1L);

        athlete.store();

        assertThat(athlete.getId()).isNotNull();
    }

    @Test
    void updatable_record_update() {
        Optional<AthleteRecord> optionalAthlete = dsl
                .selectFrom(ATHLETE)
                .where(ATHLETE.ID.eq(1000L))
                .fetchOptional();

        assertThat(optionalAthlete).isPresent();

        AthleteRecord athlete = optionalAthlete.get();
        athlete.setYearOfBirth(2000);
        athlete.store();

        AthleteRecord savedAthlete = dsl
                .selectFrom(ATHLETE)
                .where(ATHLETE.ID.eq(1000L))
                .fetchOptional()
                .orElseThrow();

        assertThat(savedAthlete.getYearOfBirth()).isEqualTo(2000);
    }

    @Test
    void projection() {
        Result<Record3<String, String, String>> athletes = dsl
                .select(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, CLUB.NAME)
                .from(ATHLETE)
                .join(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                .fetch();

        assertThat(athletes).hasSize(1);
        assertThat(athletes.getFirst()).satisfies(athlete -> {
            assertThat(athlete.get(ATHLETE.FIRST_NAME)).isEqualTo("Armand");
            assertThat(athlete.get(ATHLETE.LAST_NAME)).isEqualTo("Duplantis");
            assertThat(athlete.get(CLUB.NAME))
                    .isEqualTo("Louisiana State University");
        });
    }

    @Test
    void projection_using_java_record_with_mapping() {
        List<AthleteDTO> athletes = dsl
                .select(asterisk())
                .from(ATHLETE)
                .join(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                .fetch(record -> new AthleteDTO(
                        record.get(ATHLETE.FIRST_NAME),
                        record.get(ATHLETE.LAST_NAME),
                        record.get(CLUB.NAME))
                );

        assertThat(athletes).hasSize(1);
        assertThat(athletes.getFirst()).satisfies(athlete -> {
            assertThat(athlete.firstName()).isEqualTo("Armand");
            assertThat(athlete.lastName()).isEqualTo("Duplantis");
            assertThat(athlete.clubName())
                    .isEqualTo("Louisiana State University");
        });
    }

    @Test
    void projection_using_java_record_fetch_into() {
        List<AthleteDTO> athletes = dsl
                .select(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, CLUB.NAME)
                .from(ATHLETE)
                .join(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                .fetchInto(AthleteDTO.class);

        assertThat(athletes).hasSize(1);
        assertThat(athletes.getFirst()).satisfies(athlete -> {
            assertThat(athlete.firstName()).isEqualTo("Armand");
            assertThat(athlete.lastName()).isEqualTo("Duplantis");
            assertThat(athlete.clubName())
                    .isEqualTo("Louisiana State University");
        });
    }

    @Test
    void implicit_join() {
        List<AthleteDTO> athletes = dsl
                .select(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, ATHLETE.club().NAME)
                .from(ATHLETE)
                .fetchInto(AthleteDTO.class);

        assertThat(athletes).hasSize(1);
        assertThat(athletes.getFirst()).satisfies(athlete -> {
            assertThat(athlete.firstName()).isEqualTo("Armand");
            assertThat(athlete.lastName()).isEqualTo("Duplantis");
            assertThat(athlete.clubName())
                    .isEqualTo("Louisiana State University");
        });
    }

    @Test
    void nested_result() {
        List<NestedAthleteDTO> athletes = dsl
                .select(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME,
                        row(ATHLETE.club().NAME).mapping(ClubDTO::new))
                .from(ATHLETE)
                .fetchInto(NestedAthleteDTO.class);

        assertThat(athletes).hasSize(1);
        assertThat(athletes.getFirst()).satisfies(athlete -> {
            assertThat(athlete.firstName()).isEqualTo("Armand");
            assertThat(athlete.lastName()).isEqualTo("Duplantis");
            assertThat(athlete.club().name())
                    .isEqualTo("Louisiana State University");
        });
    }

    @Test
    void projection_multiset() {
        List<SeriesDTO> series = dsl
                .select(SERIES.NAME,
                        multiset(dsl
                                .select(COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
                                .from(COMPETITION)
                                .where(COMPETITION.SERIES_ID.eq(SERIES.ID))
                        ).convertFrom(r -> r.map(mapping(CompetitionDTO::new))))
                .from(SERIES)
                .fetchInto(SeriesDTO.class);

        assertThat(series).hasSize(1);
        assertThat(series.getFirst()).satisfies(competition -> {
            assertThat(competition.name()).isEqualTo("Diamond League 2022");
        });
    }

    @Test
    void delete() {
        int deletedRows = dsl
                .deleteFrom(ATHLETE)
                .where(ATHLETE.ID.eq(1000L))
                .execute();

        assertThat(deletedRows).isEqualTo(1);

        Optional<AthleteRecord> optionalAthlete = dsl
                .selectFrom(ATHLETE)
                .where(ATHLETE.ID.eq(1000L))
                .fetchOptional();

        assertThat(optionalAthlete).isEmpty();
    }
}
