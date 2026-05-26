package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.bet.Bettable;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.MatchResult;

import java.util.List;

/**
 * Represents a 1V1 competition (players or teams), (Sinner vs. Alcaraz, Barcelona vs. Real Madrid).
 * Player matches cannot end in a draw; team matches can.
 * Whether a draw outcome is offered is the responsibility of the BettingBrokerService,
 * which should only add "DrawOutcome" to markets for Team vs Team matches.
 *
 * @author Marek Jargaš
 */
public class Match extends AbstractCompetition<MatchResult> implements Bettable {

    private final Participant home;
    private final Participant away;

    /**
     * Creates a new match between home and away participant.
     *
     * @param id         unique identifier
     * @param home       home participant
     * @param away       away participant
     * @param sportEvent sport event this match belongs to
     * @param sport      sport played in this match
     */
    public Match(String id, Participant home, Participant away, SportEvent sportEvent, Sport sport) {
        super(id, List.of(home, away), sportEvent, sport);
        this.home = home;
        this.away = away;
    }

    /**
     * Returns the home participant.
     *
     * @return home participant
     */
    public Participant getHome() {
        return home;
    }

    /**
     * Returns the away participant.
     *
     * @return away participant
     */
    public Participant getAway() {
        return away;
    }

    @Override
    public String getName() {
        return home.getName() + " vs " + away.getName();
    }
}