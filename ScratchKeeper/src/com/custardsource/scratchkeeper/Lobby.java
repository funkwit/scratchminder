package com.custardsource.scratchkeeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;

public class Lobby implements Serializable {
	private static final long serialVersionUID = 2L;
	private List<Game> allGames = new ArrayList<Game>();
	private Map<Long, Game> gamesById = new HashMap<Long, Game>();

	public Lobby() {
		Player p1 = new Player("Cow", R.drawable.remember_the_milk, Color.rgb(
				0, 0, 80));
		Player p2 = new Player("Chris", R.drawable.pterodactyl, Color.rgb(80,
				0, 0));
		Player p3 = new Player("Krijesta", R.drawable.dino_orange, Color.rgb(
				80, 0, 80));
		Player p4 = new Player("Bod", R.drawable.caveman, Color.rgb(80, 80, 0));

		Game g1 = new Game();
		Game g2 = new Game();
		g2.setDescription("Awesome Game");
		g1.addPlayer(p1);
		g1.addPlayer(p2);
		g1.addPlayer(p3);
		g1.leave(g1.partipantInActivePosition(2));
		g2.addPlayer(p1);
		g2.addPlayer(p4);
		addGame(g1);
		addGame(g2);
	}
	
	List<Game> allGames() {
		return this.allGames;
	}
	
	void addGame(Game game) {
		allGames.add(game);
		gamesById.put(game.id(), game);
	}

	public Game gameById(long id) {
		return gamesById.get(id);
	}
}
