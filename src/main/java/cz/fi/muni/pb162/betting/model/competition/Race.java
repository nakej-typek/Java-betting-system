package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.bet.Bettable;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.RaceResult;

import java.util.List;

/**
 * Represents a competition where many participants compete simultaneously
 * and a full ranking is determined (F1 GP).
 * Participants may not finish due to disqualification, DNS or DNF.
 *
 * @author Marek Jargaš
 */
public class Race extends AbstractCompetition<RaceResult>
        implements RankableCompetition, Bettable {

    /**
     * Creates a new race with the given participants.
     *
     * @param id           unique identifier
     * @param participants list of participants in the race
     * @param sportEvent   sport event this race belongs to
     * @param sport        sport played in this race
     */
    public Race(String id, List<Participant> participants, SportEvent sportEvent, Sport sport) {
        super(id, participants, sportEvent, sport);
    }

    @Override
    public List<Participant> getRanking() {
        if (getResult() instanceof RaceResult rr) {
            return rr.finishOrder();
        }
        return List.of();
    }
}
