package cz.fi.muni.pb162.betting.model.competition;

import java.util.Objects;

/**
 * Represents a sport.
 *
 * @author Marek Jargaš
 */
public class Sport {

    private final String id;
    private final String name;

    /**
     * Creates a new sport with the given id and name.
     *
     * @param id   unique identifier
     * @param name display name
     */
    public Sport(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique id of this sport.
     *
     * @return unique id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this sport.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sport other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}