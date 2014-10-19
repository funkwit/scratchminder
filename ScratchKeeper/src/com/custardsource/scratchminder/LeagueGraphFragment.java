package com.custardsource.scratchminder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LeagueGraphFragment extends Fragment {
	private Lobby lobby;
	private View root;
	private League league;
	private LineChartView chart;

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

		this.chart = (LineChartView) root.findViewById(R.id.chart);
		refreshData();
	}

	public void refreshData() {
		Map<Player, List<PointValue>> graphData = Maps.newHashMap();
		int point = 0;
		for (Map<Player, Double> m: league.rankingsOverTime()) {
			for (Map.Entry<Player, Double> entry : m.entrySet()) {
				Player player = entry.getKey();
				Double score = entry.getValue();
				if (!graphData.containsKey(player)) {
					graphData.put(player, Lists.<PointValue>newArrayList());
				}
				List<PointValue> dataForPlayer = graphData.get(player);
				dataForPlayer.add(new PointValue(point, score.floatValue()));
			}
			point++;
		}

	    List<Line> lines = new ArrayList<Line>();
	    for (Map.Entry<Player, List<PointValue>> data : graphData.entrySet()) {
		    Line line = new Line(data.getValue()).setColor(data.getKey().getColor()).setCubic(true);
		    lines.add(line);
	    }

	    LineChartData data = new LineChartData();
	    data.setLines(lines);
	    chart.setLineChartData(data);
	}

	public void periodicRefresh() {
		refreshData();
	}
}