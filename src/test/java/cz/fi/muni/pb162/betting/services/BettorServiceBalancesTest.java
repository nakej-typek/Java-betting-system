package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.Bettor;
import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.PlacementOutcome;
import cz.fi.muni.pb162.betting.model.outcome.WinnerOutcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Player;
import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.model.result.ParticipantRaceStatus;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;
import cz.fi.muni.pb162.betting.services.dto.BettorBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BettorServiceBalancesTest {

    private CompetitionServiceImpl competitionService;
    private BettingBrokerServiceImpl brokerService;
    private BettorServiceImpl bettorService;
    private ResultEvaluatorServiceImpl evaluatorService;

    private SportEvent marathon;
    private Race race;
    private Player kipchoge;
    private BettingMarket raceMarket;
    private PlacementOutcome kipchogeThird;
    private RegisteredBettor petr;
    private RegisteredBettor jana;

    @BeforeEach
    void setUp() {
        brokerService = new BettingBrokerServiceImpl();
        competitionService = new CompetitionServiceImpl(null);
        bettorService = new BettorServiceImpl(competitionService, brokerService);
        evaluatorService = new ResultEvaluatorServiceImpl(bettorService);

        Sport running = competitionService.createSport("running", "Running");
        marathon = competitionService.createSportEvent("prg-2026", "Prague Marathon 2026");

        kipchoge = new Player("p1", "Kipchoge");
        Player novak = new Player("p2", "Novak");
        Player svoboda = new Player("p3", "Svoboda");

        race = competitionService.createRace("race-1",
                List.of(kipchoge, novak, svoboda), marathon, running);

        raceMarket = brokerService.createMarket(race);
        kipchogeThird = new PlacementOutcome(kipchoge, 3);
        brokerService.addOutcome(raceMarket, kipchogeThird, 1.80);

        petr = bettorService.registerBettor("b1", "Petr", 1000.0);
        jana = bettorService.registerBettor("b2", "Jana", 1000.0);
    }

    // --- getBalances ---

    @Test
    void getBalancesStakedCorrectlyBeforeResult() {
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 100.0);
        bettorService.placeBet(jana, raceMarket, kipchogeThird, 200.0);

        List<BettorBalance> balances = bettorService.getBalances(marathon);

        BettorBalance petrBalance = balances.stream()
                .filter(b -> b.bettor().equals(petr)).findFirst().orElseThrow();
        assertEquals(100.0, petrBalance.totalStaked(), 1e-9);
        assertEquals(0.0, petrBalance.totalWon(), 1e-9);
        assertEquals(0.0, petrBalance.totalLost(), 1e-9);
    }

    @Test
    void getBalancesWinnerGetsCorrectPayout() {
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 100.0);

        RaceResult result = new RaceResult(
                List.of(new Player("x1", "X"), new Player("x2", "Y"), kipchoge),
                Map.of(kipchoge, ParticipantRaceStatus.FINISHED));
        competitionService.recordResult(race, result);
        evaluatorService.settleMarket(raceMarket, result);

        List<BettorBalance> balances = bettorService.getBalances(marathon);
        BettorBalance petrBalance = balances.stream()
                .filter(b -> b.bettor().equals(petr)).findFirst().orElseThrow();

        assertEquals(100.0, petrBalance.totalStaked(), 1e-9);
        assertEquals(180.0, petrBalance.totalWon(), 1e-9);
        assertEquals(0.0, petrBalance.totalLost(), 1e-9);
        assertEquals(180.0, petrBalance.netResult(), 1e-9);
    }

    @Test
    void getBalancesLoserIsCharged() {
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 100.0);

        RaceResult result = new RaceResult(
                List.of(kipchoge, new Player("x1", "X"), new Player("x2", "Y")),
                Map.of(kipchoge, ParticipantRaceStatus.FINISHED));
        competitionService.recordResult(race, result);
        evaluatorService.settleMarket(raceMarket, result);

        List<BettorBalance> balances = bettorService.getBalances(marathon);
        BettorBalance petrBalance = balances.stream()
                .filter(b -> b.bettor().equals(petr)).findFirst().orElseThrow();

        assertEquals(100.0, petrBalance.totalStaked(), 1e-9);
        assertEquals(0.0, petrBalance.totalWon(), 1e-9);
        assertEquals(100.0, petrBalance.totalLost(), 1e-9);
        assertEquals(-100.0, petrBalance.netResult(), 1e-9);
    }

    @Test
    void getBalancesOnlyIncludesBettorsFromGivenEvent() {
        SportEvent otherEvent = competitionService.createSportEvent("other", "Other Event");
        Sport sport = competitionService.createSport("s2", "Sport");
        Team a = new Team("a", "A");
        Team b = new Team("b", "B");
        GroupStage otherStage = competitionService.createGroupStage("other-g",
                List.of(a, b), otherEvent, sport);
        BettingMarket otherMarket = brokerService.createMarket(otherStage);
        WinnerOutcome outcome = new WinnerOutcome(a);
        brokerService.addOutcome(otherMarket, outcome, 2.0);

        bettorService.placeBet(petr, raceMarket, kipchogeThird, 100.0);
        bettorService.placeBet(jana, otherMarket, outcome, 50.0);

        List<BettorBalance> balances = bettorService.getBalances(marathon);
        assertEquals(1, balances.size());
        assertEquals(petr, balances.get(0).bettor());
    }

    // --- getBettorsForCompetition ---

    @Test
    void getBettorsForCompetitionReturnsDistinctBettors() {
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 50.0);
        bettorService.placeBet(jana, raceMarket, kipchogeThird, 50.0);
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 50.0);

        List<Bettor> bettors = bettorService.getBettorsForCompetition(race);
        assertEquals(2, bettors.size());
        assertTrue(bettors.contains(petr));
        assertTrue(bettors.contains(jana));
    }

    @Test
    void getBettorsForCompetitionEmptyWhenNoMarket() {
        Race noMarketRace = new Race("r2", List.of(kipchoge), marathon,
                competitionService.createSport("s2", "S"));
        assertTrue(bettorService.getBettorsForCompetition(noMarketRace).isEmpty());
    }

    // --- getBettorsByTotalStake ---

    @Test
    void getBettorsByTotalStakeSortedDescending() {
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 100.0);
        bettorService.placeBet(jana, raceMarket, kipchogeThird, 300.0);

        List<Bettor> sorted = bettorService.getBettorsByTotalStake();
        assertEquals(jana, sorted.get(0));
        assertEquals(petr, sorted.get(1));
    }

    @Test
    void getBettorsByTotalStakeAccumulatesAcrossMultipleBets() {
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 100.0);
        bettorService.placeBet(petr, raceMarket, kipchogeThird, 150.0);
        bettorService.placeBet(jana, raceMarket, kipchogeThird, 200.0);

        List<Bettor> sorted = bettorService.getBettorsByTotalStake();
        assertEquals(petr, sorted.get(0));
    }
}