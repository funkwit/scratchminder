package com.custardsource.scratchminder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

@SuppressLint("UseSparseArrays")
// Sparse Array isn't serializable.
public class Lobby implements Serializable {
	private static final long serialVersionUID = 4L;
	private List<Game> allGames = new ArrayList<Game>();
	private Map<Long, Game> gamesById = new HashMap<Long, Game>();
	private List<Player> allPlayers = new ArrayList<Player>();
	private Map<Long, Player> playersById = new HashMap<Long, Player>();
	private List<League> allLeagues = new ArrayList<League>();
	private Map<Long, League> leaguesById = new LinkedHashMap<Long, League>();
	private Map<String, Player> playersByBadgeCode = Maps.newHashMap();

	public Lobby() {
		Player p1 = addPlayer("Cow", Avatar.remember_the_milk,
				Color.rgb(0, 0, 80));
		Player p2 = addPlayer("Chris", Avatar.pterodactyl, Color.rgb(80, 0, 0));
		Player p3 = addPlayer("Krijesta", Avatar.dino_orange,
				Color.rgb(80, 0, 80));
		Player p4 = addPlayer("Bod", Avatar.caveman, Color.rgb(80, 80, 0));

		Game g1 = addGame(null);
		Game g2 = addGame("Awesome Game");
		g1.addPlayer(p1);
		g1.addPlayer(p2);
		g1.addPlayer(p3);
		g1.leave(g1.partipantInActivePosition(2));
		g2.addPlayer(p1);
		g2.addPlayer(p4);
	}

	public void resetIfNecessary() {
		allLeagues = new ArrayList<League>();
		leaguesById = new LinkedHashMap<Long, League>();
	}

	List<Game> allGames() {
		return this.allGames;
	}

	public Game gameById(long id) {
		return gamesById.get(id);
	}

	Player addPlayer(String name, Avatar avatar, int color) {
		Player p = new Player(name, avatar, color);
		allPlayers.add(p);
		playersById.put(p.id(), p);
		return p;
	}

	List<Player> allPlayers() {
		return this.allPlayers;
	}

	public Player playerById(long id) {
		return playersById.get(id);
	}

	public Game addGame(String description) {
		Game g = new Game();
		if (!"".equals(description)) {
			g.setDescription(description);
		}
		allGames.add(g);
		gamesById.put(g.id(), g);
		return g;
	}

	public void deleteGame(Game g) {
		allGames.remove(g);
		gamesById.remove(g.id());
	}

	public List<League> getLeagues() {
		return this.allLeagues;
	}

	public League addLeague(String name) {
		League l = new League(name);
		allLeagues.add(l);
		leaguesById.put(l.id(), l);
		return l;
	}

	public League leagueById(long id) {
		return leaguesById.get(id);
	}

	public Map<Integer, Integer> colourPopularityMap(
			Set<Player> playersToExclude) {
		Map<Integer, Integer> results = Maps.newHashMap();
		for (Player player : allPlayers) {
			if (!playersToExclude.contains(player)) {
				Integer colour = player.getColor();
				if (results.containsKey(colour)) {
					results.put(colour, results.get(colour) + 1);
				} else {
					results.put(colour, 1);
				}
			}
		}
		return results;
	}

	public Map<Avatar, Integer> avatarPopularityMap(Set<Player> playersToExclude) {
		Map<Avatar, Integer> results = Maps.newHashMap();
		for (Player player : allPlayers) {
			if (!playersToExclude.contains(player)) {
				Avatar avatar = player.getAvatar();
				if (results.containsKey(avatar)) {
					results.put(avatar, results.get(avatar) + 1);
				} else {
					results.put(avatar, 1);
				}
			}
		}
		return results;
	}

	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		if (playersByBadgeCode == null) {
			playersByBadgeCode = Maps.newHashMap();
		}

	}

	public void registerBadgeCode(String badgeCode, Player p) {
		if (!Strings.isNullOrEmpty(p.getBadgeCode())) {
			Player alreadyInUseBy = playersByBadgeCode.get(badgeCode);
			if (alreadyInUseBy != null) {
				alreadyInUseBy.setBadgeCode(null);
				playersByBadgeCode.remove(p.getBadgeCode());
			}
		}
		p.setBadgeCode(badgeCode);
		if (Strings.isNullOrEmpty(badgeCode)) {
			return;
		}
		playersByBadgeCode.put(badgeCode, p);
	}

	public Player playerByBadgeCode(String badge) {
		return playersByBadgeCode.get(badge);
	}
}