package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.bet.Bettable;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group stage where every participant plays against every other participant.
 * Final ranking is determined by the results of all matches in the group.
 *
 * @author Marek Jargaš
 */
public class GroupStage extends AbstractCompetition<StandingsResult>
        implements RankableCompetition, Bettable {

    private final List<Match> matches;

    /**
     * Creates a group stage and automatically generates all matches
     * between the given participants in a group stage.
     *
     * @param id           unique identifier of this group stage
     * @param participants participants competing in this group stage
     * @param sportEvent   the sport event this group stage belongs to
     * @param sport        the sport which is played
     */
    public GroupStage(String id, List<Participant> participants, SportEvent sportEvent, Sport sport) {
        super(id, participants, sportEvent, sport);
        List<Match> generatedMatches = new ArrayList<>();
        int matchIndex = 0;
        for (int i = 0; i < participants.size(); i++) {
            for (int j = i + 1; j < participants.size(); j++) {
                String matchId = id + "-m" + matchIndex++;
                generatedMatches.add(new Match(matchId, participants.get(i), participants.get(j), sportEvent, sport));
            }
        }
        this.matches = List.copyOf(generatedMatches);
    }

    public List<Match> getMatches() {
        return matches;
    }

    @Override
    public List<Participant> getRanking() {
        if (getResult() instanceof StandingsResult sr) {
            return sr.standings();
        }
        return List.of();
    }
}
