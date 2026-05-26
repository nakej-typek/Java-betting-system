package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.PlacementOutcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.model.result.CancelationReason;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultEvaluatorServiceImplTest {

    private BettorServiceImpl bettorService;
    private ResultEvaluatorServiceImpl evaluator;
    private BettingMarket market;
    private Team teamA;
    private Team teamB;
    private PlacementOutcome aFirst;
    private PlacementOutcome bFirst;

    @BeforeEach
    void setUp() {
        BettingBrokerServiceImpl broker = new BettingBrokerServiceImpl();
        bettorService = new BettorServiceImpl(new CompetitionServiceImpl(null), broker);
        evaluator = new ResultEvaluatorServiceImpl(bettorService);

        teamA = new Team("A", "A");
        teamB = new Team("B", "B");
        GroupStage stage = new GroupStage(
                "g",
                List.<Participant>of(teamA, teamB),
                new SportEvent("e", "Event"),
                new Sport("s", "Sport")
        );
        market = broker.createMarket(stage);
        aFirst = new PlacementOutcome(teamA, 1);
        bFirst = new PlacementOutcome(teamB, 1);
        market.addOutcome(aFirst, 2.00);
        market.addOutcome(bFirst, 3.00);
    }

    @Test
    void winningBetPaysOutAmountTimesOdds() {
        RegisteredBettor bettor = bettorService.registerBettor("b1", "Petr", 1000.0);
        bettorService.placeBet(bettor, market, aFirst, 100.0);

        evaluator.settleMarket(market, new StandingsResult(List.<Participant>of(teamA, teamB)));

        // 1000 - 100 + 100 * 2.00 = 1100
        assertEquals(1100.0, bettor.getBalance());
    }

    @Test
    void losingBetLeavesBalanceUnchanged() {
        RegisteredBettor bettor = bettorService.registerBettor("b1", "Petr", 1000.0);
        bettorService.placeBet(bettor, market, bFirst, 100.0);

        evaluator.settleMarket(market, new StandingsResult(List.<Participant>of(teamA, teamB)));

        // 1000 - 100 + 0 = 900
        assertEquals(900.0, bettor.getBalance());
    }

    @Test
    void cancelledResultRefundsAllBets() {
        RegisteredBettor petr = bettorService.registerBettor("b1", "Petr", 1000.0);
        RegisteredBettor jana = bettorService.registerBettor("b2", "Jana", 1000.0);
        bettorService.placeBet(petr, market, aFirst, 100.0);
        bettorService.placeBet(jana, market, bFirst, 200.0);

        evaluator.settleMarket(market, new CancelledResult(CancelationReason.WEATHER));

        assertEquals(1000.0, petr.getBalance());
        assertEquals(1000.0, jana.getBalance());
    }

    @Test
    void multipleBetsEvaluatedIndependently() {
        RegisteredBettor petr = bettorService.registerBettor("b1", "Petr", 1000.0);
        RegisteredBettor jana = bettorService.registerBettor("b2", "Jana", 1000.0);
        bettorService.placeBet(petr, market, aFirst, 100.0); // wins: +200
        bettorService.placeBet(jana, market, bFirst, 150.0); // loses

        evaluator.settleMarket(market, new StandingsResult(List.<Participant>of(teamA, teamB)));

        assertEquals(1100.0, petr.getBalance());
        assertEquals(850.0, jana.getBalance());
    }
}
