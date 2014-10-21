package com.custardsource.scratchminder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jskills.GameInfo;
import jskills.IPlayer;
import jskills.ITeam;
import jskills.Rating;
import jskills.Team;
import jskills.TrueSkillCalculator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class League implements Serializable {
	private static final GameInfo GAME_INFO = GameInfo.getDefaultGameInfo();
	private static final double DEFAULT_POW_BASE = 10;
	private static final double DEFAULT_DIVISOR = 400;
	private static final int DEFAULT_K_FACTOR = 32;
	private static final int DEFAULT_RATING = 1200;
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<Player, Rating> trueSkillRatings = Maps.newHashMap();
	private Map<Player, Double> eloRatings = new HashMap<Player, Double>();
	private long id = UUID.randomUUID().getLeastSignificantBits();
	private List<LeagueGame> games = new ArrayList<LeagueGame>();
	private Avatar avatar = Avatar.caveman;

	// throw away for deserialization purposes
	// @SuppressWarnings("unused")
	// private transient int drawable = 0;
	// @SuppressWarnings("unused")
	// private Map<Player, Double> rankings = new HashMap<Player, Double>();

	public League(String name) {
		this.name = name;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// Temporary data migration.
		if (trueSkillRatings == null || eloRatings == null) {
			trueSkillRatings = Maps.newHashMap();
			eloRatings = Maps.newHashMap();
			recalculateAllRatings();
		}
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
		updateEloRatings(game);
		updateTrueSkillRatings(game);
		return game;
	}

	public boolean hasResults() {
		return !games.isEmpty();
	}

	public List<Map.Entry<Player, Double>> playersByRank() {
		List<Map.Entry<Player, Double>> sorted = Lists.newArrayList(eloRatings
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
		recalculateAllRatings();
	}

	private void recalculateAllRatings() {
		eloRatings.clear();
		trueSkillRatings.clear();
		for (LeagueGame game : games) {
			updateEloRatings(game);
			updateTrueSkillRatings(game);
		}
	}

	private void updateEloRatings(LeagueGame game) {
		updateEloRatings(game, eloRatings);
	}

	private void updateEloRatings(LeagueGame game, Map<Player, Double> ratings) {
		// Taken from
		// https://svn.apache.org/repos/asf/labs/openelo/src/main/java/org/apache/openelo/RankingCalculator.java
		double qWinner = calculateQFactor(eloRatingFor(game.winner(), ratings));
		double qLoser = calculateQFactor(eloRatingFor(game.loser(), ratings));

		double eWinner = calculateEFactor(qWinner, qLoser);
		double eLoser = calculateEFactor(qLoser, qWinner);

		setEloRating(game.winner(), DEFAULT_K_FACTOR, 1, eWinner, ratings);
		setEloRating(game.loser(), DEFAULT_K_FACTOR, 0, eLoser, ratings);
	}

	private double eloRatingFor(Player player, Map<Player, Double> ratings) {
		if (ratings.containsKey(player)) {
			return ratings.get(player);
		}
		return DEFAULT_RATING;
	}

	private static double calculateQFactor(double rating) {
		return Math.pow(DEFAULT_POW_BASE, rating / DEFAULT_DIVISOR);
	}

	private static double calculateEFactor(double qA, double qB) {
		return qA / (qA + qB);
	}

	private void setEloRating(Player player, double kFactor, double sFactor,
			double eFactor, Map<Player, Double> ratings) {
		double newRating = eloRatingFor(player, ratings)
				+ (kFactor * (sFactor - eFactor));
		ratings.put(player, newRating);
	}

	public Iterable<Map<Player, Double>> eloRatingsOverTime() {
		final Map<Player, Double> scoresCopy = Maps.newHashMap();
		return Iterables.transform(games,
				new Function<LeagueGame, Map<Player, Double>>() {
					public Map<Player, Double> apply(LeagueGame game) {
						updateEloRatings(game, scoresCopy);
						return scoresCopy;
					}
				});

	}

	private void updateTrueSkillRatings(LeagueGame game) {
		updateTrueSkillRatings(game, trueSkillRatings);
	}

	private void updateTrueSkillRatings(LeagueGame game,
			Map<Player, Rating> ratings) {
		Player winner = game.winner();
		Player loser = game.loser();
		Rating winnerRating = trueSkillRatingFor(ratings, winner);
		Rating loserRating = trueSkillRatingFor(ratings, loser);
		ITeam t1 = new Team(winner, winnerRating);
		ITeam t2 = new Team(loser, loserRating);

		for (Map.Entry<IPlayer, Rating> newRating : TrueSkillCalculator
				.calculateNewRatings(GAME_INFO, Team.concat(t1, t2),
						new int[] { 1, 2 }).entrySet()) {
			ratings.put((Player) newRating.getKey(), newRating.getValue());
		}
	}

	private Rating trueSkillRatingFor(Map<Player, Rating> ratings, Player loser) {
		Rating loserRating = ratings.get(loser);
		if (loserRating == null) {
			loserRating = GAME_INFO.getDefaultRating();
		}
		return loserRating;
	}

	public Iterable<Map<Player, Rating>> trueSkillRatingsOverTime() {
		final Map<Player, Rating> scoresCopy = Maps.newHashMap();
		return Iterables.transform(games,
				new Function<LeagueGame, Map<Player, Rating>>() {
					public Map<Player, Rating> apply(LeagueGame game) {
						updateTrueSkillRatings(game, scoresCopy);
						return scoresCopy;
					}
				});
	}
	
	public boolean supportsElo() {
		return true;
	}
}