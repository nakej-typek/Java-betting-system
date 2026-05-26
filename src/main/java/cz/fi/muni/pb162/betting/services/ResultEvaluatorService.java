package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;

/**
 * Service for settling all bets on a market once the competition result is known.
 *
 * @author Marek Jargaš
 */
public interface ResultEvaluatorService {

    /**
     * Settles all bets on the given market against the actual competition result.
     * Winning bets are paid out to the bettor's balance; losing bets are settled.
     */
    void settleMarket(BettingMarket market, CompetitionResult result);
}
