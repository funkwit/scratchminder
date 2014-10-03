package com.custardsource.scratchkeeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

public class Game implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final List<Player> players = new ArrayList<Player>();
	int activePlayerIndex = 0;

	public void addPlayer(Player player) {
		players.add(player);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public int playerCount() {
		return players.size();
	}

	public void recordScoreForActivePlayer(int score) {
		players.get(activePlayerIndex).recordScore(score);
	}

	public void nextPlayer() {
		activePlayerIndex = (activePlayerIndex + 1) % players.size();
		while (!players.get(activePlayerIndex).active()) {
			activePlayerIndex = (activePlayerIndex + 1) % players.size();

		}
	}

	public boolean isPlaying(Player player) {
		return players.get(activePlayerIndex) == player;
	}

	public List<Player> getActivePlayers() {
		List<Player> active = new ArrayList<Player>();
		for (Player player : players) {
			if (player.active()) {
				active.add(player);
			}
		}
		return active;
	}

	public Player playerInActivePosition(int position) {
		Log.i("SK", "Position: " + position);
		int current = -1;
		for (Player player : players) {
			Log.i("SK", "Checking player: " + player.toString());
			if (player.active()) {
				current++;
				Log.i("SK", "Player is active");
				if (current == position) {
					Log.i("SK", "returning");
					return player;
				}
			}
		}
		return null;
	}

	public void leave(Player player) {
		player.leave();
		if (isPlaying(player)) {
			nextPlayer();
		}

	}

	public Player playerInInactivePosition(int position) {
		// TODO-refactor
		Log.i("SK", "Position: " + position);
		int current = -1;
		for (Player player : players) {
			Log.i("SK", "Checking player: " + player.toString());
			if (!player.active()) {
				current++;
				Log.i("SK", "Player is inactive");
				if (current == position) {
					Log.i("SK", "returning");
					return player;
				}
			}
		}
		return null;
	}

	public void movePlayerLast(Player player) {
		Player active = players.get(activePlayerIndex);
		players.remove(player);
		players.add(player);
		switchPlayTo(active);
	}

	public void movePlayerNext(Player player) {
		Player active = players.get(activePlayerIndex);
		players.remove(player);
		players.add(activePlayerIndex + 1, player);
		switchPlayTo(active);
	}

	public void rejoin(Player player) {
		player.join();
	}

	public int playingPositionOf(Player player) {
		return getActivePlayers().indexOf(player);
	}

	public void switchPlayTo(Player player) {
		activePlayerIndex = players.indexOf(player);
	}

	public void replacePlayer(Player oldPlayer, Player newPlayer) {
		int index = players.indexOf(oldPlayer);
		players.remove(index);
		players.add(index, newPlayer);

	}
	// TODO(beam)

	public Collection<? extends Player> getInactivePlayers() {
		List<Player> inactive = new ArrayList<Player>();
		for (Player player : players) {
			if (!player.active()) {
				inactive.add(player);
			}
		}
		return inactive;
	}

	public void resetScores() {
		for (Player player : players) {
			player.resetScore();
		}
	}

}
