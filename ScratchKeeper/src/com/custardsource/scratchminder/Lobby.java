package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;

public class Lobby implements Serializable {
	private static final long serialVersionUID = 4L;
	private List<Game> allGames = new ArrayList<Game>();
	private Map<Long, Game> gamesById = new HashMap<Long, Game>();
	private List<Player> allPlayers = new ArrayList<Player>();
	private Map<Long, Player> playersById = new HashMap<Long, Player>();

	public Lobby() {
		Player p1 = addPlayer("Cow", R.drawable.remember_the_milk,
				Color.rgb(0, 0, 80));
		Player p2 = addPlayer("Chris", R.drawable.pterodactyl,
				Color.rgb(80, 0, 0));
		Player p3 = addPlayer("Krijesta", R.drawable.dino_orange,
				Color.rgb(80, 0, 80));
		Player p4 = addPlayer("Bod", R.drawable.caveman, Color.rgb(80, 80, 0));
		
		Game g1 = addGame(null);
		Game g2 = addGame("Awesome Game");
		g1.addPlayer(p1);
		g1.addPlayer(p2);
		g1.addPlayer(p3);
		g1.leave(g1.partipantInActivePosition(2));
		g2.addPlayer(p1);
		g2.addPlayer(p4);
	}

	List<Game> allGames() {
		return this.allGames;
	}

	public Game gameById(long id) {
		return gamesById.get(id);
	}
	
	Player addPlayer(String name, int drawable, int color) {
		Player p = new Player(name, drawable, color);
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
}