package cz.fi.muni.pb162.betting.model.participant;

import java.util.Objects;

/**
 * Common base for all participants identified by id and name.
 *
 * @author Marek Jargaš
 */
public abstract class BaseParticipant implements Participant {

    private final String id;
    private final String name;

    protected BaseParticipant(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseParticipant other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
