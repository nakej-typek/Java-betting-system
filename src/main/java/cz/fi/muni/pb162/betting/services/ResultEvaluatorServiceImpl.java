package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.Bet;
import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;

/**
 * Implementation of ResultEvaluatorService.
 *
 * @author Marek Jargaš
 */
public class ResultEvaluatorServiceImpl implements ResultEvaluatorService {

    private final BettorService bettorService;

    /**
     * Creates a new ResultEvaluatorServiceImpl with the given bettor service.
     *
     * @param bettorService service used to credit winnings to bettors
     */
    public ResultEvaluatorServiceImpl(BettorService bettorService) {
        this.bettorService = bettorService;
    }

    @Override
    public void settleMarket(BettingMarket market, CompetitionResult result) {
        for (Bet bet : market.getBets()) {
            if (isCancelled(result)) {
                bettorService.creditWinnings(bet.bettor(), bet.amount());
            } else if (bet.outcome().matches(result)) {
                bettorService.creditWinnings(bet.bettor(), bet.amount() * bet.odds());
            }
        }
    }

    private boolean isCancelled(CompetitionResult result) {
        return result instanceof CancelledResult;
    }
}
