package cz.fi.muni.pb162.betting.services.storage.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.KnockoutBracket;
import cz.fi.muni.pb162.betting.model.competition.Match;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.outcome.DrawOutcome;
import cz.fi.muni.pb162.betting.model.outcome.PlacementOutcome;
import cz.fi.muni.pb162.betting.model.outcome.WinnerOutcome;
import cz.fi.muni.pb162.betting.model.outcome.WithdrawalOutcome;
import cz.fi.muni.pb162.betting.model.participant.Player;
import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.MatchResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.model.result.StandingsResult;

/**
 * Jackson mixins that add polymorphic type information to model interfaces
 * without polluting the model with serialization annotations.
 * Each mixin emits/consumes a "type" property with a stable logical name.
 *
 * @author Jan Prosecký
 */
public final class PolymorphicMixins {

    private PolymorphicMixins() {
    }

    /**
     * Polymorphic mixin for {@link Participant} subtypes.
     *
     * @author Jan Prosecký
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Player.class, name = "player"),
            @JsonSubTypes.Type(value = Team.class, name = "team")
    })
    public abstract static class ParticipantMixin {
    }

    /**
     * Polymorphic mixin for {@link cz.fi.muni.pb162.betting.model.competition.Competition} subtypes.
     *
     * @author Jan Prosecký
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Match.class, name = "match"),
            @JsonSubTypes.Type(value = Race.class, name = "race"),
            @JsonSubTypes.Type(value = GroupStage.class, name = "groupStage"),
            @JsonSubTypes.Type(value = KnockoutBracket.class, name = "knockoutBracket")
    })
    public abstract static class CompetitionMixin {
    }

    /**
     * Polymorphic mixin for {@link cz.fi.muni.pb162.betting.model.outcome.Outcome} subtypes.
     *
     * @author Jan Prosecký
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = WinnerOutcome.class, name = "winner"),
            @JsonSubTypes.Type(value = DrawOutcome.class, name = "draw"),
            @JsonSubTypes.Type(value = PlacementOutcome.class, name = "placement"),
            @JsonSubTypes.Type(value = WithdrawalOutcome.class, name = "withdrawal")
    })
    public abstract static class OutcomeMixin {
    }

    /**
     * Polymorphic mixin for {@link cz.fi.muni.pb162.betting.model.result.CompetitionResult} subtypes.
     *
     * @author Jan Prosecký
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MatchResult.class, name = "match"),
            @JsonSubTypes.Type(value = RaceResult.class, name = "race"),
            @JsonSubTypes.Type(value = StandingsResult.class, name = "standings"),
            @JsonSubTypes.Type(value = CancelledResult.class, name = "cancelled")
    })
    public abstract static class CompetitionResultMixin {
    }
}
