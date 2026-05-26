package cz.fi.muni.pb162.betting.app;

import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.competition.Sport;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.participant.Player;
import cz.fi.muni.pb162.betting.model.participant.Team;
import cz.fi.muni.pb162.betting.services.CompetitionService;
import cz.fi.muni.pb162.betting.services.CompetitionServiceImpl;
import cz.fi.muni.pb162.betting.services.storage.CompetitionRepository;
import cz.fi.muni.pb162.betting.services.storage.json.JsonCompetitionRepository;

import java.nio.file.Path;
import java.util.List;

/**
 * Simulates a sports manager creating competitions and saving them to a file.
 *
 * @author Jan Prosecký
 */
public class SportsDemo {

    private final CompetitionRepository competitionRepository =
            new JsonCompetitionRepository(Path.of("target", "competitions.json"));
    private final CompetitionService competitionService = new CompetitionServiceImpl(competitionRepository);
    private final ConsolePrinter printer = new ConsolePrinter();

    /**
     * Entry point — sets up competitions and saves them to disk.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        new SportsDemo().run();
    }

    private void run() {
        setupHockeyWorldCup();
        setupPragueMarathon();

        competitionService.saveAll();
        printer.header("Data uložena do " + Path.of("target", "competitions.json"));
    }

    private void setupHockeyWorldCup() {
        Sport hockey = competitionService.createSport(DemoIds.SPORT_HOCKEY, "Lední hokej");
        SportEvent championship = competitionService.createSportEvent(DemoIds.EVENT_HOCKEY, "MS v ledním hokeji 2026");

        List<Participant> teams = List.of(
                new Team("CZE", "Česká republika"),
                new Team("CAN", "Kanada"),
                new Team("SWE", "Švédsko"),
                new Team("FIN", "Finsko"),
                new Team("USA", "USA"),
                new Team("GER", "Německo"),
                new Team("SVK", "Slovensko"),
                new Team("RUS", "Rusko")
        );

        GroupStage groupStage = competitionService.createGroupStage(
                DemoIds.COMP_HOCKEY_GROUP, teams, championship, hockey);

        printer.header("Založeno: " + championship.getName());
        System.out.println("Skupina " + groupStage.getId() + " — " + teams.size() + " týmů, "
                + groupStage.getMatches().size() + " zápasů");
    }

    private void setupPragueMarathon() {
        Sport running = competitionService.createSport(DemoIds.SPORT_RUNNING, "Běh");
        SportEvent marathon = competitionService.createSportEvent(DemoIds.EVENT_MARATHON, "Pražský maraton 2026");

        List<Participant> runners = List.of(
                new Player(DemoIds.RUNNER_1, "Petr Novák"),
                new Player(DemoIds.RUNNER_2, "Eliud Kipchoge"),
                new Player(DemoIds.RUNNER_3, "Marie Svobodová"),
                new Player(DemoIds.RUNNER_4, "Jan Dvořák"),
                new Player(DemoIds.RUNNER_5, "Anna Procházková")
        );

        Race race = competitionService.createRace(DemoIds.COMP_MARATHON_RACE, runners, marathon, running);

        printer.header("Založeno: " + marathon.getName());
        System.out.println("Závod " + race.getId() + " — " + runners.size() + " závodníků");
    }
}
