package com.custardsource.scratchkeeper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LobbyActivity extends Activity {

	private List<Game> games = new ArrayList<Game>();
	private SharedPreferences sharedPref;
	private ArrayAdapter<Game> gamesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);

		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		Player p1 = new Player("Cow", R.drawable.remember_the_milk, Color.rgb(
				0, 0, 80));
		Player p2 = new Player("Chris", R.drawable.pterodactyl, Color.rgb(80,
				0, 0));
		Player p3 = new Player("Krijesta", R.drawable.dino_orange, Color.rgb(
				80, 0, 80));
		Player p4 = new Player("Bod", R.drawable.caveman, Color.rgb(80, 80, 0));

		Game g1 = new Game();
		Game g2 = new Game();
		g2.setDescription("Awesome");
		g1.addPlayer(p1);
		g1.addPlayer(p2);
		g1.addPlayer(p3);
		g1.leave(p2);
		g2.addPlayer(p1);
		g2.addPlayer(p4);
		games.add(g1);
		games.add(g2);

		final ListView gamesList = (ListView) findViewById(R.id.lobbyGames);
		gamesAdapter = new ArrayAdapter<Game>(this,
				android.R.layout.simple_list_item_2, games) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Game game = getItem(position);
				LayoutInflater inflater = (LayoutInflater) LobbyActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(R.layout.game_list_entry,
						parent, false);
				TextView nameView = (TextView) rowView
						.findViewById(R.id.gameName);
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
	}
}
