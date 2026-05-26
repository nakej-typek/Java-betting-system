package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;

import java.util.List;

/**
 * Common base for competitions that hold a fixed list of participants
 * and produce a single result of type {@code R}.
 *
 * @param <T> type of the competition's result
 * @author Marek Jargaš
 */
public abstract class AbstractCompetition<T extends CompetitionResult> implements Competition {

    private final String id;
    private final List<Participant> participants;
    private final SportEvent sportEvent;
    private final Sport sport;
    private CompetitionStatus status;
    private CompetitionResult result;

    protected AbstractCompetition(String id, List<Participant> participants, SportEvent sportEvent, Sport sport) {
        this.id = id;
        this.participants = List.copyOf(participants);
        this.sportEvent = sportEvent;
        this.sport = sport;
        this.status = CompetitionStatus.UPCOMING;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CompetitionResult getResult() {
        return result;
    }

    public void setResult(CompetitionResult result) {
        this.result = result;
    }

    @Override
    public void cancel(CancelledResult result) {
        this.result = result;
        this.status = CompetitionStatus.CANCELLED;
    }

    public void setStatus(CompetitionStatus status) {
        this.status = status;
    }

    @Override
    public CompetitionStatus getStatus() {
        return status;
    }

    @Override
    public SportEvent getSportEvent() {
        return sportEvent;
    }

    @Override
    public Sport getSport() {
        return sport;
    }

    @Override
    public List<Participant> getParticipants() {
        return participants;
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < participants.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(participants.get(i).getName());
        }
        return sb.toString();
    }
}
