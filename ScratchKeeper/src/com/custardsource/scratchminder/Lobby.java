package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Color;

@SuppressLint("UseSparseArrays") // Sparse Array isn't serializable.
public class Lobby implements Serializable {
	private static final long serialVersionUID = 4L;
	private List<Game> allGames = new ArrayList<Game>();
	private Map<Long, Game> gamesById = new HashMap<Long, Game>();
	private List<Player> allPlayers = new ArrayList<Player>();
	private Map<Long, Player> playersById = new HashMap<Long, Player>();
	private List<League> allLeagues = new ArrayList<League>();
	private Map<Long, League> leaguesById = new LinkedHashMap<Long, League>();

	public Lobby() {
		Player p1 = addPlayer("Cow", Avatar.remember_the_milk,
				Color.rgb(0, 0, 80));
		Player p2 = addPlayer("Chris", Avatar.pterodactyl,
				Color.rgb(80, 0, 0));
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
		//allLeagues = new ArrayList<League>();
		//leaguesById = new LinkedHashMap<Long, League>();
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
}