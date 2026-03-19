package ch.martinelli.demo.jooq.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompetitionRankingServiceTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    void getCompetitionRanking() {
        var optionalRanking = competitionRankingService.getCompetitionRanking(1L);

        assertThat(optionalRanking).isPresent();

        var ranking = optionalRanking.get();
        assertThat(ranking.name()).isEqualTo("Athletissima");
        assertThat(ranking.competitionDate()).isEqualTo("2022-08-26");
        assertThat(ranking.alwaysFirstThreeMedals()).isTrue();
        assertThat(ranking.medalPercentage()).isZero();

        assertThat(ranking.categories()).hasSize(1);

        var category = ranking.categories().getFirst();
        assertThat(category.abbreviation()).isEqualTo("MAN");
        assertThat(category.name()).isEqualTo("Maenner");
        assertThat(category.yearFrom()).isEqualTo(1900);
        assertThat(category.yearTo()).isEqualTo(2010);

        assertThat(category.athletes()).hasSize(2);

        var duplantis = category.athletes().stream()
                .filter(a -> a.lastName().equals("Duplantis"))
                .findFirst().orElseThrow();
        assertThat(duplantis.firstName()).isEqualTo("Armand");
        assertThat(duplantis.club()).isEqualTo("Louisiana State University");
        assertThat(duplantis.results()).hasSize(2);
        assertThat(duplantis.results().getFirst().eventAbbreviation()).isEqualTo("100m");
        assertThat(duplantis.results().getFirst().points()).isEqualTo(850);

        var martinelli = category.athletes().stream()
                .filter(a -> a.lastName().equals("Martinelli"))
                .findFirst().orElseThrow();
        assertThat(martinelli.firstName()).isEqualTo("Simon");
        assertThat(martinelli.club()).isEqualTo("Stadtturnverein Bern");
        assertThat(martinelli.results()).hasSize(2);
    }

    @Test
    void getCompetitionRankingNotFound() {
        var optionalRanking = competitionRankingService.getCompetitionRanking(999L);

        assertThat(optionalRanking).isEmpty();
    }
}
