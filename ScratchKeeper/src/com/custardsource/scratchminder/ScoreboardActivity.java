package com.custardsource.scratchminder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
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
	private static final int ACTION_ASSOCIATE_BADGE = 4;

	protected static final String GAME_ID = "GAME_ID";
	protected static final String EARCON_BELL = "[bell]";
	protected static final String EARCON_BUZZER = "[buzzer]";
	protected static final String EARCON_FANFARE = "[fanfare]";
	protected static final String EARCON_FAILFARE = "[failfare]";
	protected static final String EARCON_WAH_WAH_WAH = "[wahwahwah]";

	private int inProgressScore = 0;

	private Game game;

	private ArrayAdapter<Participant> scoreboardAdapter;
	private ArrayAdapter<Participant> notPlayingAdapter;
	private SharedPreferences sharedPref;
	private ListView notPlaying;
	private Lobby lobby;
	private TextView inProgressScoreView;
	private ListView scoreboard;
	private TextToSpeech textToSpeech;
	private boolean speechEnabled;
	private boolean audioOn = true;
	private boolean swipeInProgress;
	private StringBuilder code;

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
					rowView = inflater.inflate(R.layout.scoreboard_score_entry,
							parent, false);
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
				speakPlayerChangeIfNecessary();
				scoreboardAdapter.notifyDataSetChanged();
				refocus();
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
					rowView = inflater.inflate(
							R.layout.scoreboard_score_entry_inactive, parent,
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

		this.textToSpeech = new TextToSpeech(getApplicationContext(),
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status == TextToSpeech.SUCCESS) {
							speechEnabled = true;
							textToSpeech.addEarcon(EARCON_BELL,
									getApplication().getPackageName(),
									R.raw.bell);
							textToSpeech.addEarcon(EARCON_BUZZER,
									getApplication().getPackageName(),
									R.raw.buzzer);
							textToSpeech.addEarcon(EARCON_FANFARE,
									getApplication().getPackageName(),
									R.raw.fanfare);
							textToSpeech.addEarcon(EARCON_FAILFARE,
									getApplication().getPackageName(),
									R.raw.failfare);
							textToSpeech.addEarcon(EARCON_WAH_WAH_WAH,
									getApplication().getPackageName(),
									R.raw.wah_wah_wah);
						}

					}
				});
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
			refocus();
			return true;
		case R.id.removeFromGame:
			confirmRemove((int) info.id);
			refocus();
			return true;
		case R.id.removeFromGameInactive:
			confirmRemoveInactive((int) info.id);
			refocus();
			return true;
		case R.id.rejoin:
			rejoin(info.id);
			refocus();
			return true;
		case R.id.rejoinnext:
			rejoinNext(info.id);
			refocus();
			return true;
		case R.id.rejoinlast:
			rejoinLast(info.id);
			refocus();
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
		resumeParticipant(participant);
	}

	private void resumeParticipant(Participant participant) {
		game.rejoin(participant);
		scoreboardAdapter.insert(participant,
				game.playingPositionOf(participant));
		notPlayingAdapter.remove(participant);
		if (notPlayingAdapter.isEmpty()) {
			findViewById(R.id.notPlayingPanel).setVisibility(View.GONE);
		}
		speakRejoinIfNecessary(participant);
	}

	private void leave(long id) {
		Participant participant = game.partipantInActivePosition((int) id);
		suspendParticipant(participant);
	}

	private void suspendParticipant(Participant participant) {
		game.leave(participant);
		Log.d(Constants.TAG, "Removing player" + participant);
		scoreboardAdapter.remove(participant);
		notPlayingAdapter.add(participant);
		findViewById(R.id.notPlayingPanel).setVisibility(View.VISIBLE);
		speakLeaveIfNecessary(participant);
	}

	private void confirmRemove(final int position) {
		DialogUtils.confirmDialog(this, new Runnable() {
			public void run() {
				Participant participant = game
						.partipantInActivePosition((int) position);
				game.remove(participant);
				scoreboardAdapter.remove(participant);
				updateTotalScoreDisplay();
				speakRemoveIfNecessary(participant);
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
				speakRemoveIfNecessary(participant);
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
				addPlayerToGame(p);
			}
		} else if (requestCode == ACTION_EDIT) {
			if (resultCode == RESULT_OK) {
				scoreboardAdapter.notifyDataSetChanged();
				notPlayingAdapter.notifyDataSetChanged();
			}
		} else if (requestCode == ACTION_SETTINGS) {
			reapplyPreferences();
		} else if (requestCode == ACTION_ASSOCIATE_BADGE
				&& resultCode == RESULT_OK) {
			Player p = lobby.playerById(data.getLongExtra(
					AddPlayerActivity.PLAYER_ID, 0));
			lobby.registerBadgeCode(code.toString(), p);
			handleBadgeIn(p);

		}
	}

	private void addPlayerToGame(Player p) {
		scoreboardAdapter.add(game.addPlayer(p));
		p.recordPlay();
		updateAllDisplay();
		speakJoinIfNecessary(p);
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
		refocus();
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
		} else if (id == R.id.audio_toggle) {
			audioOn = !audioOn;
			item.setIcon(audioOn ? R.drawable.ic_action_volume_on
					: R.drawable.ic_action_volume_muted);
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int pressed = event.getUnicodeChar();
		if (pressed == Constants.BADGE_START) {
			swipeInProgress = true;
			code = new StringBuilder();
			return true;
		} else if (swipeInProgress) {
			if (pressed == Constants.BADGE_END) {
				swipeInProgress = false;
				if (code.length() != 0) {
					String badge = code.toString();
					Player p = lobby.playerByBadgeCode(badge);
					if (p == null) {
						DialogUtils.confirmDialog(
								this,
								new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent(
												ScoreboardActivity.this,
												PlayerChooserActivity.class);
										intent.putExtra(
												PlayerChooserActivity.FOR_BADGE_ASSOCIATION,
												true);
										startActivityForResult(intent,
												ACTION_ASSOCIATE_BADGE);
									}
								}, R.string.unrecognized_badge_title,
								R.string.unrecognized_badge_message);
					} else {
						handleBadgeIn(p);
					}
				}
				return true;
			} else if (pressed != 0) {
				code.appendCodePoint(pressed);
				return true;
			}
			return super.onKeyUp(keyCode, event);
		} else if (game.getParticipants().isEmpty()) {
			return super.onKeyUp(keyCode, event);
		}
		swipeInProgress = false;

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
			speakPlayerChangeIfNecessary();
			scoreboardAdapter.notifyDataSetChanged();
			scoreboard.setSelection(game.currentPlayerActivePosition());
			refocus();
			return true;

		case KeyEvent.KEYCODE_RIGHT_BRACKET:
			game.nextPlayer();
			speakPlayerChangeIfNecessary();
			scoreboardAdapter.notifyDataSetChanged();
			scoreboard.setSelection(game.currentPlayerActivePosition());
			refocus();
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

	private void handleBadgeIn(Player player) {
		Participant participant = game.getParticipantFor(player);
		if (participant == null) {
			addPlayerToGame(player);
		} else if (participant.active()) {
			suspendParticipant(participant);
		} else {
			resumeParticipant(participant);
		}
		refocus();
	}

	private boolean shouldPlaySfx() {
		return this.audioOn
				&& sharedPref.getBoolean("in_progress_sound_effects", false)
				&& this.speechEnabled;
	}

	private void speakCurrentScoreIfNecessary() {
		playPluralSpeechIfPrefEnabled("speak_last_player_verbosity",
				1.0f, R.plurals.commit_score_speech_text,
				R.string.commit_score_zero_speech_text,
				R.plurals.commit_score_speech_text_verbose,
				R.string.commit_score_zero_speech_text_verbose,
				inProgressScore,
				game.getActiveParticipant().playerNameForTts(),
				inProgressScore, game.getActiveParticipant().getScore());
	}

	private void speakNextPlayerIfNecessary() {
		playPluralSpeechIfPrefEnabled("speak_next_player_verbosity", 1.0f,
				R.plurals.next_player_speech_text,
				R.string.next_player_zero_speech_text,
				R.plurals.next_player_speech_text_verbose,
				R.string.next_player_zero_speech_text_verbose, game
						.getActiveParticipant().getScore(), game
						.getActiveParticipant().playerNameForTts(), game
						.getActiveParticipant().getScore());
	}

	private void speakPlayerChangeIfNecessary() {
		playSpeechIfPrefEnabled("speak_next_player_verbosity",
				R.string.player_change_speak_text,
				R.string.player_change_speak_text_verbose, game
						.getActiveParticipant().playerNameForTts(), game
						.getActiveParticipant().getScore());
	}

	private void speakInProgressScoreIfNecessary() {
		float pitchFactor = 1.0f;
		if (inProgressScore > 3) {
			pitchFactor += (inProgressScore - 3) * 0.1f;
		}
		playSpeechIfPrefEnabled("speak_in_progress_scores_verbosity",
				pitchFactor, R.string.change_score_speech_text,
				R.string.change_score_speech_text, game.getActiveParticipant()
						.playerNameForTts(), inProgressScore);
	}

	private void speakJoinIfNecessary(Player p) {
		playSfxIfEnabled(EARCON_FANFARE);
		playSpeechIfPrefEnabled("speak_player_changes_verbosity",
				R.string.player_join_speak_text,
				R.string.player_join_speak_text_verbose, p.getNameForTts());
	}

	private void speakRejoinIfNecessary(Participant participant) {
		playSfxIfEnabled(EARCON_FANFARE);
		playSpeechIfPrefEnabled("speak_player_changes_verbosity",
				R.string.player_rejoin_speak_text,
				R.string.player_rejoin_speak_text_verbose,
				participant.playerNameForTts(), participant.getScore());
	}

	private void speakLeaveIfNecessary(Participant participant) {
		playSfxIfEnabled(EARCON_FAILFARE);
		playSpeechIfPrefEnabled("speak_player_changes_verbosity",
				R.string.player_leave_speak_text,
				R.string.player_leave_speak_text_verbose,
				participant.playerNameForTts(), participant.getScore());
	}

	private void speakRemoveIfNecessary(Participant participant) {
		playSfxIfEnabled(EARCON_WAH_WAH_WAH);
		playSpeechIfPrefEnabled("speak_player_changes_verbosity",
				R.string.player_remove_speak_text,
				R.string.player_remove_speak_text_verbose,
				participant.playerNameForTts(), participant.getScore());
	}

	private void playPluralSpeechIfPrefEnabled(String prefName,
			float pitchFactor, int pluralIdTerse, int zeroStringTerse,
			int pluralIdVerbose, int zeroStringVerbose, int count,
			Object... stringArgs) {
		String verbosity = sharedPref.getString(prefName, "OFF");
		int pluralId;
		int zeroString;
		if (verbosity.equals("OFF")) {
			return;
		} else if (verbosity.equals("TERSE")) {
			pluralId = pluralIdTerse;
			zeroString = zeroStringTerse;
		} else {
			pluralId = pluralIdVerbose;
			zeroString = zeroStringVerbose;
		}
		String toSpeak;

		if (count == 0) {
			toSpeak = getString(zeroString, stringArgs);
		} else {
			toSpeak = getResources().getQuantityString(pluralId, count,
					stringArgs);
		}

		speakText(toSpeak, pitchFactor);
	}

	private void speakText(String toSpeak, float pitchFactor) {
		if (this.audioOn
				&& sharedPref.getBoolean("text_to_speech_enabled", false)
				&& this.speechEnabled) {
			if (sharedPref.getBoolean("debug_speech_as_toast", false)) {
				Toast.makeText(this, "SPEAK: " + toSpeak + " " + pitchFactor,
						Toast.LENGTH_SHORT).show();
			} else {
				this.textToSpeech.setPitch(pitchFactor);
				this.textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
			}
		}
	}

	private void playSpeechIfPrefEnabled(String prefName, int terseStringId,
			int verboseStringId, Object... stringArgs) {
		playSpeechIfPrefEnabled(prefName, 1.0f, terseStringId, verboseStringId,
				stringArgs);
	}

	private void playSpeechIfPrefEnabled(String prefName, float pitchFactor,
			int terseStringId, int verboseStringId, Object... stringArgs) {
		String verbosity = sharedPref.getString(prefName, "OFF");
		int stringId;
		if (verbosity.equals("OFF")) {
			return;
		} else if (verbosity.equals("TERSE")) {
			stringId = terseStringId;
		} else {
			stringId = verboseStringId;
		}

		speakText(getString(stringId, stringArgs), pitchFactor);
	}

	private void clickPlus() {
		playSfxIfEnabled(EARCON_BELL);
		inProgressScore += 1;
		speakInProgressScoreIfNecessary();
		inProgressScoreView.setText(Integer.toString(inProgressScore));
		refocus();
	}

	private void playSfxIfEnabled(String earcon) {
		if (shouldPlaySfx()) {
			this.textToSpeech.playEarcon(earcon, TextToSpeech.QUEUE_ADD, null);
		}
	}

	private void clickMinus() {
		playSfxIfEnabled(EARCON_BUZZER);
		inProgressScore -= 1;
		speakInProgressScoreIfNecessary();
		inProgressScoreView.setText(Integer.toString(inProgressScore));
		refocus();
	}

	private void clickOk() {
		game.recordScoreForActivePlayer(inProgressScore);
		speakCurrentScoreIfNecessary();
		inProgressScore = 0;
		inProgressScoreView.setText(Integer.toString(inProgressScore));
		game.nextPlayer();
		speakNextPlayerIfNecessary();
		scoreboardAdapter.notifyDataSetChanged();
		scoreboard.setSelection(game.currentPlayerActivePosition());
		updateTotalScoreDisplay();
		refocus();
	}

	private void refocus() {
		findViewById(R.id.commitScore).requestFocus();
	}
}
