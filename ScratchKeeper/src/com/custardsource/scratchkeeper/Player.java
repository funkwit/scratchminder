package com.custardsource.scratchkeeper;

import java.io.Serializable;
import java.util.UUID;


public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int iconDrawable;
	private  int color;
	private long id = UUID.randomUUID().getLeastSignificantBits();

	public Player(String name, int iconDrawable, int color) {
		super();
		this.name = name;
		this.iconDrawable = iconDrawable;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}


	public int getDrawable() {
		return this.iconDrawable;
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

	long id() {
		return this.id;
	}
}
