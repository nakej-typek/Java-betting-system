package cz.fi.muni.pb162.betting.services.dto;

import cz.fi.muni.pb162.betting.model.bettor.Bettor;

/**
 * Aggregated betting balance of a single bettor across one or more competitions.
 * Returned by the bettor service so the presentation layer can display
 * how much each bettor staked, won and lost without exposing internal bet objects.
 *
 * @param bettor      the bettor this balance belongs to
 * @param totalStaked total amount the bettor has staked across all evaluated bets
 * @param totalWon    total amount paid out from winning bets
 * @param totalLost   total amount lost on losing bets
 * @author Jan Prosecký
 */
public record BettorBalance(
        Bettor bettor,
        double totalStaked,
        double totalWon,
        double totalLost
) {
    /**
     * Net profit or loss of the bettor (won minus lost).
     * Positive value means the bettor is in profit, negative means a loss.
     *
     * @return net result of all evaluated bets
     */
    public double netResult() {
        return totalWon - totalLost;
    }
}
