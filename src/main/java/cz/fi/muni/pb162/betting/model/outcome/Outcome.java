package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.result.CompetitionResult;

/**
 * Contract for any possible outcome of a competition.
 * Used both as what a bettor bets on and as the actual result used to evaluate bets.
 *
 * @author Marek Jargaš
 */
public interface Outcome {

    /**
     * Returns true if this outcome matches the actual competition result.
     * Each outcome type implements its own evaluation logic.
     *
     * @param result the actual competition result
     * @return true if this outcome is a winning bet outcome
     */
    boolean matches(CompetitionResult result);
}
