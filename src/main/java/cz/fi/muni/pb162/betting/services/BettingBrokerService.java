package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.Bettable;
import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;

/**
 * Service for managing betting markets and odds on behalf of the broker.
 *
 * @author Marek Jargaš
 */
public interface BettingBrokerService {

    /**
     * Opens a new betting market for the given competition.
     */
    BettingMarket createMarket(Bettable competition);

    /**
     * Adds a new bettable outcome to a market with initial odds.
     */
    void addOutcome(BettingMarket market, Outcome outcome, double initialOdds);

    /**
     * Updates the odds for an existing outcome on a market.
     * The previous odds are in the outcome's OddsHistory.
     */
    void updateOdds(BettingMarket market, Outcome outcome, double newOdds);

    /**
     * Returns the betting market associated with the given competition.
     *
     * @return market, or null if none exists for this competition
     */
    BettingMarket getMarket(Competition competition);

    /**
     * Returns the betting market associated with the competition with the given id.
     *
     * @return market, or null if no market exists for this competition id
     */
    BettingMarket getMarket(String competitionId);
}
