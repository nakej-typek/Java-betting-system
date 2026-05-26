package cz.fi.muni.pb162.betting.model.result;

import cz.fi.muni.pb162.betting.model.participant.Participant;

import java.util.List;
import java.util.Map;

/**
 * Represents result of a race competition.
 *
 * @param finishOrder         ordered list of participants who finished
 * @param participantStatuses status of every participant in the race
 * @author Marek Jargaš
 */
public record RaceResult(
        List<Participant> finishOrder,
        Map<Participant, ParticipantRaceStatus> participantStatuses
) implements CompetitionResult {
}
