package cz.fi.muni.pb162.betting.model.participant;

/**
 * Represents a team competing in a sport event.
 *
 * @author Marek Jargaš
 */
public class Team extends BaseParticipant {

    /**
     * Creates a new team with the given id and name.
     *
     * @param id   unique identifier
     * @param name display name
     */
    public Team(String id, String name) {
        super(id, name);
    }
}
