package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.MatchResult;

import java.util.Objects;

/**
 * Outcome representing a draw in a team match.
 * Not in 1V1 player matches.
 *
 * @author Marek Jargaš
 */
public record DrawOutcome() implements Outcome {

    @Override
    public boolean matches(CompetitionResult result) {
        if (result instanceof MatchResult) {
            return Objects.equals(this, ((MatchResult) result).outcome());
        }
        return false;
    }
}
