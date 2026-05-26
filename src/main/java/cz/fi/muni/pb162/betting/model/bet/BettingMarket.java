package cz.fi.muni.pb162.betting.model.bet;

import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the betting market for a single competition.
 * Holds available outcomes, their odds history and all bets placed.
 * Created and managed by the BettingBrokerService.
 *
 * @author Marek Jargaš
 */
public class BettingMarket {

    private final Competition competition;
    private final Map<Outcome, OddsHistory> oddsPerOutcome;
    private final List<Bet> bets;

    /**
     * Creates a new betting market for the given competition.
     *
     * @param competition the competition this market is for
     */
    public BettingMarket(Competition competition) {
        this.competition = competition;
        this.oddsPerOutcome = new HashMap<>();
        this.bets = new ArrayList<>();
    }

    /**
     * Returns the competition this market is associated with.
     *
     * @return competition
     */
    public Competition getCompetition() {
        return competition;
    }

    /**
     * Returns all outcomes currently available for betting.
     *
     * @return list of available outcomes
     */
    public List<Outcome> getAvailableOutcomes() {
        return Collections.unmodifiableList(new ArrayList<>(oddsPerOutcome.keySet()));
    }

    /**
     * Returns the full odds history for a specific outcome.
     *
     * @param outcome the outcome to look up
     * @return odds history, or null if outcome is not available
     */
    public OddsHistory getOddsHistory(Outcome outcome) {
        return oddsPerOutcome.get(outcome);
    }

    /**
     * Returns the currently valid odds for a specific outcome.
     *
     * @param outcome the outcome to look up
     * @return current odds, or null if outcome is not available
     */
    public Double getCurrentOdds(Outcome outcome) {
        OddsHistory oddsHistory = oddsPerOutcome.get(outcome);
        if (oddsHistory == null) {
            return null;
        }
        return oddsHistory.getCurrentOdds();
    }

    /**
     * Returns all bets placed on this market.
     *
     * @return list of bets
     */
    public List<Bet> getBets() {
        return Collections.unmodifiableList(bets);
    }

    /**
     * Adds a new outcome with its initial odds to this market.
     *
     * @param outcome     the outcome to add
     * @param initialOdds the initial odds for this outcome
     */
    public void addOutcome(Outcome outcome, double initialOdds) {
        OddsHistory oddsHistory = new OddsHistory();
        oddsHistory.addOdds(initialOdds);
        oddsPerOutcome.put(outcome, oddsHistory);
    }

    /**
     * Updates odds for an existing outcome by appending to its history.
     *
     * @param outcome the outcome whose odds are being updated
     * @param newOdds the new odds value
     */
    public void updateOdds(Outcome outcome, double newOdds) {
        OddsHistory oddsHistory = oddsPerOutcome.get(outcome);
        if (oddsHistory != null) {
            oddsHistory.addOdds(newOdds);
        }
    }

    /**
     * Place a bet on this market.
     *
     * @param bet the bet to be placed
     */
    public void addBet(Bet bet) {
        bets.add(bet);
    }
}
