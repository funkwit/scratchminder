package com.custardsource.scratchkeeper;

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
import android.widget.ListAdapter;

@SuppressWarnings("deprecation")
public class AddPlayerActivity extends Activity {

	static final String PLAYER_DATA = "Player";

	private Player editingPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: replace gallery.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_player);
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
							p = new Player(
									((EditText) findViewById(R.id.editName))
											.getText().toString(),

									imageAdapter.getItem(g
											.getSelectedItemPosition()),
									colourAdapter.getItem(colours
											.getSelectedItemPosition()));
						} else {
							p.setName(((EditText) findViewById(R.id.editName))
									.getText().toString());
							p.setDrawable(imageAdapter.getItem(g
									.getSelectedItemPosition()));
							p.setColour(colourAdapter.getItem(colours
									.getSelectedItemPosition()));

						}
						result.putExtra(PLAYER_DATA, p);
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
		if (getIntent().hasExtra(PLAYER_DATA)) {
			// It's an edit
			Player p = (Player) getIntent().getSerializableExtra(PLAYER_DATA);
			((EditText) findViewById(R.id.editName)).setText(p.getName());
			g.setSelection(getIndexFromAdapter(imageAdapter, p.getDrawable()));
			colours.setSelection(getIndexFromAdapter(colourAdapter,
					p.getColor()));
			editingPlayer = p;
		}
	}

	private int getIndexFromAdapter(BaseAdapter adapter, long id) {
		for (int position = 0; position < adapter.getCount(); position++)
			if (adapter.getItemId(position) == id)
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
			return mImageIds.length;
		}

		@Override
		public Integer getItem(int position) {
			return mImageIds[position];
		}

		public long getItemId(int position) {
			return mImageIds[position];
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = (ImageView) convertView;
			if (convertView == null) {
				i = new ImageView(AddPlayerActivity.this);
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				i.setBackgroundResource(mGalleryItemBackground);
			}
			i.setImageResource(mImageIds[position]);
			return i;
		}

		private Integer[] mImageIds = { R.drawable.caveman,
				R.drawable.dino_blue, R.drawable.dino_green,
				R.drawable.dino_orange, R.drawable.pterodactyl,
				R.drawable.remember_the_milk, R.drawable.user_iconshock_afro,
				R.drawable.user_iconshock_alien,
				R.drawable.user_iconshock_anciano,
				R.drawable.user_iconshock_artista,
				R.drawable.user_iconshock_astronauta,
				R.drawable.user_iconshock_barbaman,
				R.drawable.user_iconshock_bombero,
				R.drawable.user_iconshock_boxeador,
				R.drawable.user_iconshock_bruce_lee,
				R.drawable.user_iconshock_caradebolsa,
				R.drawable.user_iconshock_chavo,
				R.drawable.user_iconshock_cientifica,
				R.drawable.user_iconshock_cientifico_loco,
				R.drawable.user_iconshock_comisario,
				R.drawable.user_iconshock_cupido,
				R.drawable.user_iconshock_diabla,
				R.drawable.user_iconshock_director,
				R.drawable.user_iconshock_dreds,
				R.drawable.user_iconshock_elsanto,
				R.drawable.user_iconshock_elvis, R.drawable.user_iconshock_emo,
				R.drawable.user_iconshock_escafandra,
				R.drawable.user_iconshock_estilista,
				R.drawable.user_iconshock_extraterrestre,
				R.drawable.user_iconshock_fisicoculturista,
				R.drawable.user_iconshock_funky,
				R.drawable.user_iconshock_futbolista_brasilero,
				R.drawable.user_iconshock_gay,
				R.drawable.user_iconshock_geisha,
				R.drawable.user_iconshock_ghostbuster,
				R.drawable.user_iconshock_glamrock_singer,
				R.drawable.user_iconshock_guerrero_chino,
				R.drawable.user_iconshock_hiphopper,
				R.drawable.user_iconshock_hombre_hippie,
				R.drawable.user_iconshock_hotdog_man,
				R.drawable.user_iconshock_indio,
				R.drawable.user_iconshock_joker,
				R.drawable.user_iconshock_karateka,
				R.drawable.user_iconshock_mago,
				R.drawable.user_iconshock_maori,
				R.drawable.user_iconshock_mario_barakus,
				R.drawable.user_iconshock_mascara_antigua,
				R.drawable.user_iconshock_metalero,
				R.drawable.user_iconshock_meteoro,
				R.drawable.user_iconshock_michelin,
				R.drawable.user_iconshock_mimo,
				R.drawable.user_iconshock_mister,
				R.drawable.user_iconshock_mounstrico1,
				R.drawable.user_iconshock_mounstrico2,
				R.drawable.user_iconshock_mounstrico3,
				R.drawable.user_iconshock_mounstrico4,
				R.drawable.user_iconshock_mounstruo,
				R.drawable.user_iconshock_muerte,
				R.drawable.user_iconshock_mujer_hippie,
				R.drawable.user_iconshock_mujer_latina,
				R.drawable.user_iconshock_muneco_lego,
				R.drawable.user_iconshock_nena_afro };
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
