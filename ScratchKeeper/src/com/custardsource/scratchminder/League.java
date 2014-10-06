package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class League implements Serializable {
	private static final long serialVersionUID = 1L;
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
