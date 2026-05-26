package cz.fi.muni.pb162.betting.app;

import cz.fi.muni.pb162.betting.model.bettor.Bettor;
import cz.fi.muni.pb162.betting.model.competition.Match;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.services.dto.BettorBalance;

import java.util.List;

/**
 * Pretty-prints the BetCore demo state (headers, odds, matches, bets, balances).
 *
 * @author Jan Prosecký
 */
public class ConsolePrinter {

    /**
     * Prints a section header with the given title.
     *
     * @param title title to display
     */
    public void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    /**
     * Prints information about a placed bet.
     *
     * @param bettor name of the bettor
     * @param amount staked amount
     * @param label  description of the outcome
     * @param odds   odds at the time of betting
     */
    public void bet(String bettor, double amount, String label, double odds) {
        System.out.printf("%s vsadil/a %.0f Kč na '%s' @ %.2f%n", bettor, amount, label, odds);
    }

    /**
     * Prints the result of a single match.
     *
     * @param match  the match to print
     * @param winner the winning participant
     */
    public void match(Match match, Participant winner) {
        System.out.printf("  %s vs %s  ->  %s%n",
                match.getHome().getName(), match.getAway().getName(), winner.getName());
    }

    /**
     * Prints a numbered ranking of participants.
     *
     * @param ranking list of participants ordered by placement
     */
    public void ranking(List<Participant> ranking) {
        for (int i = 0; i < ranking.size(); i++) {
            System.out.println((i + 1) + ". " + ranking.get(i).getName());
        }
    }

    /**
     * Prints balance summary for each bettor.
     *
     * @param balances list of bettor balances to display
     */
    public void printBalances(List<BettorBalance> balances) {
        balances.forEach(b -> System.out.printf(
                "%-10s  vsazeno: %8.2f Kč   vyhráno: %8.2f Kč   prohráno: %8.2f Kč   net: %+8.2f Kč%n",
                b.bettor().getName(), b.totalStaked(), b.totalWon(), b.totalLost(), b.netResult()));
    }

    /**
     * Prints a list of bettors by name.
     *
     * @param bettors list of bettors to display
     */
    public void printBettors(List<Bettor> bettors) {
        bettors.forEach(b -> System.out.println(b.getName()));
    }
}
