package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.Bet;
import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.Bettor;
import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;
import cz.fi.muni.pb162.betting.services.dto.BettorBalance;

import java.util.List;

/**
 * Service for registering bettors, placing bets on their behalf
 * and querying aggregated information about bettors and their results.
 *
 * @author Marek Jargaš
 */
public interface BettorService {

    /**
     * Registers a new bettor with an initial account balance.
     */
    RegisteredBettor registerBettor(String id, String name, double initialBalance);

    /**
     * Places a bet on the given outcome in the given market on behalf of a bettor.
     * Deducts the amount from the bettor's balance and records the current odds.
     */
    Bet placeBet(Bettor bettor, BettingMarket market, Outcome outcome, double amount);

    /**
     * Returns balances of all bettors who have at least one bet in any market
     * belonging to a competition of the given sport event.
     * Bets in still-running competitions count only towards totalStaked.
     */
    List<BettorBalance> getBalances(SportEvent event);

    /**
     * Returns all bettors who have at least one bet in the market of the given competition.
     */
    List<Bettor> getBettorsForCompetition(Competition competition);

    /**
     * Returns all bettors sorted in descending order by their total staked amount
     * across all known markets.
     */
    List<Bettor> getBettorsByTotalStake();

    /**
     * Returns all bets placed by the given bettor.
     * Used by ResultEvaluatorService when settling markets.
     */
    List<Bet> getBets(Bettor bettor);

    /**
     * Credits winnings to a bettor's balance after a successful bet evaluation.
     * Used by ResultEvaluatorService.
     */
    void creditWinnings(Bettor bettor, double amount);
}
