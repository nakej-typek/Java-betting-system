package cz.fi.muni.pb162.betting.services.storage;

import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;

import java.util.List;
import java.util.Map;

/**
 * Snapshot of all persistable state in one bundle.
 * Enables atomic save/load through {@link CompetitionRepository}.
 *
 * @author Marek Jargaš
 * @param sports                 all registered sports
 * @param sportEvents            all registered sport events
 * @param competitions           all registered competitions
 * @param resultsByCompetitionId map of competition id to its result
 */
public record PersistenceSnapshot(
        List<Sport> sports,
        List<SportEvent> sportEvents,
        List<Competition> competitions,
        Map<String, CompetitionResult> resultsByCompetitionId
) {
}
