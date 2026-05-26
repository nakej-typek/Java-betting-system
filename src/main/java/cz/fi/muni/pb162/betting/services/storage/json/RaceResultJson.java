package cz.fi.muni.pb162.betting.services.storage.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.ParticipantRaceStatus;
import cz.fi.muni.pb162.betting.model.result.RaceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom (de)serializer for {@link RaceResult}.
 * Serializes the {@code participantStatuses} map as an array of pairs
 * to keep participant objects intact (Jackson would otherwise need a key
 * serializer that flattens them to strings).
 *
 * @author Jan Prosecký
 */
public final class RaceResultJson {

    private RaceResultJson() {
    }

    /**
     * Jackson serializer that writes a {@link RaceResult} as a JSON object
     * with {@code finishOrder} and {@code participantStatuses} fields.
     *
     * @author Jan Prosecký
     */
    public static class Serializer extends JsonSerializer<RaceResult> {

        @Override
        public void serialize(RaceResult value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeStartObject();
            writeFields(value, gen, serializers);
            gen.writeEndObject();
        }

        @Override
        public void serializeWithType(RaceResult value, JsonGenerator gen, SerializerProvider serializers,
                                      TypeSerializer typeSer) throws IOException {
            WritableTypeId typeId = typeSer.typeId(value, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(gen, typeId);
            writeFields(value, gen, serializers);
            typeSer.writeTypeSuffix(gen, typeId);
        }

        private void writeFields(RaceResult value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            JsonSerializer<Object> participantSer =
                    serializers.findTypedValueSerializer(Participant.class, true, null);

            gen.writeArrayFieldStart("finishOrder");
            for (Participant participant : value.finishOrder()) {
                participantSer.serialize(participant, gen, serializers);
            }
            gen.writeEndArray();

            gen.writeArrayFieldStart("participantStatuses");
            for (Map.Entry<Participant, ParticipantRaceStatus> entry : value.participantStatuses().entrySet()) {
                gen.writeStartObject();
                gen.writeFieldName("participant");
                participantSer.serialize(entry.getKey(), gen, serializers);
                gen.writeStringField("status", entry.getValue().name());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    /**
     * Jackson deserializer that reads a {@link RaceResult} written by {@link Serializer}.
     *
     * @author Jan Prosecký
     */
    public static class Deserializer extends JsonDeserializer<RaceResult> {

        @Override
        public RaceResult deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            List<Participant> finishOrder = new ArrayList<>();
            JsonNode finishOrderNode = node.get("finishOrder");
            if (finishOrderNode != null) {
                for (JsonNode item : finishOrderNode) {
                    finishOrder.add(p.getCodec().treeToValue(item, Participant.class));
                }
            }

            Map<Participant, ParticipantRaceStatus> statuses = new LinkedHashMap<>();
            JsonNode statusesNode = node.get("participantStatuses");
            if (statusesNode != null) {
                for (JsonNode entry : statusesNode) {
                    Participant participant = p.getCodec()
                            .treeToValue(entry.get("participant"), Participant.class);
                    ParticipantRaceStatus status = ParticipantRaceStatus.valueOf(entry.get("status").asText());
                    statuses.put(participant, status);
                }
            }
            return new RaceResult(finishOrder, statuses);
        }
    }
}
