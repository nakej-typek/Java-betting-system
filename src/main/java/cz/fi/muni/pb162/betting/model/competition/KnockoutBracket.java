package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.bet.Bettable;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a knockout bracket where participants are eliminated after a loss.
 * Ranking is determined only for the top 4 positions.
 * You cannot bet on 5th and worse placements.
 *
 * @author Marek Jargaš
 */
public class KnockoutBracket extends AbstractCompetition<StandingsResult>
        implements RankableCompetition, Bettable {

    private static final int RANKED_PLACES = 4;

    private final List<Match> matches;

    /**
     * Creates a new knockout bracket from the given matches.
     *
     * @param id         unique identifier
     * @param matches    list of matches in the bracket
     * @param sportEvent sport event this bracket belongs to
     * @param sport      sport played in this bracket
     */
    public KnockoutBracket(String id, List<Match> matches, SportEvent sportEvent, Sport sport) {
        super(id, collectParticipants(matches), sportEvent, sport);
        this.matches = List.copyOf(matches);
    }

    private static List<Participant> collectParticipants(List<Match> matches) {
        List<Participant> all = new ArrayList<>();
        for (Match match : matches) {
            for (Participant p : match.getParticipants()) {
                if (!all.contains(p)) {
                    all.add(p);
                }
            }
        }
        return all;
    }

    public List<Match> getMatches() {
        return matches;
    }

    /**
     * Returns ranking only for the top 4 participants.
     *
     * @return list of at most 4 participants ordered by placement
     */
    @Override
    public List<Participant> getRanking() {
        if (getResult() instanceof StandingsResult sr) {
            int limit = Math.min(RANKED_PLACES, sr.standings().size());
            return Collections.unmodifiableList(sr.standings().subList(0, limit));
        }
        return List.of();
    }
}
