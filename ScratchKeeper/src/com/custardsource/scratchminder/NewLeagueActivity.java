package com.custardsource.scratchminder;

import java.util.Arrays;

import com.custardsource.scratchminder.AddPlayerActivity.CheckableImageListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class NewLeagueActivity extends Activity {
	Lobby lobby;
	private int itemBackground;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_league);
		lobby = ((GlobalState) getApplication()).getLobby();
		
		TypedArray a = obtainStyledAttributes(R.styleable.GalleryTheme);
		itemBackground = a.getResourceId(
				R.styleable.GalleryTheme_android_galleryItemBackground, 0);
		a.recycle();

		final CheckableImageListAdapter<Avatar> imageAdapter = new CheckableImageListAdapter<Avatar>(
				this, Arrays.asList(Avatar.values())) {
			@Override
			protected long getIdForItem(Avatar item) {
				return item.ordinal();
			}

			@Override
			protected void populateView(View v, Avatar current) {
				ImageView i = (ImageView) v.findViewById(R.id.image);
				i.setAdjustViewBounds(true);
				TextView t = (TextView) v.findViewById(R.id.counter);

				i.setImageResource(current.drawable());
				t.setVisibility(View.INVISIBLE);
			}
		};
		final GridView gridView = (GridView) findViewById(R.id.gridPicture);
		gridView.setAdapter(imageAdapter);
		gridView.setSelector(itemBackground);
		gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridView.setSelection(0);
		gridView.setItemChecked(0, true);

		
		((Button) findViewById(R.id.ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String description = ((EditText) findViewById(R.id.newLeagueName))
								.getText().toString();
						League l = lobby.addLeague(description);
						l.setAvatar(imageAdapter.getItem(gridView
								.getCheckedItemPosition()));
						Intent intent = new Intent(NewLeagueActivity.this,
								LeagueActivity.class);
						intent.putExtra(LeagueActivity.LEAGUE_ID, l.id());
						startActivity(intent);
					}
				});
	}
}
