package com.custardsource.scratchminder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class League {
	private String name;
	private Map<Player, Double> rankings = new HashMap<Player, Double>();
	private long id = UUID.randomUUID().getLeastSignificantBits();

	public League(String name) {
		this.name = name;
	}

	public long id() {
		return id;
	}

	public String name() {
		return name;
	}

}
