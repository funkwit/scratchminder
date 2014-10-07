package com.custardsource.scratchminder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.custardsource.scratchminder.util.DialogUtils;
import com.google.common.collect.Ordering;

public class LobbyActivity extends Activity {

	private SharedPreferences sharedPref;
	private ArrayAdapter<Game> gamesAdapter;
	private Lobby lobby;
	private List<Game> sortedGames;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		lobby = ((GlobalState) getApplication()).getLobby();

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.lobby_drawer);
		final ListView drawerList = (ListView) findViewById(R.id.left_drawer_navigation);
		// Set the adapter for the list view
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.drawer_items)));
		// Set the list's click listener
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectLeftDrawerItem(position);
			}

			private void selectLeftDrawerItem(int position) {
				drawerList.setItemChecked(position, true);
				// TODO: call setTitle();
				drawerLayout.closeDrawers();
				if (position == 1) {
					Intent intent = new Intent(LobbyActivity.this,
							HeadToHeadActivity.class);
					startActivity(intent);
				}
			}
		});
		final CharSequence title = getTitle();
		final CharSequence drawerTitle = title;
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(title);
				invalidateOptionsMenu();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu();
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(drawerToggle);
		drawerList.setItemChecked(0, true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		final ListView gamesList = (ListView) findViewById(R.id.lobbyGames);
		sortedGames = new ArrayList<Game>();
		gamesAdapter = new ArrayAdapter<Game>(this,
				android.R.layout.simple_list_item_2, sortedGames) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Game game = getItem(position);
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) LobbyActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(R.layout.game_list_entry,
							parent, false);
				}
				TextView nameView = (TextView) rowView
						.findViewById(R.id.newGameName);
				TextView descriptionView = (TextView) rowView
						.findViewById(R.id.gameDescription);
				TextView dateView = (TextView) rowView
						.findViewById(R.id.gameDate);

				String names = game.getNameList();
				String description = game.description();
				if (description == null) {
					nameView.setText(names);
					descriptionView.setText("");
				} else {
					nameView.setText(description);
					descriptionView.setText(names);
				}
				dateView.setText(DateUtils.getRelativeTimeSpanString(game
						.lastPlayed()));
				return rowView;
			}
		};
		updateGamesList();
		gamesList.setAdapter(gamesAdapter);
		gamesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Game g = gamesAdapter.getItem(position);
				g.resumePlay();
				Intent intent = new Intent(LobbyActivity.this,
						ScoreboardActivity.class);
				intent.putExtra(ScoreboardActivity.GAME_ID, g.id());
				startActivity(intent);
			}
		});
		registerForContextMenu(gamesList);
	}

	private void updateGamesList() {
		this.sortedGames.clear();
		this.sortedGames.addAll(lobby.allGames());
		Collections.sort(sortedGames, new Ordering<Game>() {
			public int compare(Game g1, Game g2) {
				return Long.signum(g2.lastPlayed() - g1.lastPlayed());
			}
		});
		this.gamesAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		((GlobalState) getApplication()).flush();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateAllDisplay();
	}

	private void updateAllDisplay() {
		updateGamesList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.add_game) {
			Intent intent = new Intent(this, NewGameActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		if (v.getId() == R.id.lobbyGames) {
			inflater.inflate(R.menu.game_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.leave:

		case R.id.removeGame:
			removeGame((int) info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void removeGame(int position) {
		final Game g = gamesAdapter.getItem(position);
		DialogUtils.confirmDialog(this, new Runnable() {
			@Override
			public void run() {
				lobby.deleteGame(g);
				gamesAdapter.remove(g);

			}
		}, R.string.confirm_remove_title, R.string.confirm_remove_game_text);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

}
