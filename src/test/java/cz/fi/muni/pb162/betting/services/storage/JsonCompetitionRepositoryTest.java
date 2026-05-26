package cz.fi.muni.pb162.betting.services.storage;

import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.competition.CompetitionStatus;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Match;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.WinnerOutcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Player;
import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.model.result.CancelationReason;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.ParticipantRaceStatus;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;
import cz.fi.muni.pb162.betting.services.storage.json.JsonCompetitionRepository;
import cz.fi.muni.pb162.betting.services.storage.PersistenceSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonCompetitionRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonCompetitionRepository repo() {
        return new JsonCompetitionRepository(tempDir.resolve("data.json"));
    }

    private static SportEvent event(String id) {
        return new SportEvent(id, "Event " + id);
    }

    private static Sport sport(String id) {
        return new Sport(id, "Sport " + id);
    }

    private static Player player(String id) {
        return new Player(id, "Player " + id);
    }

    private static Team team(String id) {
        return new Team(id, "Team " + id);
    }

    @Test
    void emptySnapshotRoundTrips() {
        PersistenceSnapshot empty = new PersistenceSnapshot(List.of(), List.of(), List.of(), Map.of());
        JsonCompetitionRepository r = repo();
        r.saveAll(empty);
        PersistenceSnapshot loaded = r.loadAll();
        assertTrue(loaded.sportEvents().isEmpty());
        assertTrue(loaded.competitions().isEmpty());
        assertTrue(loaded.resultsByCompetitionId().isEmpty());
    }

    @Test
    void sportEventsRoundTrip() {
        SportEvent ev1 = new SportEvent("e1", "Event One");
        SportEvent ev2 = new SportEvent("e2", "Event Two");
        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(ev1, ev2), List.of(), Map.of()));
        PersistenceSnapshot loaded = r.loadAll();
        assertEquals(2, loaded.sportEvents().size());
        assertEquals("e1", loaded.sportEvents().get(0).getId());
        assertEquals("Event One", loaded.sportEvents().get(0).getName());
        assertEquals("e2", loaded.sportEvents().get(1).getId());
    }

    @Test
    void matchWithPlayersRoundTrips() {
        Match match = new Match("m1", player("p1"), player("p2"), event("e1"), sport("s1"));
        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(match), Map.of()));
        PersistenceSnapshot loaded = r.loadAll();

        assertInstanceOf(Match.class, loaded.competitions().get(0));
        Match m = (Match) loaded.competitions().get(0);
        assertEquals("m1", m.getId());
        assertInstanceOf(Player.class, m.getHome());
        assertInstanceOf(Player.class, m.getAway());
        assertEquals("p1", m.getHome().getId());
        assertEquals("p2", m.getAway().getId());
    }

    @Test
    void matchWithTeamsRoundTrips() {
        Match match = new Match("m1", team("t1"), team("t2"), event("e1"), sport("s1"));
        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(match), Map.of()));
        PersistenceSnapshot loaded = r.loadAll();

        Match m = (Match) loaded.competitions().get(0);
        assertInstanceOf(Team.class, m.getHome());
        assertInstanceOf(Team.class, m.getAway());
        assertEquals("t1", m.getHome().getId());
        assertEquals("t2", m.getAway().getId());
    }

    @Test
    void matchResultWithWinnerOutcomeRoundTrips() {
        Team home = team("t1");
        Team away = team("t2");
        Match match = new Match("m1", home, away, event("e1"), sport("s1"));
        MatchResult result = new MatchResult(new WinnerOutcome(home));
        match.setResult(result);
        match.setStatus(CompetitionStatus.FINISHED);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(match), Map.of("m1", result)));
        PersistenceSnapshot loaded = r.loadAll();

        CompetitionResult loadedResult = loaded.resultsByCompetitionId().get("m1");
        assertInstanceOf(MatchResult.class, loadedResult);
        MatchResult mr = (MatchResult) loadedResult;
        assertInstanceOf(WinnerOutcome.class, mr.outcome());
        assertEquals("t1", ((WinnerOutcome) mr.outcome()).winner().getId());
    }

    @Test
    void raceWithPlayerParticipantsRoundTrips() {
        Race race = new Race("r1", List.of(player("p1"), player("p2"), player("p3")), event("e1"), sport("s1"));
        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(race), Map.of()));
        PersistenceSnapshot loaded = r.loadAll();

        assertInstanceOf(Race.class, loaded.competitions().get(0));
        Race loadedRace = (Race) loaded.competitions().get(0);
        assertEquals("r1", loadedRace.getId());
        assertEquals(3, loadedRace.getParticipants().size());
        assertEquals("p1", loadedRace.getParticipants().get(0).getId());
        loadedRace.getParticipants().forEach(p -> assertInstanceOf(Player.class, p));
    }

    @Test
    void raceResultFinishOrderAndStatusesRoundTrip() {
        Player p1 = player("r1");
        Player p2 = player("r2");
        Player p3 = player("r3");
        Race race = new Race("r1", List.of(p1, p2, p3), event("e1"), sport("s1"));

        RaceResult result = new RaceResult(
                List.of(p2, p3, p1),
                Map.of(
                        p1, ParticipantRaceStatus.FINISHED,
                        p2, ParticipantRaceStatus.FINISHED,
                        p3, ParticipantRaceStatus.FINISHED
                )
        );
        race.setResult(result);
        race.setStatus(CompetitionStatus.FINISHED);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(race), Map.of("r1", result)));
        PersistenceSnapshot loaded = r.loadAll();

        RaceResult lr = (RaceResult) loaded.resultsByCompetitionId().get("r1");
        assertNotNull(lr);
        assertEquals(3, lr.finishOrder().size());
        assertEquals("r2", lr.finishOrder().get(0).getId());
        assertEquals("r3", lr.finishOrder().get(1).getId());
        assertEquals("r1", lr.finishOrder().get(2).getId());

        assertEquals(3, lr.participantStatuses().size());
        Participant loadedP1 = lr.participantStatuses().keySet().stream()
                .filter(p -> "r1".equals(p.getId())).findFirst().orElseThrow();
        assertEquals(ParticipantRaceStatus.FINISHED, lr.participantStatuses().get(loadedP1));
    }

    @Test
    void raceResultWithNonFinishedStatusesRoundTrips() {
        Player p1 = player("r1");
        Player p2 = player("r2");
        Player p3 = player("r3");
        Race race = new Race("r1", List.of(p1, p2, p3), event("e1"), sport("s1"));

        RaceResult result = new RaceResult(
                List.of(p1),
                Map.of(
                        p1, ParticipantRaceStatus.FINISHED,
                        p2, ParticipantRaceStatus.DID_NOT_FINISH,
                        p3, ParticipantRaceStatus.DID_NOT_START
                )
        );
        race.setResult(result);
        race.setStatus(CompetitionStatus.FINISHED);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(race), Map.of("r1", result)));
        PersistenceSnapshot loaded = r.loadAll();

        RaceResult lr = (RaceResult) loaded.resultsByCompetitionId().get("r1");
        assertEquals(1, lr.finishOrder().size());
        assertEquals(3, lr.participantStatuses().size());

        Participant loadedP2 = lr.participantStatuses().keySet().stream()
                .filter(p -> "r2".equals(p.getId())).findFirst().orElseThrow();
        Participant loadedP3 = lr.participantStatuses().keySet().stream()
                .filter(p -> "r3".equals(p.getId())).findFirst().orElseThrow();
        assertEquals(ParticipantRaceStatus.DID_NOT_FINISH, lr.participantStatuses().get(loadedP2));
        assertEquals(ParticipantRaceStatus.DID_NOT_START, lr.participantStatuses().get(loadedP3));
    }

    @Test
    void standingsResultRoundTrips() {
        Team t1 = team("t1");
        Team t2 = team("t2");
        GroupStage stage = new GroupStage("gs1", List.of(t1, t2), event("e1"), sport("s1"));
        StandingsResult result = new StandingsResult(List.of(t1, t2));
        stage.setResult(result);
        stage.setStatus(CompetitionStatus.FINISHED);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(stage), Map.of("gs1", result)));
        PersistenceSnapshot loaded = r.loadAll();

        CompetitionResult loadedResult = loaded.resultsByCompetitionId().get("gs1");
        assertInstanceOf(StandingsResult.class, loadedResult);
        StandingsResult sr = (StandingsResult) loadedResult;
        assertEquals(2, sr.standings().size());
        assertEquals("t1", sr.standings().get(0).getId());
        assertEquals("t2", sr.standings().get(1).getId());
    }

    @Test
    void cancelledResultRoundTrips() {
        Match match = new Match("m1", team("t1"), team("t2"), event("e1"), sport("s1"));
        CancelledResult cancelled = new CancelledResult(CancelationReason.WEATHER);
        match.cancel(cancelled);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(match), Map.of("m1", cancelled)));
        PersistenceSnapshot loaded = r.loadAll();

        CompetitionResult loadedResult = loaded.resultsByCompetitionId().get("m1");
        assertInstanceOf(CancelledResult.class, loadedResult);
        assertEquals(CancelationReason.WEATHER, ((CancelledResult) loadedResult).reason());
    }

    @Test
    void groupStageRoundTrips() {
        List<Participant> teams = List.of(team("t1"), team("t2"), team("t3"));
        GroupStage stage = new GroupStage("gs1", teams, event("e1"), sport("s1"));

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(stage), Map.of()));
        PersistenceSnapshot loaded = r.loadAll();

        assertInstanceOf(GroupStage.class, loaded.competitions().get(0));
        GroupStage loadedGs = (GroupStage) loaded.competitions().get(0);
        assertEquals("gs1", loadedGs.getId());
        assertEquals(3, loadedGs.getParticipants().size());
        assertEquals(3, loadedGs.getMatches().size());
    }

    @Test
    void groupStageWithStandingsResultRoundTrips() {
        Team t1 = team("t1");
        Team t2 = team("t2");
        GroupStage stage = new GroupStage("gs1", List.of(t1, t2), event("e1"), sport("s1"));
        StandingsResult result = new StandingsResult(List.of(t1, t2));
        stage.setResult(result);
        stage.setStatus(CompetitionStatus.FINISHED);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(stage), Map.of("gs1", result)));
        PersistenceSnapshot loaded = r.loadAll();

        assertInstanceOf(StandingsResult.class, loaded.resultsByCompetitionId().get("gs1"));
        assertEquals(CompetitionStatus.FINISHED, loaded.competitions().get(0).getStatus());
    }

    @Test
    void competitionStatusIsPreserved() {
        Match match = new Match("m1", team("t1"), team("t2"), event("e1"), sport("s1"));
        match.setStatus(CompetitionStatus.FINISHED);

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(match), Map.of()));
        assertEquals(CompetitionStatus.FINISHED, r.loadAll().competitions().get(0).getStatus());
    }

    @Test
    void multipleCompetitionTypesRoundTrip() {
        Match match = new Match("m1", player("p1"), player("p2"), event("e1"), sport("s1"));
        Race race = new Race("r1", List.of(player("p3"), player("p4")), event("e1"), sport("s1"));
        GroupStage stage = new GroupStage("gs1", List.of(team("t1"), team("t2")), event("e1"), sport("s1"));

        JsonCompetitionRepository r = repo();
        r.saveAll(new PersistenceSnapshot(List.of(), List.of(), List.of(match, race, stage), Map.of()));
        PersistenceSnapshot loaded = r.loadAll();

        assertEquals(3, loaded.competitions().size());
        Map<String, Competition> byId = new HashMap<>();
        loaded.competitions().forEach(c -> byId.put(c.getId(), c));
        assertInstanceOf(Match.class, byId.get("m1"));
        assertInstanceOf(Race.class, byId.get("r1"));
        assertInstanceOf(GroupStage.class, byId.get("gs1"));
    }
}