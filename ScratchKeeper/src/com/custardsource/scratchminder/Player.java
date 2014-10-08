package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	@SuppressWarnings("unused")  // throw away for deserialization purposes
	private int iconDrawable;
	private Avatar avatar = Avatar.caveman;
	private int color;
	private long id = UUID.randomUUID().getLeastSignificantBits();
	private long lastPlayed = System.currentTimeMillis();

	public Player(String name, Avatar avatar, int color) {
		super();
		this.name = name;
		this.avatar = avatar;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}

	public int getDrawable() {
		if (this.avatar == null) {
			this.avatar = Avatar.caveman;
		}
		return this.avatar.drawable();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public void setColour(Integer colour) {
		this.color = colour;
	}

	long id() {
		return this.id;
	}

	public long lastPlayed() {
		return lastPlayed;
	}

	public void recordPlay() {
		lastPlayed = System.currentTimeMillis();
	}

	public Avatar getAvatar() {
		return avatar;
	}
}
