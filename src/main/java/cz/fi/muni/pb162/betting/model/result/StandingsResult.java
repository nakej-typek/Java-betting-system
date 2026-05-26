package cz.fi.muni.pb162.betting.model.result;

import cz.fi.muni.pb162.betting.model.participant.Participant;

import java.util.List;

/**
 * Represents standings result for competitions that produce a final ranking.
 * Used by both GroupStage and KnockoutBracket.
 *
 * @param standings participants ordered by their final placement
 * @author Marek Jargaš
 */
public record StandingsResult(List<Participant> standings) implements CompetitionResult {
}