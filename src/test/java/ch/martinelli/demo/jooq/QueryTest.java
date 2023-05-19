package ch.martinelli.demo.jooq;

import ch.martinelli.demo.jooq.database.tables.records.AthleteRecord;
import ch.martinelli.demo.jooq.database.tables.records.CompetitionRecord;
import ch.martinelli.demo.jooq.projection.AthleteDTO;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.martinelli.demo.jooq.database.tables.Athlete.ATHLETE;
import static ch.martinelli.demo.jooq.database.tables.Club.CLUB;
import static ch.martinelli.demo.jooq.database.tables.Competition.COMPETITION;
import static org.assertj.core.api.Assertions.assertThat;

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
                .columns(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH, ATHLETE.CLUB_ID, ATHLETE.ORGANIZATION_ID)
                .values("Sanya", "Richards-Ross", "f", 1985, 1L, 1L)
                .returningResult(ATHLETE.ID)
                .fetchOneInto(Long.class);

        assertThat(id).isEqualTo(1);
    }

    @Test
    void updatable_record() {
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
    void projection() {
        List<AthleteDTO> athletes = dsl
                .select(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, CLUB.NAME)
                .from(ATHLETE)
                .join(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                .fetchInto(AthleteDTO.class);

        assertThat(athletes).hasSize(1);
        assertThat(athletes.get(0)).satisfies(athlete -> {
            assertThat(athlete.firstName()).isEqualTo("Armand");
            assertThat(athlete.lastName()).isEqualTo("Duplantis");
            assertThat(athlete.clubName()).isEqualTo("Louisiana State University");
        });
    }

    @Test
    void implicit_join() {
        List<AthleteDTO> athletes = dsl
                .select(ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, ATHLETE.club().NAME)
                .from(ATHLETE)
                .fetchInto(AthleteDTO.class);

        assertThat(athletes).hasSize(1);
        assertThat(athletes.get(0)).satisfies(athlete -> {
            assertThat(athlete.firstName()).isEqualTo("Armand");
            assertThat(athlete.lastName()).isEqualTo("Duplantis");
            assertThat(athlete.clubName()).isEqualTo("Louisiana State University");
        });
    }

    @Test
    void delete() {
        int deletedRows = dsl
                .deleteFrom(ATHLETE)
                .where(ATHLETE.ID.eq(1000L))
                .execute();

        assertThat(deletedRows).isEqualTo(1);
    }
}
