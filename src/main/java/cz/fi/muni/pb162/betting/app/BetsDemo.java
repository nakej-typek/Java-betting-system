package cz.fi.muni.pb162.betting.app;

import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.Bettor;
import cz.fi.muni.pb162.betting.model.competition.GroupStage;
import cz.fi.muni.pb162.betting.model.competition.Race;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.PlacementOutcome;
import cz.fi.muni.pb162.betting.model.outcome.WinnerOutcome;
import cz.fi.muni.pb162.betting.model.outcome.WithdrawalOutcome;
import cz.fi.muni.pb162.betting.model.participant.Participant;
import cz.fi.muni.pb162.betting.model.result.ParticipantRaceStatus;
import cz.fi.muni.pb162.betting.model.result.RaceResult;
import cz.fi.muni.pb162.betting.services.BettingBrokerService;
import cz.fi.muni.pb162.betting.services.BettingBrokerServiceImpl;
import cz.fi.muni.pb162.betting.services.BettorService;
import cz.fi.muni.pb162.betting.services.BettorServiceImpl;
import cz.fi.muni.pb162.betting.services.CompetitionService;
import cz.fi.muni.pb162.betting.services.CompetitionServiceImpl;
import cz.fi.muni.pb162.betting.services.ResultEvaluatorService;
import cz.fi.muni.pb162.betting.services.ResultEvaluatorServiceImpl;
import cz.fi.muni.pb162.betting.services.storage.CompetitionRepository;
import cz.fi.muni.pb162.betting.services.storage.json.JsonCompetitionRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Simulates a bookmaker setting odds, bettors placing bets and evaluating results.
 * Loads competition data from a file saved by SportsDemo.
 *
 * @author Jan Prosecký
 */
public class BetsDemo {

    private final CompetitionRepository competitionRepository =
            new JsonCompetitionRepository(Path.of("target", "competitions.json"));
    private final CompetitionService competitionService = new CompetitionServiceImpl(competitionRepository);
    private final BettingBrokerService brokerService = new BettingBrokerServiceImpl();
    private final BettorService bettorService = new BettorServiceImpl(competitionService, brokerService);
    private final ResultEvaluatorService evaluatorService = new ResultEvaluatorServiceImpl(bettorService);
    private final ConsolePrinter printer = new ConsolePrinter();

    private Bettor petr;
    private Bettor jana;
    private BettingMarket marathonMarket;
    private PlacementOutcome kipchogeThird;

