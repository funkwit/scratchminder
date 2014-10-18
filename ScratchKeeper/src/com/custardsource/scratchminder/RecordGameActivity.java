package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

	// TODO: calculate this programatically
	private static final int PANEL_INITIAL_COLOUR = Color.rgb(102, 102, 102);
	private static final int WINNER_INITIAL_DRAWABLE = R.drawable.ic_action_person;
	private static final int WINNER_INITIAL_TEXT = R.string.click_to_choose_winner;
	private static final int LOSER_INITIAL_DRAWABLE = R.drawable.ic_action_person_sad;
	private static final int LOSER_INITIAL_TEXT = R.string.click_to_choose_loser;

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

		findViewById(R.id.winnerPanel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(RecordGameActivity.this,
								PlayerChooserActivity.class);
						if (loser != null) {
							intent.putExtra(
									PlayerChooserActivity.EXCLUDE_PLAYERS,
									new long[] { loser.id() });
						}
						startActivityForResult(intent, ACTION_CHOOSE_WINNER);
					}
				});
		findViewById(R.id.loserPanel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(RecordGameActivity.this,
						PlayerChooserActivity.class);
				if (winner != null) {
					intent.putExtra(PlayerChooserActivity.EXCLUDE_PLAYERS,
							new long[] { winner.id() });
				}
				startActivityForResult(intent, ACTION_CHOOSE_LOSER);
			}
		});
		findViewById(R.id.recordGameButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent result = new Intent("foo");
						result.putExtra(WINNER_ID, winner.id());
						result.putExtra(LOSER_ID, loser.id());
						setResult(Activity.RESULT_OK, result);
						finish();
					}
				});
		findViewById(R.id.swapButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Player temp = winner;
				winner = loser;
				loser = temp;
				updateWinner();
				updateLoser();
			}
		});
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_CHOOSE_WINNER) {
			if (resultCode == RESULT_OK) {
				winner = lobby.playerById(data.getLongExtra(
						AddPlayerActivity.PLAYER_ID, 0));
				updateWinner();
				checkEnableButton();
			}
		}
		if (requestCode == ACTION_CHOOSE_LOSER) {
			if (resultCode == RESULT_OK) {
				loser = lobby.playerById(data.getLongExtra(
						AddPlayerActivity.PLAYER_ID, 0));
				updateLoser();
				checkEnableButton();
			}
		}
	}

	private void updateWinner() {
		updatePanel(R.id.winnerPanel, R.id.winnerName, R.id.winnerIcon, winner,
				WINNER_INITIAL_TEXT, WINNER_INITIAL_DRAWABLE);
	}

	private void updatePanel(int panelId, int nameId, int iconId, Player src,
			int initialText, int initialDrawable) {
		TextView textView = (TextView) findViewById(nameId);
		ImageView imageView = (ImageView) findViewById(iconId);
		View panel = findViewById(panelId);
		if (src == null) {
			imageView.setImageResource(initialDrawable);
			textView.setText(initialText);
			panel.setBackgroundColor(PANEL_INITIAL_COLOUR);

		} else {
			imageView.setImageResource(src.getDrawable());
			textView.setText(src.getName());
			panel.setBackgroundColor(src.getColor());
		}

	}

	private void updateLoser() {
		updatePanel(R.id.loserPanel, R.id.loserName, R.id.loserIcon, loser,
				LOSER_INITIAL_TEXT, LOSER_INITIAL_DRAWABLE);
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
