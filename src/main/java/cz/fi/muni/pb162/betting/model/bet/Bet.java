package cz.fi.muni.pb162.betting.model.bet;

import cz.fi.muni.pb162.betting.model.bettor.Bettor;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;

/**
 * Represents a bet placed by a bettor on specific outcome.
 * Captures all information at the moment the bet was accepted.
 *
 * @param bettor  the person who placed the bet
 * @param amount  the amount of money used on bet
 * @param outcome the outcome the bettor is betting on
 * @param odds    the odds valid at the time the bet was placed
 * @author Marek Jargaš
 */
public record Bet(Bettor bettor, double amount, Outcome outcome, double odds) {
}
