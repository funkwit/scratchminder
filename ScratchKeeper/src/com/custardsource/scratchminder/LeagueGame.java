package com.custardsource.scratchminder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LeagueGame implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Collection<Team>> results;
	private long timestamp = System.currentTimeMillis();

	// Legacy serialized data
	@SuppressWarnings("unused")
	private Player winner;
	@SuppressWarnings("unused")
	private Player loser;

	public LeagueGame(Player winner, Player loser) {
		results = Lists.newArrayList();
		results.add(Collections.singleton(Team.of(winner)));
		results.add(Collections.singleton(Team.of(loser)));
	}

	public long timestamp() {
		return timestamp;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// Temporary data migration.
		if (results == null) {
			results = Lists.newArrayList();
			results.add(Collections.singleton(Team.of(winner)));
			results.add(Collections.singleton(Team.of(loser)));
		}
	}

	// TODO: deprecate
	public Player winner() {
		return Iterables.getOnlyElement(Iterables
				.getOnlyElement(results.get(0)).players());
	}

	// TODO: deprecate
	public Player loser() {
		return Iterables.getOnlyElement(Iterables
				.getOnlyElement(results.get(1)).players());
	}

	public List<Collection<Team>> results() {
		return results;
	}
}
