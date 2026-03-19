package ch.martinelli.demo.jooq.service;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ch.martinelli.demo.jooq.database.tables.Category.CATEGORY;
import static ch.martinelli.demo.jooq.database.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.martinelli.demo.jooq.database.tables.Competition.COMPETITION;
import static ch.martinelli.demo.jooq.database.tables.Result.RESULT;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.*;

// @formatter:off
@Service
public class CompetitionRankingService {

    private final DSLContext dslContext;

    public CompetitionRankingService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Optional<CompetitionRankingData> getCompetitionRanking(Long competitionId) {
        return dslContext
            .select(
                COMPETITION.NAME,
                COMPETITION.COMPETITION_DATE,
                COMPETITION.ALWAYS_FIRST_THREE_MEDALS,
                COMPETITION.MEDAL_PERCENTAGE,
                multiset(
                    select(
                        CATEGORY.ABBREVIATION,
                        CATEGORY.NAME,
                        CATEGORY.YEAR_FROM,
                        CATEGORY.YEAR_TO,
                        multiset(
                            select(
                                CATEGORY_ATHLETE.athlete().FIRST_NAME,
                                CATEGORY_ATHLETE.athlete().LAST_NAME,
                                CATEGORY_ATHLETE.athlete().YEAR_OF_BIRTH,
                                CATEGORY_ATHLETE.athlete().club().NAME,
                                multiset(
                                    select(
                                        RESULT.event().ABBREVIATION,
                                        RESULT.RESULT_,
                                        RESULT.POINTS
                                    )
                                        .from(RESULT)
                                        .where(RESULT.ATHLETE_ID.eq(CATEGORY_ATHLETE.athlete().ID))
                                        .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                                        .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                                        .orderBy(RESULT.POSITION)
                                ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category.Athlete.Result::new)))
                            )
                                .from(CATEGORY_ATHLETE)
                                .where(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                        ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category.Athlete::new)))
                    )
                        .from(CATEGORY)
                        .where(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                        .orderBy(CATEGORY.ABBREVIATION)
                ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category::new)))
            )
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOptional(mapping(CompetitionRankingData::new));
    }

}
