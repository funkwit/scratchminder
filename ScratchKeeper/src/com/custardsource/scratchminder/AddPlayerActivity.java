package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class AddPlayerActivity extends Activity {

	static final String PLAYER_ID = "PlayerID";

	private Player editingPlayer;
	private GlobalState state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: replace gallery.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_player);
		state = (GlobalState) getApplication();
		final Gallery g = (Gallery) findViewById(R.id.galleryPicture);
		final ImageAdapter imageAdapter = new ImageAdapter();
		g.setAdapter(imageAdapter);
		final Gallery colours = (Gallery) findViewById(R.id.galleryColour);
		final ColourAdapter colourAdapter = new ColourAdapter();
		colours.setAdapter(colourAdapter);
		((Button) findViewById(R.id.cancel))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO - string?
						Intent result = new Intent("foo");
						setResult(Activity.RESULT_CANCELED, result);
						finish();

					}
				});
		((Button) findViewById(R.id.ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO - string?
						Intent result = new Intent("foo");
						Player p = editingPlayer;
						if (p == null) {
							p = state.getLobby().addPlayer(
									((EditText) findViewById(R.id.editName))
											.getText().toString(),

									imageAdapter.getItem(g
											.getSelectedItemPosition()),
									colourAdapter.getItem(colours
											.getSelectedItemPosition()));
						} else {
							p.setName(((EditText) findViewById(R.id.editName))
									.getText().toString());
							p.setAvatar(imageAdapter.getItem(g
									.getSelectedItemPosition()));
							p.setColour(colourAdapter.getItem(colours
									.getSelectedItemPosition()));

						}
						result.putExtra(PLAYER_ID, p.id());
						setResult(Activity.RESULT_OK, result);
						finish();
					}
				});

		((EditText) findViewById(R.id.editName))
				.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						((Button) findViewById(R.id.ok)).setEnabled(s.length() > 0);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
		if (getIntent().hasExtra(PLAYER_ID)) {
			// It's an edit
			Player p = state.getLobby().playerById(getIntent().getLongExtra(PLAYER_ID, 0));
			((EditText) findViewById(R.id.editName)).setText(p.getName());
			g.setSelection(getIndexFromAdapter(imageAdapter, p.getAvatar()));
			colours.setSelection(getIndexFromAdapter(colourAdapter,
					p.getColor()));
			editingPlayer = p;
		}
	}

	private <T> int getIndexFromAdapter(BaseAdapter adapter, T object) {
		for (int position = 0; position < adapter.getCount(); position++)
			if (adapter.getItem(position) == object)
				return position;
		return 0;
	}

	// TODO: recent players
	// TODO: proper image picker
	// TODO: delete player completely

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ImageAdapter extends BaseAdapter {
		private int mGalleryItemBackground;

		public ImageAdapter() {
			TypedArray a = obtainStyledAttributes(R.styleable.GalleryTheme);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			a.recycle();

		}

		public int getCount() {
			return Avatar.values().length;
		}

		@Override
		public Avatar getItem(int position) {
			return Avatar.values()[position];
		}

		public long getItemId(int position) {
			return Avatar.values()[position].ordinal();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = (ImageView) convertView;
			if (convertView == null) {
				i = new ImageView(AddPlayerActivity.this);
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				i.setBackgroundResource(mGalleryItemBackground);
			}
			i.setImageResource(Avatar.values()[position].drawable());
			return i;
		}
	}

	public class ColourAdapter extends BaseAdapter {
		private int mGalleryItemBackground;

		// TODO- refactor with above
		// TODO - consistent spelling of colour
		// tODO - consistent terminology with players enabled/disabled
		public ColourAdapter() {
			TypedArray a = obtainStyledAttributes(R.styleable.GalleryTheme);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			a.recycle();

		}

		public int getCount() {
			return mColours.length;
		}

		@Override
		public Integer getItem(int position) {
			return mColours[position];
		}

		public long getItemId(int position) {
			return mColours[position];
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(AddPlayerActivity.this);
			Drawable color = new ColorDrawable(mColours[position]);
			i.setImageDrawable(color);
			i.setBackgroundResource(mGalleryItemBackground);

			i.setLayoutParams(new Gallery.LayoutParams(136, 136));
			return i;
		}

		// TODO: add recent
		private Integer[] mColours = { Color.rgb(127, 0, 0),
				Color.rgb(0, 127, 0), Color.rgb(0, 0, 127),
				Color.rgb(127, 127, 0), Color.rgb(0, 127, 127),
				Color.rgb(127, 0, 127), Color.rgb(64, 127, 0),
				Color.rgb(127, 64, 0), Color.rgb(0, 127, 64),
				Color.rgb(0, 64, 127), Color.rgb(64, 0, 127),
				Color.rgb(127, 0, 64) };

	}
}
