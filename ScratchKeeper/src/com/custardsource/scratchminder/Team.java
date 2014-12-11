package com.custardsource.scratchminder;

import java.io.Serializable;
import java.util.Collection;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class Team implements Serializable {
	private static final long serialVersionUID = 1L;
	private Collection<Player> players;

	public Team(Collection<Player> of) {
		this.players = of;
	}

	public static Team of(Player... players) {
		return new Team(Lists.newArrayList(players));
	}

	public Collection<Player> players() {
		return players;
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("players", players).toString();
	}

}
