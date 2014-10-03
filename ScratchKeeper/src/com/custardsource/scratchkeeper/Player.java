package com.custardsource.scratchkeeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int RECENT_SCORES = 5;

	private String name;

	private int iconDrawable;
	private int score = 0;

	private  int color;
	private final List<Integer> scores = new ArrayList<Integer>();

	private boolean active = true;

	public Player(String name, int iconDrawable, int color) {
		super();
		this.name = name;
		this.iconDrawable = iconDrawable;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	@Override
	public String toString() {
		return name + ": " + score;
	}

	public int getColor() {
		return color;
	}

	public void recordScore(int score) {
		scores.add(score);
		this.score += score;
	}

	public int getDrawable() {
		return this.iconDrawable;
	}

	public boolean active() {
		return this.active;
	}

	public void leave() {
		active = false;
	}

	public void join() {
		active = true;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setDrawable(Integer drawable) {
		this.iconDrawable = drawable;
	}

	public void setColour(Integer colour) {
		this.color = colour;
	}
}
