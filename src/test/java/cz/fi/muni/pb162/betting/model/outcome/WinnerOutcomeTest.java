package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WinnerOutcomeTest {

    private final Team teamA = new Team("A", "Team A");
    private final Team teamB = new Team("B", "Team B");

    @Test
    void matchesWhenWinnerInResultEquals() {
        WinnerOutcome outcome = new WinnerOutcome(teamA);
        MatchResult result = new MatchResult(new WinnerOutcome(teamA));
        assertTrue(outcome.matches(result));
    }

    @Test
    void doesNotMatchWhenWinnerDiffers() {
        WinnerOutcome outcome = new WinnerOutcome(teamA);
        MatchResult result = new MatchResult(new WinnerOutcome(teamB));
        assertFalse(outcome.matches(result));
    }

    @Test
    void doesNotMatchDrawResult() {
        WinnerOutcome outcome = new WinnerOutcome(teamA);
        MatchResult result = new MatchResult(new DrawOutcome());
        assertFalse(outcome.matches(result));
    }

    @Test
    void doesNotMatchUnrelatedResultType() {
        WinnerOutcome outcome = new WinnerOutcome(teamA);
        RaceResult result = new RaceResult(List.of(teamA, teamB), Map.of());
        assertFalse(outcome.matches(result));
    }
}
