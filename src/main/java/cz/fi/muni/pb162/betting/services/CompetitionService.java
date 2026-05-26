package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.KnockoutBracket;
import cz.fi.muni.pb162.betting.model.competition.Match;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;

import java.util.List;

/**
 * Service for creating and managing sport competitions and recording their results.
 *
 * @author Marek Jargaš
 */
public interface CompetitionService {

    /**
     * Creates and registers a new sport event.
     */
    SportEvent createSportEvent(String id, String name);

    /**
     * Creates and registers a new sport.
     */
    Sport createSport(String id, String name);

    /**
     * Creates a head-to-head match between two participants.
     */
    Match createMatch(String id, Participant home, Participant away, SportEvent event, Sport sport);

    /**
     * Creates a race with the given participants.
     */
    Race createRace(String id, List<Participant> participants, SportEvent event, Sport sport);

    /**
     * Creates a group stage with the given participants.
     */
    GroupStage createGroupStage(String id, List<Participant> participants, SportEvent event, Sport sport);

    /**
     * Creates a knockout bracket from the given matches.
     */
    KnockoutBracket createKnockoutBracket(String id, List<Match> matches, SportEvent event, Sport sport);

    /**
     * Records the final result of a match.
     */
    void recordResult(Match match, MatchResult result);

    /**
     * Records the final result of a race.
     */
    void recordResult(Race race, RaceResult result);

    /**
     * Records the final standings of a group stage.
     */
    void recordResult(GroupStage stage, StandingsResult result);

    /**
     * Records the final standings of a knockout bracket.
     */
    void recordResult(KnockoutBracket bracket, StandingsResult result);

    /**
     * Cancels a competition of any type. Sets its status to CANCELLED.
     * All bets should be refunded via ResultEvaluatorService.
     */
    void cancelCompetition(Competition competition, CancelledResult result);

    /**
     * Returns all competitions belonging to the given sport event.
     */
    List<Competition> getCompetitions(SportEvent event);

    /**
     * Returns the registered sport event with the given id, or {@code null} if none exists.
     *
     * @param id sport event id
     * @return sport event with the given id or {@code null}
     */
    SportEvent getSportEvent(String id);

    /**
     * Returns the registered sport with the given id, or null  if none exists.
     *
     * @param id sport id
     * @return sport with the given id or null
     */
    Sport getSport(String id);

    /**
     * Returns the registered competition with the given id, or {@code null} if none exists.
     *
     * @param id competition id
     * @return competition with the given id or {@code null}
     */
    Competition getCompetition(String id);

    /**
     * Persists the current state (sport events and competitions) via the underlying repository.
     */
    void saveAll();

    /**
     * Replaces the current in-memory state with the snapshot loaded from the underlying repository.
     */
    void loadAll();
}
