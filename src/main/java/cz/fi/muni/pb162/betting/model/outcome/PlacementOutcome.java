package cz.fi.muni.pb162.betting.model.outcome;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;

import java.util.List;

/**
 * Outcome representing a participant finishing at a specific placement.
 * Applicable to rankable competitions (races, group stages, knockout brackets).
 *
 * @param participant the participant whose placement is predicted or confirmed
 * @param placement   the finishing position (1 = first)
 * @author Marek Jargaš
 */
public record PlacementOutcome(Participant participant, int placement) implements Outcome {

    @Override
    public boolean matches(CompetitionResult result) {
        List<Participant> standings = null;
        if (result instanceof RaceResult) {
            standings = ((RaceResult) result).finishOrder();
        } else if (result instanceof StandingsResult) {
            standings = ((StandingsResult) result).standings();
        }
        if (standings == null) {
            return false;
        }
        int actualPlacement = standings.indexOf(participant) + 1;
        return actualPlacement == placement;
    }
}
