package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;

import java.util.List;

/**
 * Contract for all competition types that can be bet on (1V1, Team vs Team, race, group stage ...).
 *
 * @author Marek Jargaš
 */
public interface Competition {

    /**
     * Returns the unique identifier of this competition.
     *
     * @return id of the competition
     */
    String getId();

    /**
     * Returns the name of this competition.
     *
     * @return name of the competition
     */
    String getName();

    /**
     * Returns the current status of this competition.
     *
     * @return status of the competition
     */
    CompetitionStatus getStatus();

    /**
     * Returns the sport event this competition belongs to for example - "Ice Hockey World Championship 2026".
     *
     * @return sport event
     */
    SportEvent getSportEvent();

    /**
     * Returns all participants who taking part in this competition.
     *
     * @return list of participants
     */
    List<Participant> getParticipants();

    /**
     * Returns the sport this competition belongs to.
     *
     * @return non-null sport
     */
    Sport getSport();

    /**
     * Returns the result of this competition.
     * Available only once it has been recorded (typically when the competition
     * is FINISHED or CANCELLED).
     *
     * @return competition result, or {@code null} if not yet available
     */
    CompetitionResult getResult();

    /**
     * Cancels this competition, stores the cancellation result and sets status to CANCELLED.
     *
     * @param result the cancellation result with the reason
     */
    void cancel(CancelledResult result);
}
