package com.custardsource.scratchminder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LeagueGraphFragment extends Fragment {
	private Lobby lobby;
	private View root;
	private League league;
	private LineChartView chart;
	private Axis axisY;
	private ArrayAdapter<Player> legendAdapter;
	private boolean useTrueSkill;

	public LeagueGraphFragment(boolean useTrueSkill) {
		super();
		this.useTrueSkill = useTrueSkill;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_league_play_graph, container,
				false);

		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		this.lobby = ((GlobalState) getActivity().getApplication()).getLobby();

		this.league = lobby.leagueById(getActivity().getIntent().getLongExtra(
				LeagueActivity.LEAGUE_ID, 0));

		legendAdapter = new ArrayAdapter<Player>(getActivity(),
				android.R.layout.simple_list_item_1, new ArrayList<Player>()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inflater.inflate(
							android.R.layout.simple_list_item_1, parent, false);
				}
				TextView playerName = (TextView) rowView
						.findViewById(android.R.id.text1);
				playerName.setText(getItem(position).getName());

				ColorDrawable colour = new ColorDrawable(getItem(position)
						.getColor());
				// TODO: scale size not per-pixel
				colour.setBounds(new Rect(0, 0, 30, 30));
				playerName.setCompoundDrawables(colour, null, null, null);

				return rowView;
			}
		};
		ListView legend = (ListView) root.findViewById(R.id.chartLegend);
		legend.setAdapter(legendAdapter);

		this.chart = (LineChartView) root.findViewById(R.id.chart);
		axisY = new Axis().setHasLines(true);
		chart.setInteractive(true);
		chart.setValueSelectionEnabled(true);
		chart.setZoomType(ZoomType.HORIZONTAL);
		chart.setOnValueTouchListener(new LineChartView.LineChartOnValueTouchListener() {
			@Override
			public void onValueTouched(int selectedLine, int selectedValue,
					PointValue value) {
				int a = selectedLine;
				int b = selectedValue;
				PointValue v = value;
				Log.i("Touch", "Ouch!");
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingTouched() {
				// TODO Auto-generated method stub

			}
		});
		refreshData();
	}

	public void refreshData() {
		Map<Player, List<PointValue>> graphData = Maps.newHashMap();
		int point = 0;
		Iterable<Map<Player, Double>> ratingsOverTime = useTrueSkill ? league
				.trueSkillConservativeRatingsOverTime() : league
				.eloRatingsOverTime();

		for (Map<Player, Double> m : ratingsOverTime) {
			for (Map.Entry<Player, Double> entry : m.entrySet()) {
				Player player = entry.getKey();
				Double score = entry.getValue();
				if (!graphData.containsKey(player)) {
					graphData.put(player, Lists.<PointValue> newArrayList());
				}
				List<PointValue> dataForPlayer = graphData.get(player);
				dataForPlayer.add(new PointValue(point, score.floatValue()));
			}
			point++;
		}

		List<Line> lines = new ArrayList<Line>();
		for (Map.Entry<Player, List<PointValue>> data : graphData.entrySet()) {
			Line line = new Line(data.getValue())
					.setColor(data.getKey().getColor()).setCubic(true)
					.setHasPoints(true).setPointRadius(2);
			line.setHasLabelsOnlyForSelected(true);
			lines.add(line);
		}

		LineChartData data = new LineChartData();
		data.setLines(lines);
		data.setAxisYLeft(axisY);
		chart.setLineChartData(data);
		final Viewport v = new Viewport(chart.getMaxViewport());
		v.inset(0, -10);
		chart.setMaxViewport(v);
		chart.setCurrentViewport(v, false);

		List<Player> players = Lists.newArrayList(graphData.keySet());
		legendAdapter.clear();
		// TODO: sort
		legendAdapter.addAll(players);
	}

	public void setUseTrueSkill(boolean useTrueSkill) {
		this.useTrueSkill = useTrueSkill;
		refreshData();
	}
}