package com.custardsource.scratchminder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class AddPlayerActivity extends Activity {
	static final String PLAYER_ID = "PlayerID";
	private static final int[] COLOUR_COMPONENTS = { 0x00, 0x2A, 0x54, 0x7F,
			0xAA };
	private static final List<Integer> VALID_COLOURS = Lists.newArrayList();
	protected static final int ACTIVITY_REGISTER_BADGE = 1;
	private static final int BADGE_SELECTED_COLOR = Color.rgb(120, 200, 120);
	static {
		for (int red : COLOUR_COMPONENTS) {
			for (int green : COLOUR_COMPONENTS) {
				for (int blue : COLOUR_COMPONENTS) {
					if (red != green || red != blue || green != blue) {
						// don't want grey
						VALID_COLOURS.add(Color.rgb(red, green, blue));
					}
				}
			}
		}
	}

	private Player editingPlayer;
	private GlobalState state;

	private int itemBackground;
	private Map<Integer, Integer> colourPopularity;
	private Map<Avatar, Integer> avatarPopularity;
	private String badgeCode;
	private ImageButton registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_player);
		state = (GlobalState) getApplication();

		TypedArray a = obtainStyledAttributes(R.styleable.GalleryTheme);
		itemBackground = a.getResourceId(
				R.styleable.GalleryTheme_android_galleryItemBackground, 0);
		a.recycle();

		if (getIntent().hasExtra(PLAYER_ID)) {
			editingPlayer = state.getLobby().playerById(
					getIntent().getLongExtra(PLAYER_ID, 0));
		}
		Set<Player> toExclude = Collections.emptySet();
		if (editingPlayer != null) {
			toExclude = Collections.singleton(editingPlayer);
			badgeCode = editingPlayer.getBadgeCode();
		}
		colourPopularity = state.getLobby().colourPopularityMap(toExclude);
		avatarPopularity = state.getLobby().avatarPopularityMap(toExclude);

		final ImageAdapter imageAdapter = new ImageAdapter();
		final GridView gridView = (GridView) findViewById(R.id.gridPicture);
		gridView.setAdapter(imageAdapter);
		gridView.setSelector(itemBackground);
		gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridView.setSelection(0);
		gridView.setItemChecked(0, true);

		final GridView colours = (GridView) findViewById(R.id.gridColour);
		final ColourAdapter colourAdapter = new ColourAdapter();
		colours.setAdapter(colourAdapter);
		colours.setSelector(itemBackground);
		colours.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		colours.setSelection(0);
		colours.setItemChecked(0, true);

		((Button) findViewById(R.id.cancel))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent result = new Intent("foo");
						setResult(Activity.RESULT_CANCELED, result);
						finish();
					}
				});
		((Button) findViewById(R.id.ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent result = new Intent("foo");
						Player p = editingPlayer;
						if (p == null) {
							p = state.getLobby().addPlayer(
									((EditText) findViewById(R.id.editName))
											.getText().toString(),
									imageAdapter.getItem(gridView
											.getCheckedItemPosition()),
									colourAdapter.getItem(colours
											.getCheckedItemPosition()));
						} else {
							p.setName(((EditText) findViewById(R.id.editName))
									.getText().toString());
							p.setAvatar(imageAdapter.getItem(gridView
									.getCheckedItemPosition()));
							p.setColour(colourAdapter.getItem(colours
									.getCheckedItemPosition()));
						}
						String ttsName = ((EditText) findViewById(R.id.editTtsName))
								.getText().toString();
						p.setTtsName(ttsName);
						state.getLobby().registerBadgeCode(badgeCode, p);

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
		registerButton = (ImageButton) findViewById(R.id.registerBadgeButton);
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddPlayerActivity.this,
						RegisterPlayerBadgeActivity.class);
				startActivityForResult(intent, ACTIVITY_REGISTER_BADGE);
			}
		});
		if (editingPlayer != null) {
			((EditText) findViewById(R.id.editName)).setText(editingPlayer
					.getName());
			((EditText) findViewById(R.id.editTtsName)).setText(editingPlayer
					.getTtsName());
			int index = imageAdapter.indexOf(editingPlayer.getAvatar());
			gridView.setSelection(index);
			gridView.setItemChecked(index, true);

			index = colourAdapter.indexOf(editingPlayer.getColor());
			colours.setSelection(index);
			colours.setItemChecked(index, true);
			if(!Strings.isNullOrEmpty(editingPlayer.getBadgeCode())) {
				registerButton.setBackgroundColor(BADGE_SELECTED_COLOR);
			}
		}
	}

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
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public abstract static class CheckableImageListAdapter<T> extends
			BaseAdapter {
		private final List<T> validChoices;
		private Context context;

		protected CheckableImageListAdapter(Context context,
				List<T> validChoices) {
			this.validChoices = validChoices;
			this.context = context;
		}

		@Override
		public int getCount() {
			return validChoices.size();
		}

		@Override
		public T getItem(int position) {
			return validChoices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getIdForItem(getItem(position));
		}

		protected abstract long getIdForItem(T item);

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.image_with_count, parent, false);
			}
			T current = getItem(position);
			populateView(v, current);
			return v;
		}

		public int indexOf(T item) {
			return validChoices.indexOf(item);
		}

		protected abstract void populateView(View v, T current);
	}

	public class ImageAdapter extends CheckableImageListAdapter<Avatar> {

		protected ImageAdapter() {
			super(AddPlayerActivity.this, Arrays.asList(Avatar.values()));
		}

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

			Integer popularity = avatarPopularity.get(current);
			if (popularity == null) {
				t.setVisibility(View.INVISIBLE);
			} else {
				t.setVisibility(View.VISIBLE);
				t.setText(Integer.toString(popularity));
			}
		}
	}

	public class ColourAdapter extends CheckableImageListAdapter<Integer> {
		// TODO - consistent spelling of colour
		// tODO - consistent terminology with players enabled/disabled

		protected ColourAdapter() {
			super(AddPlayerActivity.this, VALID_COLOURS);
		}

		@Override
		protected long getIdForItem(Integer item) {
			return item;
		}

		@Override
		protected void populateView(View v, Integer current) {
			ImageView i = (ImageView) v.findViewById(R.id.image);
			TextView t = (TextView) v.findViewById(R.id.counter);
			float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					40, getResources().getDisplayMetrics());
			int padding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
							.getDisplayMetrics());

			i.setLayoutParams(new RelativeLayout.LayoutParams((int) size,
					(int) size));
			i.setPadding(padding, padding, padding, padding);
			Drawable color = new ColorDrawable(current);
			i.setImageDrawable(color);

			Integer popularity = colourPopularity.get(current);
			if (popularity == null) {
				t.setVisibility(View.INVISIBLE);
			} else {
				t.setVisibility(View.VISIBLE);
				t.setText(Integer.toString(popularity));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_REGISTER_BADGE && resultCode == RESULT_OK) {
			badgeCode = data
					.getStringExtra(RegisterPlayerBadgeActivity.BADGE_CODE);
			registerButton.setBackgroundColor(BADGE_SELECTED_COLOR);
			
			Toast.makeText(this, R.string.registered_badge, Toast.LENGTH_SHORT)
					.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
