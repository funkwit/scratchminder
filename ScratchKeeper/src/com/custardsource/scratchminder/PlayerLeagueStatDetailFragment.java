package com.custardsource.scratchminder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jskills.Rating;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.custardsource.scratchminder.League.HeadToHeadSummary;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

		Iterable<Map<Player, Rating>> ratingsOverTime = league
				.trueSkillRatingsOverTime();
		List<PointValue> conservative = Lists.newArrayList();
		List<PointValue> uncertainty = Lists.newArrayList();
		List<PointValue> rankingData = Lists.newArrayList();
		int point = 0;

		for (Map<Player, Rating> ratings : ratingsOverTime) {
			if (ratings.containsKey(player)) {
				Rating r = ratings.get(player);
				conservative.add(new PointValue(point, (float) r
						.getConservativeRating()));
				uncertainty.add(new PointValue(point, (float) (r.getMean() + r
						.getConservativeStandardDeviationMultiplier()
						* r.getStandardDeviation())));

				rankingData.add(new PointValue(point, league
						.playerRankingsByConservativeTrueSkillRating(ratings)
						.indexOf(player) + 1));
			}
			point++;
		}

		List<Line> lines = new ArrayList<Line>();
		lines.add(new Line(conservative).setColor(player.getColor())
				.setCubic(true).setHasPoints(true).setPointRadius(2)
				.setHasLabelsOnlyForSelected(true));
		lines.add(new Line(uncertainty).setColor(player.getColor())
				.setCubic(true).setHasPoints(true).setPointRadius(2)
				.setHasLabelsOnlyForSelected(true));
		LineChartData data = new LineChartData();
		data.setLines(lines);
		Axis axisY = new Axis().setHasLines(true);
		data.setAxisYLeft(axisY);
		LineChartView chart = (LineChartView) rootView
				.findViewById(R.id.scoreOverTimeChart);
		chart.setLineChartData(data);

		final Viewport v = new Viewport(chart.getMaxViewport());
		v.inset(0, -10);
		chart.setMaxViewport(v);
		chart.setCurrentViewport(v, false);

		List<Line> lines2 = Lists.newArrayList();
		lines2.add(new Line(rankingData).setColor(player.getColor())
				.setHasPoints(true).setPointRadius(2)
				.setHasLabels(true));
		LineChartData data2 = new LineChartData();
		data2.setLines(lines2);
		Axis axisY2 = new Axis().setHasLines(true);
		data2.setAxisYLeft(axisY2);
		LineChartView chart2 = (LineChartView) rootView
				.findViewById(R.id.rankingOverTimeChart);
		chart2.setLineChartData(data2);

		return rootView;
	}
}
