package cz.fi.muni.pb162.betting.model.result;

/**
 * Represents a canceled competition result of any competition type.
 * All bets on this competition will be refunded.
 *
 * @param reason the reason why competition was canceled
 * @author Marek Jargaš
 */
public record CancelledResult(CancelationReason reason) implements CompetitionResult {
}