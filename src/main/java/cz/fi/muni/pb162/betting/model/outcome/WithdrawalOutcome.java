package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.ParticipantRaceStatus;
import cz.fi.muni.pb162.betting.model.result.RaceResult;

/**
 * Outcome representing a participant withdrawing from a race
 * (either not starting or not finishing).
 * Applicable only to race competitions.
 *
 * @param participant the participant predicted or confirmed to withdraw
 * @author Jan Prosecký
 */
public record WithdrawalOutcome(Participant participant) implements Outcome {

    @Override
    public boolean matches(CompetitionResult result) {
        if (result instanceof RaceResult) {
            ParticipantRaceStatus status = ((RaceResult) result).participantStatuses().get(participant);
            return status == ParticipantRaceStatus.DID_NOT_START
                    || status == ParticipantRaceStatus.DID_NOT_FINISH;
        }
        return false;
    }
}
