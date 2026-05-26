package cz.fi.muni.pb162.betting.model.result;

import cz.fi.muni.pb162.betting.model.outcome.Outcome;

/**
 * Represents result of a 1V1 match.
 *
 * @param outcome the actual outcome of the match
 * @author Marek Jargaš
 */
public record MatchResult(Outcome outcome) implements CompetitionResult {
}
