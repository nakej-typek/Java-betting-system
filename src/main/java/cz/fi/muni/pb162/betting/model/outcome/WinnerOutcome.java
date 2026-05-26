package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.MatchResult;

import java.util.Objects;

/**
 * Outcome representing a specific participant winning a head-to-head match.
 * Applicable to both player and team matches.
 *
 * @param winner the participant predicted or confirmed to win
 * @author Marek Jargaš
 */
public record WinnerOutcome(Participant winner) implements Outcome {

    @Override
    public boolean matches(CompetitionResult result) {
        if (result instanceof MatchResult) {
            return Objects.equals(this, ((MatchResult) result).outcome());
        }
        return false;
    }
}
