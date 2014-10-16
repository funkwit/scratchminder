package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewLeagueActivity extends Activity {
	Lobby lobby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_league);
		lobby = ((GlobalState) getApplication()).getLobby();

		((Button) findViewById(R.id.ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String description = ((EditText) findViewById(R.id.newLeagueName))
								.getText().toString();
						League l = lobby.addLeague(description);
						Intent intent = new Intent(NewLeagueActivity.this,
								LeagueActivity.class);
						intent.putExtra(LeagueActivity.LEAGUE_ID, l.id());
						startActivity(intent);
					}
				});
	}
}
