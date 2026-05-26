package cz.fi.muni.pb162.betting.model.bettor;

/**
 * Contract for any entity that can place bets in the system.
 *
 * @author Marek Jargaš
 */
public interface Bettor {

    /**
     * Returns the unique id of this bettor.
     *
     * @return unique id
     */
    String getId();

    /**
     * Returns the name of this bettor.
     *
     * @return name of the bettor
     */
    String getName();

    /**
     * Returns the current account balance available for betting.
     *
     * @return current balance
     */
    double getBalance();

    /**
     * Sets the account balance.
     *
     * @param balance new balance
     */
    void setBalance(double balance);
}
