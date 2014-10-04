package com.custardsource.scratchminder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import com.custardsource.scratchminder.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ScoreboardActivity extends Activity {
	private static final int ACTION_ADD = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_SETTINGS = 3;

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	protected static final String GAME_ID = "GAME_ID";

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private int inProgressScore = 0;

	private Game game;

	private ArrayAdapter<Participant> scoreboardAdapter;
	private ArrayAdapter<Participant> notPlayingAdapter;
	private SharedPreferences sharedPref;
	private ListView notPlaying;
	private Lobby lobby;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_scoreboard);
		lobby = ((GlobalState) getApplication()).getLobby();

		this.game = lobby.gameById(getIntent().getLongExtra(GAME_ID, 0));

		/*
		 * final View controlsView =
		 * findViewById(R.id.fullscreen_content_controls); final View
		 * contentView = findViewById(R.id.fullscreen_content);
		 * 
		 * // Set up an instance of SystemUiHider to control the system UI for
		 * // this activity. mSystemUiHider = SystemUiHider.getInstance(this,
		 * contentView, HIDER_FLAGS); mSystemUiHider.setup(); mSystemUiHider
		 * .setOnVisibilityChangeListener(new
		 * SystemUiHider.OnVisibilityChangeListener() { // Cached values. int
		 * mControlsHeight; int mShortAnimTime;
		 * 
		 * @Override
		 * 
		 * @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) public void
		 * onVisibilityChange(boolean visible) { if (Build.VERSION.SDK_INT >=
		 * Build.VERSION_CODES.HONEYCOMB_MR2) { // If the ViewPropertyAnimator
		 * API is available // (Honeycomb MR2 and later), use it to animate the
		 * // in-layout UI controls at the bottom of the // screen. if
		 * (mControlsHeight == 0) { mControlsHeight = controlsView.getHeight();
		 * } if (mShortAnimTime == 0) { mShortAnimTime =
		 * getResources().getInteger( android.R.integer.config_shortAnimTime); }
		 * controlsView .animate() .translationY(visible ? 0 : mControlsHeight)
		 * .setDuration(mShortAnimTime); } else { // If the ViewPropertyAnimator
		 * APIs aren't // available, simply show or hide the in-layout UI //
		 * controls. controlsView.setVisibility(visible ? View.VISIBLE :
		 * View.GONE); }
		 * 
		 * if (visible && AUTO_HIDE) { // Schedule a hide().
		 * delayedHide(AUTO_HIDE_DELAY_MILLIS); } } });
		 * 
		 * // Set up the user interaction to manually show or hide the system
		 * UI. contentView.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { if (TOGGLE_ON_CLICK) {
		 * mSystemUiHider.toggle(); } else { mSystemUiHider.show(); } } });
		 * 
		 * // Upon interacting with UI controls, delay any scheduled hide() //
		 * operations to prevent the jarring behavior of controls going away //
		 * while interacting with the UI.
		 * findViewById(R.id.dummy_button).setOnTouchListener(
		 * mDelayHideTouchListener);
		 */
		final Context context = getApplicationContext();
		final ListView scoreboard = (ListView) findViewById(R.id.scoreboardListView);
		scoreboardAdapter = new ArrayAdapter<Participant>(this,
				android.R.layout.simple_list_item_1,
				game.getActiveParticipants()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Participant participant = game
						.partipantInActivePosition(position);
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(R.layout.score, parent, false);
				TextView nameView = (TextView) rowView.findViewById(R.id.name);
				TextView scoreView = (TextView) rowView
						.findViewById(R.id.score);
				TextView scoreHistoryView = (TextView) rowView
						.findViewById(R.id.scoreHistory);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.icon);
				imageView.setImageResource(participant.playerDrawable());
				imageView.setScaleX(0.5f);
				imageView.setScaleY(0.5f);
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
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(R.layout.disabled_score,
						parent, false);
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

		final TextView inProgressScoreView = (TextView) findViewById(R.id.inprogressScore);
		((Button) findViewById(R.id.plusone))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inProgressScore += 1;
						inProgressScoreView.setText(Integer
								.toString(inProgressScore));
					}
				});
		((Button) findViewById(R.id.minusone))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						inProgressScore -= 1;
						inProgressScoreView.setText(Integer
								.toString(inProgressScore));
					}
				});
		((Button) findViewById(R.id.commitScore))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						game.recordScoreForActivePlayer(inProgressScore);
						inProgressScore = 0;
						inProgressScoreView.setText(Integer
								.toString(inProgressScore));
						game.nextPlayer();
						scoreboardAdapter.notifyDataSetChanged();
						updateTotalScoreDisplay();
					}
				});

		registerForContextMenu(scoreboard);
		registerForContextMenu(notPlaying);
		reapplyPreferences();
		updateAllDisplay();
	}

	// TODO: total score
	// TODO: graphs

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		// delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_ADD) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				Player p = lobby.playerById(data
						.getLongExtra(AddPlayerActivity.PLAYER_ID, 0));
				scoreboardAdapter.add(game.addPlayer(p));
			}
		}
		if (requestCode == ACTION_EDIT) {
			if (resultCode == RESULT_OK) {
				scoreboardAdapter.notifyDataSetChanged();
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
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
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
			intent.putExtra(PlayerChooserActivity.EXCLUDE_PLAYERS, game
					.playerIdsAsArray());
			startActivityForResult(intent, ACTION_ADD);
			return true;
		} else if (id == R.id.reset_scores) {
			// TODO: add confirmation dialog
			game.resetScores();
			scoreboardAdapter.notifyDataSetChanged();
			notPlayingAdapter.notifyDataSetChanged();
			updateTotalScoreDisplay();
			return true;
		} else if (id == R.id.settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateTotalScoreDisplay() {
		((TextView) findViewById(R.id.totalScoreValue)).setText(Integer
				.toString(game.totalScore()));
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
}
