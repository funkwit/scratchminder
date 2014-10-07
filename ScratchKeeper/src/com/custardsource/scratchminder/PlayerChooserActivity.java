package com.custardsource.scratchminder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

public class PlayerChooserActivity extends Activity {

	private static final int ACTION_NEW_PLAYER = 1;

	public static final String EXCLUDE_PLAYERS = "EXCLUDE_PLAYERS";

	private Lobby lobby;
	private ArrayAdapter<Player> playerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_chooser);
		this.lobby = ((GlobalState) getApplication()).getLobby();

		final ListView playerList = (ListView) findViewById(R.id.choosePlayer);
		
		List<Player> players = new ArrayList<Player>(lobby.allPlayers());
		long[] exclude = getIntent().getLongArrayExtra(EXCLUDE_PLAYERS);
		if (exclude != null) {
			Set<Long> excludeSet = new HashSet<Long>(Longs.asList(exclude));
			Iterator<Player> iter = players.iterator();
			while (iter.hasNext()) {
			    if (excludeSet.contains(iter.next().id())) {
			        iter.remove();
			    }
			}
		}
		Collections.sort(players, new Ordering<Player>() {
			public int compare(Player p1, Player p2) {
				return Long.signum(p2.lastPlayed() - p1.lastPlayed());
			}
		});
		this.playerAdapter = new ArrayAdapter<Player>(this,
				android.R.layout.simple_list_item_2, players) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Player player = getItem(position);
				View rowView = convertView;
				if (rowView != null) {
					LayoutInflater inflater = (LayoutInflater) PlayerChooserActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(R.layout.player_list_entry,
							parent, false);
				}
				TextView nameView = (TextView) rowView
						.findViewById(R.id.playerName);
				ImageView iconView = (ImageView) rowView
						.findViewById(R.id.playerIcon);

				nameView.setText(player.getName());
				iconView.setImageResource(player.getDrawable());

				TextView dateView = (TextView) rowView
						.findViewById(R.id.playerDate);
				dateView.setText(DateUtils.getRelativeTimeSpanString(player
						.lastPlayed()));
				
				return rowView;
			}
		};
		playerList.setAdapter(playerAdapter);
		playerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Player p = playerAdapter.getItem(position);
				Intent result = new Intent("foo");
				result.putExtra(AddPlayerActivity.PLAYER_ID, p.id());
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player_chooser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.add_player) {
			Intent intent = new Intent(this, AddPlayerActivity.class);
			startActivityForResult(intent, ACTION_NEW_PLAYER);
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
		if (requestCode == ACTION_NEW_PLAYER) {
			if (resultCode == RESULT_OK) {
				// Just go straight back to our own parent
				Intent result = new Intent("foo");
				result.putExtra(AddPlayerActivity.PLAYER_ID,
						data.getLongExtra(AddPlayerActivity.PLAYER_ID, 0));
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		}
	}
}
