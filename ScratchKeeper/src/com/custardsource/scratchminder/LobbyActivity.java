package com.custardsource.scratchminder;

import com.custardsource.scratchminder.util.DialogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LobbyActivity extends Activity {

	private SharedPreferences sharedPref;
	private ArrayAdapter<Game> gamesAdapter;
	private Lobby lobby;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		lobby = ((GlobalState) getApplication()).getLobby();
		
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


		final ListView gamesList = (ListView) findViewById(R.id.lobbyGames);
		gamesAdapter = new ArrayAdapter<Game>(this,
				android.R.layout.simple_list_item_2, lobby.allGames()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Game game = getItem(position);
				LayoutInflater inflater = (LayoutInflater) LobbyActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(R.layout.game_list_entry,
						parent, false);
				TextView nameView = (TextView) rowView
						.findViewById(R.id.newGameName);
				TextView descriptionView = (TextView) rowView
						.findViewById(R.id.gameDescription);

				String names = game.getNameList();
				String description = game.description();
				if (description == null) {
					nameView.setText(names);
					descriptionView.setText("");
				} else {
					nameView.setText(description);
					descriptionView.setText(names);
				}
				return rowView;
			}
		};
		gamesList.setAdapter(gamesAdapter);
		gamesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Game g = gamesAdapter.getItem(position);
				Intent intent = new Intent(LobbyActivity.this, ScoreboardActivity.class);
				intent.putExtra(ScoreboardActivity.GAME_ID, g.id());
				startActivity(intent);
			}
		});
		registerForContextMenu(gamesList);
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
		gamesAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
}
