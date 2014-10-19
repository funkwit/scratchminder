package com.custardsource.scratchminder;

public interface LeagueGameListener {
	public void onGameAdded(LeagueGame game);

	public void onGameDeleted(LeagueGame g);
}