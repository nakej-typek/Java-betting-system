package cz.fi.muni.pb162.betting.model.bettor;

/**
 * Represents a registered user who can place bets in the system.
 *
 * @author Marek Jargaš
 */
public class RegisteredBettor implements Bettor {

    private final String id;
    private final String name;
    private double balance;

    /**
     * Creates a new registered bettor with an initial balance.
     *
     * @param id      unique identifier
     * @param name    display name
     * @param balance starting account balance
     */
    public RegisteredBettor(String id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
