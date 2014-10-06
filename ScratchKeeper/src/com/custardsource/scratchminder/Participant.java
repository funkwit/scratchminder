package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class Participant implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int RECENT_SCORES = 5;

	private final Player player;
	private int score = 0;
	private final List<Integer> scores = new ArrayList<Integer>();
	private boolean active = true;

	public Participant(Player player) {
		this.player = player;
	}

	public void recordScore(int score) {
		scores.add(score);
		this.score += score;
		player.recordPlay();
	}

	public boolean active() {
		return this.active;
	}

	public void leave() {
		active = false;
	}

	public void join() {
		active = true;
		player.recordPlay();
	}

	public String getScoreHistoryString() {
		List<String> recentScores = new ArrayList<String>(RECENT_SCORES);
		for (Integer score : Lists.reverse(scores)) {
			if (score != 0) { // TODO - optional?
				recentScores.add(String.format("%+d", score)); // TODO - actual
																// score?
				if (recentScores.size() == RECENT_SCORES) {
					break;
				}
			}
		}
		return Joiner.on("  ").join(Lists.reverse(recentScores));
	}

	public void resetScore() {
		this.score = 0;
		this.scores.clear();
	}

	public int getScore() {
		return score;
	}

	@Override
	public String toString() {
		return player.getName() + ": " + score;
	}

	public String playerName() {
		return player.getName();
	}

	public long playerId() {
		return player.id();
	}

	public int playerDrawable() {
		return player.getDrawable();
	}

	public int playerColor() {
		return player.getColor();
	}
}