    /**
     * Entry point — loads competitions, sets odds, places bets and prints balances.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        new BetsDemo().run();
    }

    private void run() {
        competitionService.loadAll();
        printer.header("Načtena data z " + Path.of("target", "competitions.json"));

        setOddsForHockey();
        setOddsForMarathon();
        placeBets();
        recordMarathonResults();
        printBalances();
        printExtras();
    }

    private void setOddsForHockey() {
        GroupStage groupStage = (GroupStage) competitionService.getCompetition(DemoIds.COMP_HOCKEY_GROUP);
        printer.header("Kurzy pro MS-2026 (skupina)");
        groupStage.getMatches().forEach(match -> {
            BettingMarket market = brokerService.createMarket(match);
            WinnerOutcome home = new WinnerOutcome(match.getHome());
            WinnerOutcome away = new WinnerOutcome(match.getAway());
            brokerService.addOutcome(market, home, 1.80);
            brokerService.addOutcome(market, away, 2.10);
            System.out.printf("  %s vs %s   home @ %.2f   away @ %.2f%n",
                    match.getHome().getName(), match.getAway().getName(),
                    market.getCurrentOdds(home), market.getCurrentOdds(away));
        });
    }

    private void setOddsForMarathon() {
        Race race = (Race) competitionService.getCompetition(DemoIds.COMP_MARATHON_RACE);
        Participant kipchoge = race.getParticipants().stream()
                .filter(p -> DemoIds.RUNNER_2.equals(p.getId()))
                .findFirst()
                .orElseThrow();

        marathonMarket = brokerService.createMarket(race);
        WinnerOutcome winner = new WinnerOutcome(kipchoge);
        kipchogeThird = new PlacementOutcome(kipchoge, 3);
        WithdrawalOutcome withdrawal = new WithdrawalOutcome(kipchoge);

        brokerService.addOutcome(marathonMarket, winner, 2.50);
        brokerService.addOutcome(marathonMarket, kipchogeThird, 1.80);
        brokerService.addOutcome(marathonMarket, withdrawal, 8.00);

        printer.header("Kurzy pro Pražský maraton 2026 — " + kipchoge.getName());
        System.out.printf("  výhra        @ %.2f%n", marathonMarket.getCurrentOdds(winner));
        System.out.printf("  3. místo     @ %.2f%n", marathonMarket.getCurrentOdds(kipchogeThird));
        System.out.printf("  odstoupení   @ %.2f%n", marathonMarket.getCurrentOdds(withdrawal));
    }

    private void placeBets() {
        printer.header("Sázky");
        petr = bettorService.registerBettor(DemoIds.BETTOR_PETR, "Petr", 1000);
        jana = bettorService.registerBettor(DemoIds.BETTOR_JANA, "Jana", 1000);

        bettorService.placeBet(petr, marathonMarket, kipchogeThird, 100);
        printer.bet(petr.getName(), 100, "Kipchoge 3. místo", marathonMarket.getCurrentOdds(kipchogeThird));

        bettorService.placeBet(jana, marathonMarket, kipchogeThird, 100);
        printer.bet(jana.getName(), 100, "Kipchoge 3. místo", marathonMarket.getCurrentOdds(kipchogeThird));

        brokerService.updateOdds(marathonMarket, kipchogeThird, 2.20);
        System.out.printf("  bookmaker mění kurz na 'Kipchoge 3. místo' -> %.2f%n",
                marathonMarket.getCurrentOdds(kipchogeThird));

        bettorService.placeBet(jana, marathonMarket, kipchogeThird, 100);
        printer.bet(jana.getName(), 100, "Kipchoge 3. místo", marathonMarket.getCurrentOdds(kipchogeThird));
    }

    private void recordMarathonResults() {
        Race race = (Race) competitionService.getCompetition(DemoIds.COMP_MARATHON_RACE);
        List<Participant> runners = race.getParticipants();
        Participant p1 = findById(runners, DemoIds.RUNNER_1);
        Participant p2 = findById(runners, DemoIds.RUNNER_2);
        Participant p3 = findById(runners, DemoIds.RUNNER_3);
        Participant p4 = findById(runners, DemoIds.RUNNER_4);
        Participant p5 = findById(runners, DemoIds.RUNNER_5);

        List<Participant> finishOrder = List.of(p3, p4, p2, p1, p5);
        Map<Participant, ParticipantRaceStatus> statuses = Map.of(
                p1, ParticipantRaceStatus.FINISHED,
                p2, ParticipantRaceStatus.FINISHED,
                p3, ParticipantRaceStatus.FINISHED,
                p4, ParticipantRaceStatus.FINISHED,
                p5, ParticipantRaceStatus.FINISHED
        );
        RaceResult raceResult = new RaceResult(finishOrder, statuses);

        competitionService.recordResult(race, raceResult);
        evaluatorService.settleMarket(marathonMarket, raceResult);
        competitionService.saveAll();

        printer.header("Výsledek maratonu");
        printer.ranking(finishOrder);
    }

    private void printBalances() {
        SportEvent marathon = competitionService.getSportEvent(DemoIds.EVENT_MARATHON);
        printer.header("Bilance sázkařů (maraton)");
        printer.printBalances(bettorService.getBalances(marathon));
    }

    private void printExtras() {
        Race race = (Race) competitionService.getCompetition(DemoIds.COMP_MARATHON_RACE);
        printer.header("Sázkaři na maraton");
        printer.printBettors(bettorService.getBettorsForCompetition(race));
        printer.header("Sázkaři dle celkové vsazené částky");
        printer.printBettors(bettorService.getBettorsByTotalStake());
    }

    private Participant findById(List<Participant> participants, String id) {
        return participants.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElseThrow();
    }
}
