package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.CompetitionStatus;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CompetitionServiceImplTest {

    private CompetitionServiceImpl competitionService;
    private BettingBrokerServiceImpl brokerService;
    private BettorServiceImpl bettorService;
    private ResultEvaluatorServiceImpl evaluatorService;
    private Sport hockey;
    private SportEvent championship;
    private Team teamA;
    private Team teamB;

    @BeforeEach
    void setUp() {
        brokerService = new BettingBrokerServiceImpl();
        competitionService = new CompetitionServiceImpl(null);
        bettorService = new BettorServiceImpl(competitionService, brokerService);
        evaluatorService = new ResultEvaluatorServiceImpl(bettorService);

        hockey = competitionService.createSport("s1", "Hockey");
        championship = competitionService.createSportEvent("ev1", "WC 2026");
        teamA = new Team("A", "Alpha");
        teamB = new Team("B", "Beta");
    }

    @Test
    void cancelledCompetitionHasStatusCancelled() {
        Match match = competitionService.createMatch("m1", teamA, teamB, championship, hockey);
        competitionService.cancelCompetition(match, new CancelledResult(CancelationReason.WEATHER));
        assertEquals(CompetitionStatus.CANCELLED, match.getStatus());
    }

    @Test
    void cancelledCompetitionGetResultReturnsCancelledResult() {
        Match match = competitionService.createMatch("m1", teamA, teamB, championship, hockey);
        competitionService.cancelCompetition(match, new CancelledResult(CancelationReason.WEATHER));
        assertInstanceOf(CancelledResult.class, match.getResult());
    }

    @Test
    void cancelledCompetitionRefundsBettors() {
        Match match = competitionService.createMatch("m1", teamA, teamB, championship, hockey);
        BettingMarket market = brokerService.createMarket(match);
        WinnerOutcome outcome = new WinnerOutcome(teamA);
        brokerService.addOutcome(market, outcome, 2.0);

        RegisteredBettor bettor = bettorService.registerBettor("b1", "Petr", 1000.0);
        bettorService.placeBet(bettor, market, outcome, 100.0);

        CancelledResult cancelled = new CancelledResult(CancelationReason.WEATHER);
        competitionService.cancelCompetition(match, cancelled);
        evaluatorService.settleMarket(market, match.getResult());

        assertEquals(1000.0, bettor.getBalance());
    }

    @Test
    void raceRankingEmptyBeforeResultRecorded() {
        List<Participant> runners = List.of(new Player("p1", "A"), new Player("p2", "B"));
        Race race = competitionService.createRace("r1", runners, championship, hockey);
        assertEquals(List.of(), race.getRanking());
    }
}