package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordGameActivity extends Activity {

	protected static final int ACTION_CHOOSE_WINNER = 1;
	protected static final int ACTION_CHOOSE_LOSER = 2;
	protected static final String WINNER_ID = "WINNER_ID";
	protected static final String LOSER_ID = "LOSER_ID";
	private Lobby lobby;
	private Player winner;
	private Player loser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.lobby = ((GlobalState) getApplication()).getLobby();
		setContentView(R.layout.activity_record_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_game, menu);
		
		findViewById(R.id.winnerPanel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(RecordGameActivity.this, PlayerChooserActivity.class);
				if (loser != null) {
					intent.putExtra(PlayerChooserActivity.EXCLUDE_PLAYERS, new long[] {loser.id()});
				}
				startActivityForResult(intent, ACTION_CHOOSE_WINNER);
			}
		});	
		findViewById(R.id.loserPanel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(RecordGameActivity.this, PlayerChooserActivity.class);
				if (winner != null) {
					intent.putExtra(PlayerChooserActivity.EXCLUDE_PLAYERS, new long[] {winner.id()});
				}
				startActivityForResult(intent, ACTION_CHOOSE_LOSER);
			}
		});
		findViewById(R.id.recordGameButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent result = new Intent("foo");
				result.putExtra(WINNER_ID, winner.id());
				result.putExtra(LOSER_ID, loser.id());
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		});
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_CHOOSE_WINNER) {
			if (resultCode == RESULT_OK) {
				Player p = lobby.playerById(data
						.getLongExtra(AddPlayerActivity.PLAYER_ID, 0));
				((ImageView) findViewById(R.id.winnerIcon)).setImageResource(p
						.getDrawable());
				((TextView) findViewById(R.id.winnerName)).setText(p
						.getName());
				findViewById(R.id.winnerPanel).setBackgroundColor(p
						.getColor());
				winner = p;
				checkEnableButton();
			}
		}
		if (requestCode == ACTION_CHOOSE_LOSER) {
			if (resultCode == RESULT_OK) {
				Player p = lobby.playerById(data
						.getLongExtra(AddPlayerActivity.PLAYER_ID, 0));
				((ImageView) findViewById(R.id.loserIcon)).setImageResource(p
						.getDrawable());
				((TextView) findViewById(R.id.loserName)).setText(p
						.getName());
				findViewById(R.id.loserPanel).setBackgroundColor(p
						.getColor());
				loser = p;
				checkEnableButton();
			}
		}
	}

	private void checkEnableButton() {
		if (winner != null && loser != null) {
			findViewById(R.id.recordGameButton).setEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
