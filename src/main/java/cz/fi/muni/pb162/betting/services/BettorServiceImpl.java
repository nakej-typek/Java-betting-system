package cz.fi.muni.pb162.betting.services;

import cz.fi.muni.pb162.betting.model.bet.Bet;
import cz.fi.muni.pb162.betting.model.bet.BettingMarket;
import cz.fi.muni.pb162.betting.model.bettor.Bettor;
import cz.fi.muni.pb162.betting.model.bettor.RegisteredBettor;
import cz.fi.muni.pb162.betting.model.competition.Competition;
import cz.fi.muni.pb162.betting.model.competition.SportEvent;
import cz.fi.muni.pb162.betting.model.outcome.Outcome;
import cz.fi.muni.pb162.betting.model.result.CancelledResult;
import cz.fi.muni.pb162.betting.model.result.CompetitionResult;
import cz.fi.muni.pb162.betting.services.dto.BettorBalance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of BettorService.
 *
 * @author Marek Jargaš
 */
public class BettorServiceImpl implements BettorService {

    private final Map<String, RegisteredBettor> bettors = new HashMap<>();
    private final Map<Bettor, List<Bet>> bets = new HashMap<>();
    private final CompetitionService competitionService;
    private final BettingBrokerService brokerService;

    /**
     * Creates a new BettorServiceImpl with the given services.
     *
     * @param competitionService service for retrieving competitions
     * @param brokerService      service for retrieving betting markets
     */
    public BettorServiceImpl(CompetitionService competitionService, BettingBrokerService brokerService) {
        this.competitionService = competitionService;
        this.brokerService = brokerService;
    }

    @Override
    public RegisteredBettor registerBettor(String id, String name, double initialBalance) {
        RegisteredBettor bettor = new RegisteredBettor(id, name, initialBalance);
        bettors.put(id, bettor);
        return bettor;
    }

    @Override
    public Bet placeBet(Bettor bettor, BettingMarket market, Outcome outcome, double amount) {
        Bet bet = new Bet(bettor, amount, outcome, market.getCurrentOdds(outcome));
        market.addBet(bet);
        bets.computeIfAbsent(bettor, b -> new ArrayList<>()).add(bet);
        bettor.setBalance(bettor.getBalance() - amount);
        return bet;
    }

    @Override
    public List<BettorBalance> getBalances(SportEvent event) {
        Map<Bettor, double[]> totals = new HashMap<>();
        competitionService.getCompetitions(event).stream()
                .map(this::marketBetsWithResult)
                .flatMap(List::stream)
                .forEach(entry -> accumulate(totals, entry));
        return totals.entrySet().stream()
                .map(e -> new BettorBalance(e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]))
                .toList();
    }

    @Override
    public List<Bettor> getBettorsForCompetition(Competition competition) {
        BettingMarket market = brokerService.getMarket(competition);
        if (market == null) {
            return List.of();
        }
        return market.getBets().stream()
                .map(Bet::bettor)
                .distinct()
                .toList();
    }

    @Override
    public List<Bettor> getBettorsByTotalStake() {
        return bets.entrySet().stream()
                .sorted(Comparator.comparingDouble(
                        (Map.Entry<Bettor, List<Bet>> e) -> e.getValue().stream().mapToDouble(Bet::amount).sum()
                ).reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<Bet> getBets(Bettor bettor) {
        List<Bet> bettorBets = bets.get(bettor);
        if (bettorBets == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(bettorBets);
    }

    @Override
    public void creditWinnings(Bettor bettor, double amount) {
        bettor.setBalance(bettor.getBalance() + amount);
    }

    private List<BetWithResult> marketBetsWithResult(Competition competition) {
        BettingMarket market = brokerService.getMarket(competition);
        if (market == null) {
            return List.of();
        }
        CompetitionResult result = competition.getResult();
        return market.getBets().stream()
                .map(bet -> new BetWithResult(bet, result))
                .toList();
    }

    private void accumulate(Map<Bettor, double[]> totals, BetWithResult entry) {
        Bet bet = entry.bet();
        CompetitionResult result = entry.result();
        double[] t = totals.computeIfAbsent(bet.bettor(), b -> new double[3]);
        t[0] += bet.amount();
        if (result == null) {
            return;
        }
        if (result instanceof CancelledResult) {
            t[1] += bet.amount();
        } else if (bet.outcome().matches(result)) {
            t[1] += bet.amount() * bet.odds();
        } else {
            t[2] += bet.amount();
        }
    }

    /**
     * Pairs a bet with the result of the competition it was placed on.
     *
     * @author Marek Jargaš
     */
    private record BetWithResult(Bet bet, CompetitionResult result) {
    }
}
