package com.custardsource.scratchminder;

import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class LeagueRankingsFragment extends Fragment {
	private Lobby lobby;
	private View root;
	private League league;
	private List<Entry<Player, Double>> rankedPlayers;
	private ArrayAdapter<Entry<Player, Double>> rankingAdapter;
	private List<LeagueGame> recentGames;
	private ArrayAdapter<LeagueGame> recentGamesAdapter;
	private LeagueGameListener listener;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.listener = (LeagueGameListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement LeagueGameListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_league_play_rankings,
				container, false);

		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.lobby = ((GlobalState) getActivity().getApplication()).getLobby();

		this.league = lobby.leagueById(getActivity().getIntent().getLongExtra(
				LeagueActivity.LEAGUE_ID, 0));

		checkVisibility();
		rankedPlayers = league.playersByRank();
		rankingAdapter = new ArrayAdapter<Entry<Player, Double>>(getActivity(),
				android.R.layout.simple_list_item_1, rankedPlayers) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Player player = getItem(position).getKey();
				double ranking = getItem(position).getValue();
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
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
		((ListView) root.findViewById(R.id.leagueRankingsTable))
				.setAdapter(rankingAdapter);

		recentGames = league.recentGames(LeagueActivity.RECENT_GAME_COUNT);
		recentGamesAdapter = new ArrayAdapter<LeagueGame>(getActivity(),
				android.R.layout.simple_list_item_1, recentGames) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Player winner = getItem(position).winner();
				Player loser = getItem(position).loser();
				long timestamp = getItem(position).timestamp();
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
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
		ListView historyList = (ListView) root.findViewById(R.id.leagueRecentGamesTable);
		historyList
				.setAdapter(recentGamesAdapter);
		registerForContextMenu(historyList);
	}

	private void checkVisibility() {
		if (league.hasResults()) {
			root.findViewById(R.id.noRankingsLabel).setVisibility(View.GONE);
			root.findViewById(R.id.rankingsLayout).setVisibility(View.VISIBLE);
		} else {
			root.findViewById(R.id.noRankingsLabel).setVisibility(View.VISIBLE);
			root.findViewById(R.id.rankingsLayout).setVisibility(View.GONE);
		}
	}

	public void refreshData() {
		recentGames = league.recentGames(LeagueActivity.RECENT_GAME_COUNT);
		recentGamesAdapter.clear();
		recentGamesAdapter.addAll(recentGames);

		rankedPlayers = league.playersByRank();
		rankingAdapter.clear();
		rankingAdapter.addAll(rankedPlayers);
		checkVisibility();
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		if (v.getId() == R.id.leagueRecentGamesTable) {
			inflater.inflate(R.menu.league_games_history, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.record_same_result:
			LeagueGame g = recentGamesAdapter.getItem(info.position);
			recordResult(g.winner(), g.loser());
			return true;
		case R.id.record_opposite_result:
			LeagueGame g2 = recentGamesAdapter.getItem(info.position);
			recordResult(g2.loser(), g2.winner());
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void recordResult(Player winner, Player loser) {
		LeagueGame game = league.recordResult(winner, loser);
		recentGamesAdapter.insert(game, 0);
		listener.onGameAdded(game);
	}
}
