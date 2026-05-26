package cz.fi.muni.pb162.betting.services.storage;

/**
 * Persistence boundary for competition data.
 * Implementations decide the storage format (JSON, DB, ...);
 * the service layer only sees this interface.
 *
 * @author Marek Jargaš
 */
public interface CompetitionRepository {

    /**
     * Persists the whole snapshot of competition data, replacing any previously stored state.
     *
     * @param snapshot snapshot of sport events, competitions and recorded results
     */
    void saveAll(PersistenceSnapshot snapshot);

    /**
     * Loads the previously persisted snapshot of competition data.
     *
     * @return snapshot read from the underlying storage
     */
    PersistenceSnapshot loadAll();
}
