package cz.fi.muni.pb162.betting.model.competition;

import java.util.Objects;

/**
 * Represents a named sport event that groups related competitions together for
 * example - "Ice Hockey World Championship 2026"
 *
 * @author Marek Jargaš
 */
public class SportEvent {

    private final String id;
    private final String name;

    /**
     * Creates a new sport event with the given id and name.
     *
     * @param id   unique identifier
     * @param name display name
     */
    public SportEvent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique id of this sport event.
     *
     * @return unique id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this sport event.
     *
     * @return name of this event
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SportEvent other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
