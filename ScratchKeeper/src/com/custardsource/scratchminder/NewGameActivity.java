package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewGameActivity extends Activity {

	Lobby lobby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		lobby = ((GlobalState) getApplication()).getLobby();

		((Button) findViewById(R.id.ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String description = ((EditText) findViewById(R.id.newGameName))
								.getText().toString();
						Game g = lobby.addGame(description);
						Intent intent = new Intent(NewGameActivity.this,
								ScoreboardActivity.class);
						intent.putExtra(ScoreboardActivity.GAME_ID, g.id());
						startActivity(intent);
					}
				});
	}
}
