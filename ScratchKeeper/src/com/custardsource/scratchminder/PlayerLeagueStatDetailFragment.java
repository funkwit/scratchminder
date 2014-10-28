package com.custardsource.scratchminder;

import java.text.DecimalFormat;
import java.util.Collection;

import com.custardsource.scratchminder.League.HeadToHeadSummary;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A fragment representing a single Player League Stat detail screen. This
 * fragment is either contained in a {@link PlayerLeagueStatListActivity} in
 * two-pane mode (on tablets) or a {@link PlayerLeagueStatDetailActivity} on
 * handsets.
 */
public class PlayerLeagueStatDetailFragment extends Fragment {
	public static final String PLAYER_ID = "PLAYER_ID";
	public static final String LEAGUE_ID = "LEAGUE_ID";

	private League league;
	private Player player;
	private Lobby lobby;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PlayerLeagueStatDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lobby = ((GlobalState) getActivity().getApplication()).getLobby();

		player = lobby.playerById(getArguments().getLong(PLAYER_ID));
		league = lobby.leagueById(getArguments().getLong(LEAGUE_ID));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_playerleaguestat_detail, container, false);

		TableLayout table = (TableLayout) rootView
				.findViewById(R.id.headToHeadTable);
		Collection<HeadToHeadSummary> summaries = league
				.summaryForPlayer(player);
		final DecimalFormat decimalFormat = new DecimalFormat("#.00%");
		int id = 0;

		for (HeadToHeadSummary summary : summaries) {
			TableRow row = new TableRow(getActivity());
			row.setId(id++);

			TextView text = new TextView(getActivity());
			text.setText(summary.otherPlayer().getName());
			row.addView(text);

			text = new TextView(getActivity());
			text.setText(Integer.toString(summary.wins() + summary.losses()));
			row.addView(text);

			text = new TextView(getActivity());
			text.setText(Integer.toString(summary.wins()));
			row.addView(text);

			text = new TextView(getActivity());
			text.setText(Integer.toString(summary.losses()));
			row.addView(text);

			text = new TextView(getActivity());
			text.setText(String.format("%+d", summary.wins() - summary.losses()));
			row.addView(text);

			text = new TextView(getActivity());
			text.setText(decimalFormat.format(((float) summary.wins())
					/ (summary.wins() + summary.losses())));
			row.addView(text);

			table.addView(row);
		}

		return rootView;
	}
}
