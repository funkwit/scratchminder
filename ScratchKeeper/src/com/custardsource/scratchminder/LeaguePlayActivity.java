package com.custardsource.scratchminder;

import java.util.List;

import com.custardsource.scratchminder.util.DrawerUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LeaguePlayActivity extends Activity {
	private static final int ACTIVITY_NEW_LEAGUE = 1;
	private Lobby lobby;
	private List<League> leagues;
	private ArrayAdapter<League> leaguesAdapter;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_league_play);

		this.lobby = ((GlobalState) getApplication()).getLobby();
		this.leagues = lobby.getLeagues();

		final ListView leagueList = (ListView) findViewById(R.id.leagueList);
		leaguesAdapter = new ArrayAdapter<League>(this,
				android.R.layout.simple_list_item_1, this.leagues) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				League league = getItem(position);
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) LeaguePlayActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(
							android.R.layout.simple_list_item_1, parent, false);
				}
				TextView nameView = (TextView) rowView
						.findViewById(android.R.id.text1);

				nameView.setText(league.name());
				return rowView;
			}
		};
		leagueList.setAdapter(leaguesAdapter);
		leagueList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				League l = leaguesAdapter.getItem(position);
				Intent intent = new Intent(LeaguePlayActivity.this,
						LeagueActivity.class);
				intent.putExtra(LeagueActivity.LEAGUE_ID, l.id());
				startActivity(intent);
			}
		});

		updateAllDisplay();
		drawerToggle = DrawerUtils.configureDrawer(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.league_play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.add_league) {
			Intent intent = new Intent(this, NewLeagueActivity.class);
			startActivityForResult(intent, ACTIVITY_NEW_LEAGUE);
			return true;
		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
		leagues = lobby.getLeagues();
		leaguesAdapter.notifyDataSetChanged();
		if (leagues.isEmpty()) {
			findViewById(R.id.leagueList).setVisibility(View.GONE);
			findViewById(R.id.noLeagues).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.leagueList).setVisibility(View.VISIBLE);
			findViewById(R.id.noLeagues).setVisibility(View.GONE);
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
}
