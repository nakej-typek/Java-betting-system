package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.competition.AbstractCompetition;
import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.competition.CompetitionStatus;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.KnockoutBracket;
import cz.fi.muni.pb162.betting.model.competition.Match;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;
import cz.fi.muni.pb162.betting.services.storage.CompetitionRepository;
import cz.fi.muni.pb162.betting.services.storage.PersistenceSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of CompetitionService backed by a CompetitionRepository
 * for persistence.
 *
 * @author Marek Jargaš
 */
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository repository;
    private final Map<String, Sport> sports = new LinkedHashMap<>();
    private final Map<String, SportEvent> sportEvents = new LinkedHashMap<>();
    private final Map<String, Competition> competitions = new LinkedHashMap<>();

    /**
     * Creates the service backed by the given repository for save/load operations.
     *
     * @param repository repository used by {@link #saveAll()} and {@link #loadAll()}
     */
    public CompetitionServiceImpl(CompetitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public SportEvent createSportEvent(String id, String name) {
        SportEvent event = new SportEvent(id, name);
        sportEvents.put(id, event);
        return event;
    }

    @Override
    public Sport createSport(String id, String name) {
        Sport sport = new Sport(id, name);
        sports.put(id, sport);
        return sport;
    }

    @Override
    public Sport getSport(String id) {
        return sports.get(id);
    }

    @Override
    public Match createMatch(String id, Participant home, Participant away, SportEvent event, Sport sport) {
        Match match = new Match(id, home, away, event, sport);
        competitions.put(id, match);
        return match;
    }

    @Override
    public Race createRace(String id, List<Participant> participants, SportEvent event, Sport sport) {
        Race race = new Race(id, participants, event, sport);
        competitions.put(id, race);
        return race;
    }

    @Override
    public GroupStage createGroupStage(String id, List<Participant> participants, SportEvent event, Sport sport) {
        GroupStage stage = new GroupStage(id, participants, event, sport);
        competitions.put(id, stage);
        return stage;
    }

    @Override
    public KnockoutBracket createKnockoutBracket(String id, List<Match> matches, SportEvent event, Sport sport) {
        KnockoutBracket bracket = new KnockoutBracket(id, matches, event, sport);
        competitions.put(id, bracket);
        return bracket;
    }

    @Override
    public void recordResult(Match match, MatchResult result) {
        match.setResult(result);
        match.setStatus(CompetitionStatus.FINISHED);
    }

    @Override
    public void recordResult(Race race, RaceResult result) {
        race.setResult(result);
        race.setStatus(CompetitionStatus.FINISHED);
    }

    @Override
    public void recordResult(GroupStage stage, StandingsResult result) {
        stage.setResult(result);
        stage.setStatus(CompetitionStatus.FINISHED);
    }

    @Override
    public void recordResult(KnockoutBracket bracket, StandingsResult result) {
        bracket.setResult(result);
        bracket.setStatus(CompetitionStatus.FINISHED);
    }

    @Override
    public void cancelCompetition(Competition competition, CancelledResult result) {
        competition.cancel(result);
    }

    @Override
    public List<Competition> getCompetitions(SportEvent event) {
        List<Competition> result = new ArrayList<>();
        for (Competition competition : competitions.values()) {
            if (Objects.equals(competition.getSportEvent(), event)) {
                result.add(competition);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public SportEvent getSportEvent(String id) {
        return sportEvents.get(id);
    }

    @Override
    public Competition getCompetition(String id) {
        return competitions.get(id);
    }

    @Override
    public void saveAll() {
        Map<String, CompetitionResult> resultsById = new LinkedHashMap<>();
        for (Competition competition : competitions.values()) {
            CompetitionResult result = extractResult(competition);
            if (result != null) {
                resultsById.put(competition.getId(), result);
            }
        }
        PersistenceSnapshot snapshot = new PersistenceSnapshot(
                new ArrayList<>(sports.values()),
                new ArrayList<>(sportEvents.values()),
                new ArrayList<>(competitions.values()),
                resultsById
        );
        repository.saveAll(snapshot);
    }

    @Override
    public void loadAll() {
        PersistenceSnapshot snapshot = repository.loadAll();
        sports.clear();
        sportEvents.clear();
        competitions.clear();
        for (Sport sport : snapshot.sports()) {
            sports.put(sport.getId(), sport);
        }
        for (SportEvent event : snapshot.sportEvents()) {
            sportEvents.put(event.getId(), event);
        }
        for (Competition competition : snapshot.competitions()) {
            competitions.put(competition.getId(), competition);
        }
        snapshot.resultsByCompetitionId().forEach((id, result) -> {
            Competition competition = competitions.get(id);
            if (competition instanceof AbstractCompetition<?> ac) {
                ac.setResult(result);
            }
        });
    }

    private static CompetitionResult extractResult(Competition competition) {
        if (competition instanceof Match m) {
            return m.getResult();
        }
        if (competition instanceof Race r) {
            return r.getResult();
        }
        if (competition instanceof GroupStage g) {
            return g.getResult();
        }
        if (competition instanceof KnockoutBracket k) {
            return k.getResult();
        }
        return null;
    }
}
