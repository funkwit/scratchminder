package com.custardsource.scratchminder;

import java.util.List;
import java.util.Map.Entry;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LeagueActivity extends Activity {

	private Lobby lobby;
	private League league;
	protected static final String LEAGUE_ID = "LEAGUE_ID";
	private static final int ACTIVITY_RECORD_GAME = 1;
	private static final int RECENT_GAME_COUNT = 3;
	private List<Entry<Player, Double>> rankedPlayers;
	private ArrayAdapter<Entry<Player, Double>> rankingAdapter;
	private List<LeagueGame> recentGames;
	private ArrayAdapter<LeagueGame> recentGamesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_league);
		lobby = ((GlobalState) getApplication()).getLobby();

		this.league = lobby.leagueById(getIntent().getLongExtra(LEAGUE_ID, 0));
		setTitle(league.name());
		
		checkVisibility();
		rankedPlayers = league.playersByRank();
		rankingAdapter = new ArrayAdapter<Entry<Player, Double>>(this,
				android.R.layout.simple_list_item_1, rankedPlayers) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Player player = getItem(position).getKey();
				double ranking = getItem(position).getValue();
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) LeagueActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(
							R.layout.league_ranking_list_entry, parent, false);
				}
				TextView nameView = (TextView) rowView
						.findViewById(R.id.rankingEntryName);
				TextView rankingView = (TextView) rowView
						.findViewById(R.id.rankingEntryRanking);
				TextView positionView = (TextView) rowView
						.findViewById(R.id.rankingEntryPosition);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.rankingEntryIcon);
				imageView.setImageResource(player.getDrawable());
				nameView.setText(player.getName());
				positionView.setText(Integer.toString(position + 1));
				rankingView.setText(Integer.toString((int) ranking));
				rowView.setBackgroundColor(player.getColor());
				return rowView;
			}
		};
		((ListView) findViewById(R.id.leagueRankingsTable))
				.setAdapter(rankingAdapter);
		
		recentGames = league.recentGames(RECENT_GAME_COUNT);
		recentGamesAdapter = new ArrayAdapter<LeagueGame>(this,
				android.R.layout.simple_list_item_1, recentGames) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Player winner = getItem(position).winner();
				Player loser = getItem(position).loser();
				long timestamp = getItem(position).timestamp();
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) LeagueActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(
							R.layout.league_recent_game_entry, parent, false);
				}
				TextView winnerName = (TextView) rowView
						.findViewById(R.id.winnerName);
				ImageView winnerIcon = (ImageView) rowView
						.findViewById(R.id.winnerIcon);
				TextView loserName = (TextView) rowView
						.findViewById(R.id.loserName);
				ImageView loserIcon = (ImageView) rowView
						.findViewById(R.id.loserIcon);
				TextView dateView = (TextView) rowView
						.findViewById(R.id.recentGameDate);
				winnerIcon.setImageResource(winner.getDrawable());
				winnerName.setText(winner.getName());
				winnerIcon.setBackgroundColor(winner.getColor());
				winnerName.setBackgroundColor(winner.getColor());

				loserIcon.setImageResource(loser.getDrawable());
				loserName.setText(loser.getName());
				loserIcon.setBackgroundColor(loser.getColor());
				loserName.setBackgroundColor(loser.getColor());

				dateView.setText(DateUtils.getRelativeTimeSpanString(timestamp));
				return rowView;
			}
		};
		((ListView) findViewById(R.id.leagueRecentGamesTable))
				.setAdapter(recentGamesAdapter);


	}

	private void checkVisibility() {
		if (league.hasResults()) {
			findViewById(R.id.noRankingsLabel).setVisibility(View.GONE);
			findViewById(R.id.rankingsLayout).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.noRankingsLabel).setVisibility(View.VISIBLE);
			findViewById(R.id.rankingsLayout).setVisibility(View.GONE);
		}
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
				Player winner = lobby.playerById(data.getLongExtra(
						RecordGameActivity.WINNER_ID, 0));
				Player loser = lobby.playerById(data.getLongExtra(
						RecordGameActivity.LOSER_ID, 0));
				league.recordResult(winner, loser);
				winner.recordPlay();
				loser.recordPlay();
				checkVisibility();
				setUpRankingsData();
				recentGames = league.recentGames(RECENT_GAME_COUNT);
				recentGamesAdapter.clear();
				recentGamesAdapter.addAll(recentGames);
			}
		}
	}

	private void setUpRankingsData() {
		rankingAdapter.clear();
		rankingAdapter.addAll(league.playersByRank());
	}
}
