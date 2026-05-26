package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Player;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.ParticipantRaceStatus;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WithdrawalOutcomeTest {

    private final Player runnerA = new Player("A", "Runner A");
    private final Player runnerB = new Player("B", "Runner B");

    @Test
    void matchesWhenParticipantDidNotStart() {
        WithdrawalOutcome outcome = new WithdrawalOutcome(runnerA);
        RaceResult result = new RaceResult(
                List.<Participant>of(runnerB),
                Map.of(runnerA, ParticipantRaceStatus.DID_NOT_START,
                        runnerB, ParticipantRaceStatus.FINISHED));
        assertTrue(outcome.matches(result));
    }

    @Test
    void matchesWhenParticipantDidNotFinish() {
        WithdrawalOutcome outcome = new WithdrawalOutcome(runnerA);
        RaceResult result = new RaceResult(
                List.<Participant>of(runnerB),
                Map.of(runnerA, ParticipantRaceStatus.DID_NOT_FINISH,
                        runnerB, ParticipantRaceStatus.FINISHED));
        assertTrue(outcome.matches(result));
    }

    @Test
    void doesNotMatchWhenParticipantFinished() {
        WithdrawalOutcome outcome = new WithdrawalOutcome(runnerA);
        RaceResult result = new RaceResult(
                List.<Participant>of(runnerA, runnerB),
                Map.of(runnerA, ParticipantRaceStatus.FINISHED,
                        runnerB, ParticipantRaceStatus.FINISHED));
        assertFalse(outcome.matches(result));
    }

    @Test
    void doesNotMatchWhenParticipantDisqualified() {
        WithdrawalOutcome outcome = new WithdrawalOutcome(runnerA);
        RaceResult result = new RaceResult(
                List.<Participant>of(runnerB),
                Map.of(runnerA, ParticipantRaceStatus.DISQUALIFIED,
                        runnerB, ParticipantRaceStatus.FINISHED));
        assertFalse(outcome.matches(result));
    }

    @Test
    void doesNotMatchUnrelatedResultType() {
        WithdrawalOutcome outcome = new WithdrawalOutcome(runnerA);
        MatchResult result = new MatchResult(new WinnerOutcome(runnerB));
        assertFalse(outcome.matches(result));
    }
}
