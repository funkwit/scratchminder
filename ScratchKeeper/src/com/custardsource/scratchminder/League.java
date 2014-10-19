package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
	@SuppressWarnings("unused")
	// throw away for deserialization purposes
	private int drawable = 0;
	private Avatar avatar = Avatar.caveman;

	public League(String name) {
		this.name = name;
	}

	public long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public LeagueGame recordResult(Player winner, Player loser) {
		LeagueGame game = new LeagueGame(winner, loser);
		if (games == null) {
			games = new ArrayList<LeagueGame>();
		}
		games.add(game);
		winner.recordPlay();
		loser.recordPlay();
		updateRankings(game);
		return game;
	}

	private void updateRankings(LeagueGame game) {
		updateRankings(game, rankings);

	}

	private void updateRankings(LeagueGame game, Map<Player, Double> rankings) {
		// Taken from
		// https://svn.apache.org/repos/asf/labs/openelo/src/main/java/org/apache/openelo/RankingCalculator.java
		double qWinner = calculateQFactor(rankingFor(game.winner(), rankings));
		double qLoser = calculateQFactor(rankingFor(game.loser(), rankings));

		double eWinner = calculateEFactor(qWinner, qLoser);
		double eLoser = calculateEFactor(qLoser, qWinner);

		setRanking(game.winner(), DEFAULT_K_FACTOR, 1, eWinner, rankings);
		setRanking(game.loser(), DEFAULT_K_FACTOR, 0, eLoser, rankings);
	}

	private double rankingFor(Player player, Map<Player, Double> rankings) {
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
			double eFactor, Map<Player, Double> rankings) {
		double newRanking = rankingFor(player, rankings)
				+ (kFactor * (sFactor - eFactor));
		rankings.put(player, newRanking);
	}

	public boolean hasResults() {
		return !games.isEmpty();
	}

	public List<Map.Entry<Player, Double>> playersByRank() {
		List<Map.Entry<Player, Double>> sorted = Lists.newArrayList(rankings
				.entrySet());
		Collections.sort(sorted, new Ordering<Map.Entry<Player, Double>>() {
			public int compare(Map.Entry<Player, Double> x,
					Map.Entry<Player, Double> y) {
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

	public List<LeagueGame> allGames() {
		return recentGames(Integer.MAX_VALUE);
	}

	public Long lastPlayed() {
		if (games.isEmpty()) {
			return null;
		}
		return games.get(games.size() - 1).timestamp();
	}

	public int getDrawable() {
		if (this.avatar == null) {
			this.avatar = Avatar.ic_poolballs_barkerbaggies_13;
		}
		return this.avatar.drawable();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public Avatar getAvatar() {
		return this.avatar;
	}

	public void deleteGame(LeagueGame g) {
		games.remove(g);
		recalculateAllRankings();
	}

	private void recalculateAllRankings() {
		rankings.clear();
		for (LeagueGame game : games) {
			updateRankings(game);
		}
	}

	public Iterable<Map<Player, Double>> rankingsOverTime() {
		final Map<Player, Double> scoresCopy = Maps.newHashMap();
		return Iterables.transform(games,
				new Function<LeagueGame, Map<Player, Double>>() {
					public Map<Player, Double> apply(LeagueGame game) {
						updateRankings(game, scoresCopy);
						return scoresCopy;
					}
				});

	}
}