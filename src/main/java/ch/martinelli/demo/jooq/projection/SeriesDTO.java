package ch.martinelli.demo.jooq.projection;

import java.util.List;

public record SeriesDTO(String name, List<CompetitionDTO> competitions) {
}
