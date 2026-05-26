package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.Bettable;
import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of BettingBrokerService.
 *
 * @author Marek Jargaš
 */
public class BettingBrokerServiceImpl implements BettingBrokerService {

    private final Map<Competition, BettingMarket> markets = new HashMap<>();

    @Override
    public BettingMarket createMarket(Bettable competition) {
        Competition asCompetition = (Competition) competition;
        BettingMarket market = new BettingMarket(asCompetition);
        markets.put(asCompetition, market);
        return market;
    }

    @Override
    public void addOutcome(BettingMarket market, Outcome outcome, double initialOdds) {
        market.addOutcome(outcome, initialOdds);
    }

    @Override
    public void updateOdds(BettingMarket market, Outcome outcome, double newOdds) {
        market.updateOdds(outcome, newOdds);
    }

    @Override
    public BettingMarket getMarket(Competition competition) {
        return markets.get(competition);
    }

    @Override
    public BettingMarket getMarket(String competitionId) {
        return markets.entrySet().stream()
                .filter(e -> e.getKey().getId().equals(competitionId))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
