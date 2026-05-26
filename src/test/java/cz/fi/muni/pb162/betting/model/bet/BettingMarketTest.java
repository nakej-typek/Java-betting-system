package cz.fi.muni.pb162.betting.model.bet;

import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.PlacementOutcome;
import cz.fi.muni.pb162.betting.model.outcome.WinnerOutcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BettingMarketTest {

    private Team teamA;
    private Team teamB;
    private GroupStage groupStage;
    private BettingMarket market;
    private PlacementOutcome outcome;

    @BeforeEach
    void setUp() {
        teamA = new Team("A", "Team A");
        teamB = new Team("B", "Team B");
        Sport sport = new Sport("s", "Sport");
        SportEvent event = new SportEvent("e", "Event");
        groupStage = new GroupStage("g", List.<Participant>of(teamA, teamB), event, sport);
        market = new BettingMarket(groupStage);
        outcome = new PlacementOutcome(teamA, 1);
    }

    @Test
    void addOutcomeStoresInitialOdds() {
        market.addOutcome(outcome, 2.10);
        assertEquals(2.10, market.getCurrentOdds(outcome), 1e-9);
    }

    @Test
    void updateOddsAppendsToHistory() {
        market.addOutcome(outcome, 2.10);
        market.updateOdds(outcome, 2.50);
        assertEquals(2.50, market.getCurrentOdds(outcome), 1e-9);
        assertEquals(2, market.getOddsHistory(outcome).getHistory().size());
    }

    @Test
    void unknownOutcomeReturnsNullCurrentOdds() {
        assertNull(market.getCurrentOdds(outcome));
    }

    @Test
    void getAvailableOutcomesContainsAddedOnes() {
        market.addOutcome(outcome, 2.10);
        assertTrue(market.getAvailableOutcomes().contains(outcome));
    }

    @Test
    void addBetIsRecorded() {
        market.addOutcome(outcome, 2.10);
        RegisteredBettor bettor = new RegisteredBettor("b", "Bettor", 1000.0);
        Bet bet = new Bet(bettor, 100.0, new WinnerOutcome(teamA), 2.10);
        market.addBet(bet);
        assertEquals(List.of(bet), market.getBets());
    }

    @Test
    void marketCarriesItsCompetition() {
        assertEquals(groupStage, market.getCompetition());
    }
}
