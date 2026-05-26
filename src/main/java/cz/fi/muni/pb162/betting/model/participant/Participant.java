package cz.fi.muni.pb162.betting.model.participant;

/**
 * Contract for any entity that can compete in a sport competition.
 * Common abstraction for both individual players and teams.
 *
 * @author Marek Jargaš
 */
public interface Participant {

    /**
     * Returns the unique id of this participant.
     *
     * @return unique id
     */
    String getId();

    /**
     * Returns the display name of this participant
     *
     * @return name of this participant
     */
    String getName();
}
