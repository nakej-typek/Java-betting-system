package cz.fi.muni.pb162.betting.model.bet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks the history of odds changes for a single outcome on a betting market.
 * Odds are ordered by the time they were set, relative order — index 0 is the first.
 *
 * @author Marek Jargaš
 */
public class OddsHistory {

    private final List<Double> history;

    /**
     * Creates an empty odds history.
     */
    public OddsHistory() {
        this.history = new ArrayList<>();
    }

    /**
     * Adds a new odds value to end of the history.
     *
     * @param odds the new odds
     */
    public void addOdds(double odds) {
        history.add(odds);
    }

    /**
     * Returns the most recent odds.
     *
     * @return current odds, or null if no odds have been set yet
     */
    public Double getCurrentOdds() {
        if (history.isEmpty()) {
            return null;
        }
        return history.getLast();
    }

    /**
     * Returns the odds that were valid at a specific time in the history.
     * Used to determine which odds applied when a bet was placed.
     *
     * @param index position in history (0 = first odds set)
     * @return odds at the given index
     */
    public double getOddsAt(int index) {
        return history.get(index);
    }

    /**
     * Returns the full ordered history of odds.
     *
     * @return list of odds in chronological order
     */
    public List<Double> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
