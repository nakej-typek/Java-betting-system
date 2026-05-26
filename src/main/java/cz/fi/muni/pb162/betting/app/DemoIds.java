package cz.fi.muni.pb162.betting.app;

/**
 * Shared ID constants used by SportsDemo and BetsDemo.
 * Centralises all demo data identifiers so a change in one place
 * stays consistent across both classes.
 *
 * @author Marek Jargaš
 */
final class DemoIds {

    static final String SPORT_HOCKEY = "hockey";
    static final String SPORT_RUNNING = "running";

    static final String EVENT_HOCKEY = "ms-2026";
    static final String EVENT_MARATHON = "prg-marathon-2026";

    static final String COMP_HOCKEY_GROUP = "group-2026";
    static final String COMP_MARATHON_RACE = "prg-marathon-race";

    static final String RUNNER_1 = "p1";
    static final String RUNNER_2 = "p2";
    static final String RUNNER_3 = "p3";
    static final String RUNNER_4 = "p4";
    static final String RUNNER_5 = "p5";

    static final String BETTOR_PETR = "b1";
    static final String BETTOR_JANA = "b2";

    private DemoIds() {
    }
}