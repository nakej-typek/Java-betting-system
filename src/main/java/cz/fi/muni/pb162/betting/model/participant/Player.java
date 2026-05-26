package cz.fi.muni.pb162.betting.model.participant;

/**
 * Represents an individual person competing in a sport event.
 *
 * @author Marek Jargaš
 */
public class Player extends BaseParticipant {

    /**
     * Creates a new player with the given id and name.
     *
     * @param id   unique identifier
     * @param name display name
     */
    public Player(String id, String name) {
        super(id, name);
    }
}
