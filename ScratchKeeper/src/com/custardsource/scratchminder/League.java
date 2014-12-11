package com.custardsource.scratchminder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class League implements Serializable {
	public static class HeadToHeadSummary {

		private final Player otherPlayer;
		private int wins;
		private int losses;

		public HeadToHeadSummary(Player other) {
			this.otherPlayer = other;
		}

		public void recordWin() {
			wins++;

		}

		public void recordLoss() {
			losses++;
		}

		public int wins() {
			return wins;
		}

		public int losses() {
			return losses;
		}

		public Player otherPlayer() {
			return otherPlayer;
		}

	}

	private static final GameInfo GAME_INFO = GameInfo.getDefaultGameInfo();
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<Player, Rating> trueSkillRatings = Maps.newHashMap();
	
	private long id = UUID.randomUUID().getLeastSignificantBits();
	private List<LeagueGame> games = new ArrayList<LeagueGame>();
	private Avatar avatar = Avatar.caveman;
	private int minTeamSize = 1;
	private int maxTeamSize = 1;
	private int minTeamCount = 2;
	private int maxTeamCount = 2;
	
	@SuppressWarnings("unused")
	private Map<Player, Double> eloRatings = new HashMap<Player, Double>();

	
	private static final Ordering<Map.Entry<Player, Double>> RATING_ENTRY_COMPARATOR = new Ordering<Map.Entry<Player, Double>>() {
		public int compare(Map.Entry<Player, Double> x,
				Map.Entry<Player, Double> y) {
			return Double.compare(y.getValue(), x.getValue());
		}
	};
	private static final Function<Rating, Double> RATING_TO_CONSERVATIVE_RATING = new Function<Rating, Double>() {
		public Double apply(Rating r) {
			return r.getConservativeRating();
		}
	};
	
	public League(String name) {
		this.name = name;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// Temporary data migration.
		if (trueSkillRatings == null) {
			trueSkillRatings = Maps.newHashMap();
			recalculateAllRatings();
		}
		if (minTeamCount == 0) {
			minTeamCount = 2;
			maxTeamCount = 2;
			minTeamSize = 1;
			maxTeamSize = 1;
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
		updateTrueSkillRatings(game);
		return game;
	}

	public boolean hasResults() {
		return !games.isEmpty();
	}


	public List<Map.Entry<Player, Double>> playersByConservativeTrueSkillRating() {
		return playersByConservativeTrueSkillRating(trueSkillRatings);
	}

	public List<Map.Entry<Player, Double>> playersByConservativeTrueSkillRating(Map<Player, Rating> data) {
		List<Map.Entry<Player, Double>> sorted = Lists.newArrayList(Maps
				.transformValues(data,
						RATING_TO_CONSERVATIVE_RATING).entrySet());
		Collections.sort(sorted, RATING_ENTRY_COMPARATOR);
		return sorted;
	}
	
	public List<Player> playerRankingsByConservativeTrueSkillRating(Map<Player, Rating> data) {
		return Lists.transform(playersByConservativeTrueSkillRating(data), new Function<Map.Entry<Player, Double>, Player>(){
			public Player apply(Map.Entry<Player, Double> in) {
				return in.getKey();
			}
		});
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
		trueSkillRatings.clear();
		for (LeagueGame game : games) {
			updateTrueSkillRatings(game);
		}
	}

	private void updateTrueSkillRatings(LeagueGame game) {
		updateTrueSkillRatings(game, trueSkillRatings);
	}

	private void updateTrueSkillRatings(LeagueGame game,
			Map<Player, Rating> ratings) {
		int rank = 1;
		List<ITeam> teams = Lists.newArrayList();
		List<Integer> rankings = Lists.newArrayList();
		for (Collection<com.custardsource.scratchminder.Team> orderedTeams : game.results()) {
			for (com.custardsource.scratchminder.Team evenlyRankedTeam : orderedTeams) {
				ITeam team = new Team();
				for (Player p : evenlyRankedTeam.players()) {
					team.put(p, trueSkillRatingFor(ratings, p));
				}
				teams.add(team);
				rankings.add(rank);
			}
			rank++;
		}
		int[] rankingsInts = new int[rankings.size()];
		for (int index = 0; index < rankings.size(); index++) {
			rankingsInts[index] = rankings.get(index);
		}
		for (Map.Entry<IPlayer, Rating> newRating : TrueSkillCalculator
				.calculateNewRatings(GAME_INFO, teams,
						rankingsInts).entrySet()) {
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

	public Iterable<Rating> trueSkillRatingsOverTimeFor(final Player p) {
		return Iterables.filter(Iterables.transform(trueSkillRatingsOverTime(),
				new Function<Map<Player, Rating>, Rating>() {
					public Rating apply(Map<Player, Rating> in) {
						return in.get(p);
					}
				}), Predicates.notNull());
	}

	public Iterable<Map<Player, Double>> trueSkillConservativeRatingsOverTime() {
		return Iterables.transform(trueSkillRatingsOverTime(),
				new Function<Map<Player, Rating>, Map<Player, Double>>() {
					public Map<Player, Double> apply(Map<Player, Rating> in) {
						return Maps.transformValues(in,
								RATING_TO_CONSERVATIVE_RATING);
					}
				});
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

	public Collection<HeadToHeadSummary> summaryForPlayer(Player p) {
		if (!isHeadToHead()) {
			return Collections.emptyList();
		}
		Map<Player, HeadToHeadSummary> byPlayer = Maps.newHashMap();
		for (LeagueGame g : games) {
			Player other = null;
			List<Collection<com.custardsource.scratchminder.Team>> results = g.results();
			Player winner = Iterables.getOnlyElement(Iterables.getOnlyElement(results.get(0)).players());
			Player loser = Iterables.getOnlyElement(Iterables.getOnlyElement(results.get(1)).players());
			if (p.id() == winner.id()) {
				other = loser;
			} else	if (p.id() == loser.id()) {
				other = winner;
			}

			if (other != null) {
				HeadToHeadSummary summary = byPlayer.get(other);
				if (summary == null) {
					summary = new HeadToHeadSummary(other);
					byPlayer.put(other, summary);
				}
				if (p.id() == winner.id()) {
					summary.recordWin();
				} else {
					summary.recordLoss();
				}
			}
		}
		return byPlayer.values();
	}

	private boolean isHeadToHead() {
		return (minTeamCount == 2 && maxTeamCount == 2 && minTeamSize == 1 && maxTeamSize == 1);
	}
}