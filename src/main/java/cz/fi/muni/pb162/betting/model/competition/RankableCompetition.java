package cz.fi.muni.pb162.betting.model.competition;

import cz.fi.muni.pb162.betting.model.participant.Participant;

import java.util.List;

/**
 * A competition that produces a final ranking of its participants.
 * Applies to races, group stages, and knockout brackets.
 *
 * @author Marek Jargaš
 */
public interface RankableCompetition extends Competition {

    /**
     * Returns participants ordered by their final placement.
     * Index 0 is the winner and so on.
     * Available only when status of the competition is FINISHED.
     *
     * @return ordered list of participants by placement
     */
    List<Participant> getRanking();
}
