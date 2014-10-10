package com.custardsource.scratchminder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.custardsource.scratchminder.util.DialogUtils;

public class ScoreboardActivity extends Activity {
	private static final int ACTION_ADD = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_SETTINGS = 3;

	protected static final String GAME_ID = "GAME_ID";

	private int inProgressScore = 0;

	private Game game;

	private ArrayAdapter<Participant> scoreboardAdapter;
	private ArrayAdapter<Participant> notPlayingAdapter;
	private SharedPreferences sharedPref;
	private ListView notPlaying;
	private Lobby lobby;
	private TextView inProgressScoreView;
	private ListView scoreboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_scoreboard);
		lobby = ((GlobalState) getApplication()).getLobby();

		this.game = lobby.gameById(getIntent().getLongExtra(GAME_ID, 0));

		final Context context = getApplicationContext();
		scoreboard = (ListView) findViewById(R.id.scoreboardListView);
		scoreboard.setSelection(game.currentPlayerActivePosition());
		scoreboardAdapter = new ArrayAdapter<Participant>(this,
				android.R.layout.simple_list_item_1,
				game.getActiveParticipants()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Participant participant = game
						.partipantInActivePosition(position);
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) context
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(R.layout.scoreboard_score_entry, parent, false);
				}
				TextView nameView = (TextView) rowView.findViewById(R.id.name);
				TextView scoreView = (TextView) rowView
						.findViewById(R.id.score);
				TextView scoreHistoryView = (TextView) rowView
						.findViewById(R.id.scoreHistory);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.icon);
				imageView.setImageResource(participant.playerDrawable());
				imageView.setScaleX(0.75f);
				imageView.setScaleY(0.75f);
				nameView.setText(participant.playerName());
				scoreView.setText(Integer.toString(participant.getScore()));
				scoreHistoryView.setText(participant.getScoreHistoryString());
				rowView.setBackgroundColor(game
						.isCurrentParticipant(participant) ? participant
						.playerColor() : Color.DKGRAY);
				// rowView.setSelected(game.isPlaying(player));
				return rowView;

			}
		};
		scoreboard.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				setPlaying((Participant) scoreboard.getItemAtPosition(position));
			}

			private void setPlaying(Participant player) {
				game.switchPlayTo(player);
				scoreboardAdapter.notifyDataSetChanged();
			}
		});
		scoreboard.setAdapter(scoreboardAdapter);
		// scoreboard.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		notPlaying = (ListView) findViewById(R.id.notPlayingListView);
		// TODO - persist the list
		notPlayingAdapter = new ArrayAdapter<Participant>(this,
				android.R.layout.simple_list_item_1,
				new ArrayList<Participant>()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Participant participant = notPlayingAdapter.getItem(position);
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) context
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(R.layout.scoreboard_score_entry_inactive, parent,
							false);
				}
				TextView nameView = (TextView) rowView.findViewById(R.id.name);
				TextView scoreView = (TextView) rowView
						.findViewById(R.id.score);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.icon);
				imageView.setImageResource(participant.playerDrawable());
				imageView.setScaleX(0.5f);
				imageView.setScaleY(0.5f);
				nameView.setText(participant.playerName());
				scoreView.setText(Integer.toString(participant.getScore()));
				rowView.setBackgroundColor(Color.DKGRAY);
				return rowView;
			}
		};
		notPlaying.setAdapter(notPlayingAdapter);

		inProgressScoreView = (TextView) findViewById(R.id.inprogressScore);
		((Button) findViewById(R.id.plusone))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						clickPlus();
					}
				});
		((Button) findViewById(R.id.minusone))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						clickMinus();
					}
				});
		((Button) findViewById(R.id.commitScore))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						clickOk();
					}
				});

		registerForContextMenu(scoreboard);
		registerForContextMenu(notPlaying);
		reapplyPreferences();
		updateAllDisplay();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		// delayedHide(100);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		if (v.getId() == R.id.notPlayingListView) {
			inflater.inflate(R.menu.inactive_player_menu, menu);
		} else {
			inflater.inflate(R.menu.active_player_menu, menu);
			menu.findItem(R.id.leave).setEnabled(
					game.getActiveParticipants().size() > 1);
			menu.findItem(R.id.removeFromGame).setEnabled(
					game.getActiveParticipants().size() > 1);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.leave:
			leave(info.id);
			return true;
		case R.id.removeFromGame:
			confirmRemove((int) info.id);
			return true;
		case R.id.removeFromGameInactive:
			confirmRemoveInactive((int) info.id);
			return true;
		case R.id.rejoin:
			rejoin(info.id);
			return true;
		case R.id.rejoinnext:
			rejoinNext(info.id);
			return true;
		case R.id.rejoinlast:
			rejoinLast(info.id);
			return true;
			// TODO: show who's next down the bottom
		case R.id.edit:
			edit((int) info.id);
			return true;
		case R.id.editInactive:
			editInactive((int) info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void edit(int position) {
		Intent intent = new Intent(this, AddPlayerActivity.class);
		intent.putExtra(AddPlayerActivity.PLAYER_ID, game
				.partipantInActivePosition(position).playerId());
		startActivityForResult(intent, ACTION_EDIT);

		// TODO Rematch in action bar

	}

	private void editInactive(int position) {
		Intent intent = new Intent(this, AddPlayerActivity.class);
		intent.putExtra(AddPlayerActivity.PLAYER_ID,
				notPlayingAdapter.getItem(position).playerId());
		startActivityForResult(intent, ACTION_EDIT);
	}

	private void rejoinLast(long id) {
		Participant participant = notPlayingAdapter.getItem((int) id);
		game.moveParticipantLast(participant);
		rejoin(id);
	}

	private void rejoinNext(long id) {
		Participant participant = notPlayingAdapter.getItem((int) id);
		game.moveParticipantNext(participant);
		rejoin(id);
	}

	private void rejoin(long id) {
		Participant participant = notPlayingAdapter.getItem((int) id);
		game.rejoin(participant);
		scoreboardAdapter.insert(participant,
				game.playingPositionOf(participant));
		notPlayingAdapter.remove(participant);
		if (notPlayingAdapter.isEmpty()) {
			findViewById(R.id.notPlayingPanel).setVisibility(View.GONE);
		}
	}

	private void leave(long id) {
		Participant participant = game.partipantInActivePosition((int) id);
		game.leave(participant);
		Log.e("SK", "Removing player" + participant);
		scoreboardAdapter.remove(participant);
		notPlayingAdapter.add(participant);
		findViewById(R.id.notPlayingPanel).setVisibility(View.VISIBLE);
	}

	private void confirmRemove(final int position) {
		DialogUtils.confirmDialog(this, new Runnable() {
			public void run() {
				Participant participant = game
						.partipantInActivePosition((int) position);
				game.remove(participant);
				scoreboardAdapter.remove(participant);
				updateTotalScoreDisplay();
			}
		}, R.string.confirm_remove_title, R.string.confirm_remove_text);
	}

	private void confirmRemoveInactive(final int position) {
		DialogUtils.confirmDialog(this, new Runnable() {
			@Override
			public void run() {
				Participant participant = notPlayingAdapter.getItem(position);
				game.remove(participant);
				notPlayingAdapter.remove(participant);
				if (notPlayingAdapter.isEmpty()) {
					findViewById(R.id.notPlayingPanel).setVisibility(View.GONE);
				}
				updateTotalScoreDisplay();
			}
		}, R.string.confirm_remove_title, R.string.confirm_remove_text);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_ADD) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				Player p = lobby.playerById(data.getLongExtra(
						AddPlayerActivity.PLAYER_ID, 0));
				scoreboardAdapter.add(game.addPlayer(p));
				p.recordPlay();
				updateAllDisplay();
			}
		}
		if (requestCode == ACTION_EDIT) {
			if (resultCode == RESULT_OK) {
				scoreboardAdapter.notifyDataSetChanged();
				notPlayingAdapter.notifyDataSetChanged();
			}
		}
		if (requestCode == ACTION_SETTINGS) {
			reapplyPreferences();
		}
	}

	private void reapplyPreferences() {
		findViewById(R.id.totalScorePanel).setVisibility(
				sharedPref.getBoolean("show_total", true) ? View.VISIBLE
						: View.GONE);
		if (sharedPref.getBoolean("lock_screen", false)) {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		updateTotalScoreDisplay();
	}

	private void updateAllDisplay() {
		notPlayingAdapter.clear();
		notPlayingAdapter.addAll(game.getInactiveParticipants());
		scoreboardAdapter.clear();
		scoreboardAdapter.addAll(game.getActiveParticipants());
		updateTotalScoreDisplay();
		findViewById(R.id.notPlayingPanel).setVisibility(
				game.getInactiveParticipants().isEmpty() ? View.GONE
						: View.VISIBLE);
		findViewById(R.id.scoringPanel).setVisibility(
				game.getParticipants().isEmpty() ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scoreboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_player) {
			Intent intent = new Intent(this, PlayerChooserActivity.class);
			intent.putExtra(PlayerChooserActivity.EXCLUDE_PLAYERS,
					game.playerIdsAsArray());
			startActivityForResult(intent, ACTION_ADD);
			return true;
		} else if (id == R.id.reset_scores) {
			DialogUtils.confirmDialog(this, new Runnable() {
				@Override
				public void run() {
					game.resetScores();
					scoreboardAdapter.notifyDataSetChanged();
					notPlayingAdapter.notifyDataSetChanged();
					updateTotalScoreDisplay();
				}
			}, R.string.confirm_reset_game_title,
					R.string.confirm_reset_game_text);
			return true;
		} else if (id == R.id.remove_game) {
			DialogUtils
					.confirmDialog(this, new Runnable() {
						@Override
						public void run() {
							lobby.deleteGame(game);
							finish();
						}
					}, R.string.confirm_remove_title,
							R.string.confirm_remove_game_text);
			return true;
		} else if (id == R.id.settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateTotalScoreDisplay() {
		boolean includeAbandoned = sharedPref.getBoolean("include_abandoned",
				true);
		((TextView) findViewById(R.id.abandonedScore)).setText(Integer
				.toString(game.abandonedPoints()));
		findViewById(R.id.abandonedScorePanel).setVisibility(
				includeAbandoned && game.abandonedPoints() > 0 ? View.VISIBLE
						: View.GONE);
		((TextView) findViewById(R.id.totalScoreValue)).setText(Integer
				.toString(game.totalScore(includeAbandoned)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO: use a listener
		reapplyPreferences();
	}

	@Override
	protected void onPause() {
		((GlobalState) getApplication()).flush();
		super.onPause();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (game.getParticipants().isEmpty()) {
			return super.onKeyUp(keyCode, event);
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_PLUS:
		case KeyEvent.KEYCODE_NUMPAD_ADD:
		case KeyEvent.KEYCODE_EQUALS:
			clickPlus();
			return true;

		case KeyEvent.KEYCODE_MINUS:
		case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
			clickMinus();
			return true;

		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_NUMPAD_ENTER:
		case KeyEvent.KEYCODE_SPACE:
			clickOk();
			return true;

		case KeyEvent.KEYCODE_DPAD_DOWN:
			clickMinus();
			return true;

		case KeyEvent.KEYCODE_LEFT_BRACKET:
			game.previousPlayer();
			scoreboardAdapter.notifyDataSetChanged();
			scoreboard.setSelection(game.currentPlayerActivePosition());
			return true;

		case KeyEvent.KEYCODE_RIGHT_BRACKET:
			game.nextPlayer();
			scoreboardAdapter.notifyDataSetChanged();
			scoreboard.setSelection(game.currentPlayerActivePosition());
			return true;

		default:
			String message = "Received unhandled keyup: " + keyCode + " "
					+ KeyEvent.keyCodeToString(keyCode);
			Log.d("ScratchMinder", message);
			if (sharedPref.getBoolean("debug_scoreboard_keys", false)) {
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			}

			return super.onKeyUp(keyCode, event);
		}
	}

	private void clickPlus() {
		inProgressScore += 1;
		inProgressScoreView.setText(Integer.toString(inProgressScore));
	}

	private void clickMinus() {
		inProgressScore -= 1;
		inProgressScoreView.setText(Integer.toString(inProgressScore));
	}

	private void clickOk() {
		game.recordScoreForActivePlayer(inProgressScore);
		inProgressScore = 0;
		inProgressScoreView.setText(Integer.toString(inProgressScore));
		game.nextPlayer();
		scoreboardAdapter.notifyDataSetChanged();
		scoreboard.setSelection(game.currentPlayerActivePosition());
		updateTotalScoreDisplay();
		// TODO: shouldn't do this here, but will do for now
		((GlobalState) getApplication()).flush();
	}
}
