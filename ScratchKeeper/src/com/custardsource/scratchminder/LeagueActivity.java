package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LeagueActivity extends Activity {

	private Lobby lobby;
	private League league;
	protected static final String LEAGUE_ID = "LEAGUE_ID";
	private static final int ACTIVITY_RECORD_GAME = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_league);
		lobby = ((GlobalState) getApplication()).getLobby();

		this.league = lobby.leagueById(getIntent().getLongExtra(LEAGUE_ID, 0));
		setTitle(league.name());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.league, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.record_game) {
			Intent intent = new Intent(this, RecordGameActivity.class);
			startActivityForResult(intent, ACTIVITY_RECORD_GAME);
			return true;
		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_RECORD_GAME) {
			if (resultCode == RESULT_OK) {
				Player winner = lobby.playerById(data
						.getLongExtra(RecordGameActivity.WINNER_ID, 0));
				Player loser = lobby.playerById(data
						.getLongExtra(RecordGameActivity.LOSER_ID, 0));
				league.recordResult(winner, loser);
				winner.recordPlay();
				loser.recordPlay();
			}
		}
	}

}
