package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementOutcomeTest {

    private final Team teamA = new Team("A", "Team A");
    private final Team teamB = new Team("B", "Team B");
    private final Team teamC = new Team("C", "Team C");

    @Test
    void matchesStandingsAtCorrectPlacement() {
        PlacementOutcome outcome = new PlacementOutcome(teamB, 2);
        StandingsResult result = new StandingsResult(List.<Participant>of(teamA, teamB, teamC));
        assertTrue(outcome.matches(result));
    }

    @Test
    void doesNotMatchStandingsAtWrongPlacement() {
        PlacementOutcome outcome = new PlacementOutcome(teamB, 1);
        StandingsResult result = new StandingsResult(List.<Participant>of(teamA, teamB, teamC));
        assertFalse(outcome.matches(result));
    }

    @Test
    void matchesRaceResultByFinishOrder() {
        PlacementOutcome outcome = new PlacementOutcome(teamA, 1);
        RaceResult result = new RaceResult(List.<Participant>of(teamA, teamB), Map.of());
        assertTrue(outcome.matches(result));
    }

    @Test
    void doesNotMatchUnrelatedResultType() {
        PlacementOutcome outcome = new PlacementOutcome(teamA, 1);
        MatchResult result = new MatchResult(new WinnerOutcome(teamA));
        assertFalse(outcome.matches(result));
    }
}
