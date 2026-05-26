package cz.fi.muni.pb162.betting.services.storage.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.services.storage.CompetitionRepository;
import cz.fi.muni.pb162.betting.services.storage.PersistenceSnapshot;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JSON file-based implementation of {@link CompetitionRepository}.
 * Stores the whole {@link PersistenceSnapshot} in a single pretty-printed JSON file.
 *
 * @author Jan Prosecký
 */
public class JsonCompetitionRepository implements CompetitionRepository {

    private final Path file;
    private final ObjectMapper mapper;

    /**
     * Creates a repository backed by the given JSON file.
     * The file does not need to exist for saving; parent directories are created on demand.
     *
     * @param file path to the JSON file used for persistence
     */
    public JsonCompetitionRepository(Path file) {
        this.file = file;
        this.mapper = buildMapper();
    }

    @Override
    public void saveAll(PersistenceSnapshot snapshot) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            mapper.writeValue(file.toFile(), snapshot);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save snapshot to " + file, e);
        }
    }

    @Override
    public PersistenceSnapshot loadAll() {
        try {
            return mapper.readValue(file.toFile(), PersistenceSnapshot.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load snapshot from " + file, e);
        }
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new ParameterNamesModule());

        m.addMixIn(Participant.class, PolymorphicMixins.ParticipantMixin.class);
        m.addMixIn(Competition.class, PolymorphicMixins.CompetitionMixin.class);
        m.addMixIn(Outcome.class, PolymorphicMixins.OutcomeMixin.class);
        m.addMixIn(CompetitionResult.class, PolymorphicMixins.CompetitionResultMixin.class);

        SimpleModule raceResultModule = new SimpleModule("RaceResultModule");
        raceResultModule.addSerializer(RaceResult.class, new RaceResultJson.Serializer());
        raceResultModule.addDeserializer(RaceResult.class, new RaceResultJson.Deserializer());
        m.registerModule(raceResultModule);

        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return m;
    }
}
