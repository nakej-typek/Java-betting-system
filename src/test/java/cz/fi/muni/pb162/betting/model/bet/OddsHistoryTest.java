package cz.fi.muni.pb162.betting.model.bet;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OddsHistoryTest {

    private static final double DELTA = 1e-9;

    @Test
    void emptyHistoryReturnsNullAsCurrent() {
        OddsHistory history = new OddsHistory();
        assertNull(history.getCurrentOdds());
    }

    @Test
    void currentOddsReturnsLatestAdded() {
        OddsHistory history = new OddsHistory();
        history.addOdds(2.10);
        history.addOdds(2.50);
        history.addOdds(2.30);
        assertEquals(2.30, history.getCurrentOdds(), DELTA);
    }

    @Test
    void getOddsAtReturnsHistoricalEntry() {
        OddsHistory history = new OddsHistory();
        history.addOdds(2.10);
        history.addOdds(2.50);
        assertEquals(2.10, history.getOddsAt(0), DELTA);
        assertEquals(2.50, history.getOddsAt(1), DELTA);
    }

    @Test
    void getHistoryReturnsAllOddsInOrder() {
        OddsHistory history = new OddsHistory();
        history.addOdds(2.10);
        history.addOdds(2.50);
        assertEquals(List.of(2.10, 2.50), history.getHistory());
    }
}
