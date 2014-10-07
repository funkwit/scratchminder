package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class League implements Serializable {
	private static final int DEFAULT_RANKING = 1200;
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<Player, Double> rankings = new HashMap<Player, Double>();
	private long id = UUID.randomUUID().getLeastSignificantBits();
	private List<LeagueGame> games = new ArrayList<LeagueGame>();
	private static final double DEFAULT_POW_BASE = 10;
	private static final double DEFAULT_DIVISOR = 400;
	private static final int DEFAULT_K_FACTOR = 32;

	public League(String name) {
		this.name = name;
	}

	public long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public void recordResult(Player winner, Player loser) {
		LeagueGame game = new LeagueGame(winner, loser);
		if (games == null) {
			games = new ArrayList<LeagueGame>();
		}
		games.add(game);
		winner.recordPlay();
		loser.recordPlay();
		updateRankings(game);
	}

	private void updateRankings(LeagueGame game) {
		// Taken from
		// https://svn.apache.org/repos/asf/labs/openelo/src/main/java/org/apache/openelo/RankingCalculator.java
		double qWinner = calculateQFactor(rankingFor(game.winner()));
		double qLoser = calculateQFactor(rankingFor(game.loser()));

		double eWinner = calculateEFactor(qWinner, qLoser);
		double eLoser = calculateEFactor(qLoser, qWinner);

		setRanking(game.winner(), DEFAULT_K_FACTOR, 1, eWinner);
		setRanking(game.loser(), DEFAULT_K_FACTOR, 0, eLoser);

	}

	private double rankingFor(Player player) {
		if (rankings.containsKey(player)) {
			return rankings.get(player);
		}
		return DEFAULT_RANKING;
	}

	private static double calculateQFactor(double ranking) {
		return Math.pow(DEFAULT_POW_BASE, ranking / DEFAULT_DIVISOR);
	}

	private static double calculateEFactor(double qA, double qB) {
		return qA / (qA + qB);
	}

	private void setRanking(Player player, double kFactor, double sFactor,
			double eFactor) {
		double newRanking = rankingFor(player)
				+ (kFactor * (sFactor - eFactor));
		rankings.put(player, newRanking);
	}

	public boolean hasResults() {
		return !games.isEmpty();
	}

	public List<Map.Entry<Player, Double>> playersByRank() {
		List<Map.Entry<Player, Double>> sorted = Lists.newArrayList(rankings.entrySet());
		Collections.sort(sorted, new Ordering<Map.Entry<Player, Double>>() {
			public int compare(Map.Entry<Player, Double> x, Map.Entry<Player, Double> y) {
				return Double.compare(y.getValue(), x.getValue());
			}
		});
		return sorted;
	}

	public List<LeagueGame> recentGames(int count) {
		if (count >= games.size()) {
			return Lists.reverse(new ArrayList<LeagueGame>(games));
		}
		return Lists.reverse((new ArrayList<LeagueGame>(games.subList(
				games.size() - count, games.size()))));
	}
}
