package com.custardsource.scratchminder;

import java.io.Serializable;

public class LeagueGame implements Serializable {
	private static final long serialVersionUID = 1L;
	private Player winner;
	private Player loser;
	private long timestamp = System.currentTimeMillis();

	public LeagueGame(Player winner, Player loser) {
		this.winner = winner;
		this.loser = loser;
	}

	public Player winner() {
		return winner;
	}

	public Player loser() {
		return loser;
	}

	public long timestamp() {
		return timestamp;
	}
}
