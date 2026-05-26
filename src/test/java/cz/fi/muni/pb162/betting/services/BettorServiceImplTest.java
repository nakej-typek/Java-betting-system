package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.Bet;
import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.PlacementOutcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BettorServiceImplTest {

    private BettorServiceImpl service;
    private BettingMarket market;
    private PlacementOutcome outcome;

    @BeforeEach
    void setUp() {
        BettingBrokerServiceImpl broker = new BettingBrokerServiceImpl();
        service = new BettorServiceImpl(new CompetitionServiceImpl(null), broker);
        Team teamA = new Team("A", "A");
        Team teamB = new Team("B", "B");
        GroupStage stage = new GroupStage(
                "g",
                List.<Participant>of(teamA, teamB),
                new SportEvent("e", "Event"),
                new Sport("s", "Sport")
        );
        market = broker.createMarket(stage);
        outcome = new PlacementOutcome(teamA, 1);
        market.addOutcome(outcome, 2.50);
    }

    @Test
    void registerBettorReturnsBettorWithGivenBalance() {
        RegisteredBettor bettor = service.registerBettor("b1", "Petr", 1000.0);
        assertEquals("b1", bettor.getId());
        assertEquals("Petr", bettor.getName());
        assertEquals(1000.0, bettor.getBalance());
    }

    @Test
    void placeBetCapturesCurrentOddsAtTimeOfBet() {
        RegisteredBettor bettor = service.registerBettor("b1", "Petr", 1000.0);
        Bet bet = service.placeBet(bettor, market, outcome, 100.0);

        // Update odds AFTER the bet — the bet must keep the original odds.
        market.updateOdds(outcome, 3.50);
        assertEquals(2.50, bet.odds(), 1e-9);
    }

    @Test
    void placeBetDeductsAmountFromBettorBalance() {
        RegisteredBettor bettor = service.registerBettor("b1", "Petr", 1000.0);
        service.placeBet(bettor, market, outcome, 100.0);
        assertEquals(900.0, bettor.getBalance());
    }

    @Test
    void placeBetIsStoredInMarketAndInBettorHistory() {
        RegisteredBettor bettor = service.registerBettor("b1", "Petr", 1000.0);
        Bet bet = service.placeBet(bettor, market, outcome, 100.0);

        assertTrue(market.getBets().contains(bet));
        assertEquals(List.of(bet), service.getBets(bettor));
    }

    @Test
    void creditWinningsIncreasesBalance() {
        RegisteredBettor bettor = service.registerBettor("b1", "Petr", 1000.0);
        service.creditWinnings(bettor, 250.0);
        assertEquals(1250.0, bettor.getBalance());
    }

    @Test
    void betReferencesItsBettor() {
        RegisteredBettor bettor = service.registerBettor("b1", "Petr", 1000.0);
        Bet bet = service.placeBet(bettor, market, outcome, 100.0);
        assertSame(bettor, bet.bettor());
    }
}
