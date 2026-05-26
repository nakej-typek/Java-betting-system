package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupStageTest {

    private List<Participant> teams;
    private GroupStage stage;

    @BeforeEach
    void setUp() {
        teams = List.<Participant>of(
                new Team("A", "A"),
                new Team("B", "B"),
                new Team("C", "C"),
                new Team("D", "D")
        );
        stage = new GroupStage("g", teams, new SportEvent("e", "Event"), new Sport("s", "Sport"));
    }

    @Test
    void generatesAllUniquePairs() {
        // n = 4 → n*(n-1)/2 = 6
        assertEquals(6, stage.getMatches().size());
    }

    @Test
    void everyPairAppearsExactlyOnce() {
        Set<Set<Participant>> seenPairs = new HashSet<>();
        for (Match match : stage.getMatches()) {
            Set<Participant> pair = Set.of(match.getHome(), match.getAway());
            assertTrue(seenPairs.add(pair), "Duplicate pair: " + pair);
        }
        assertEquals(6, seenPairs.size());
    }

    @Test
    void initialStatusIsUpcoming() {
        assertEquals(CompetitionStatus.UPCOMING, stage.getStatus());
    }

    @Test
    void rankingEmptyBeforeResultRecorded() {
        assertTrue(stage.getRanking().isEmpty());
    }
}
