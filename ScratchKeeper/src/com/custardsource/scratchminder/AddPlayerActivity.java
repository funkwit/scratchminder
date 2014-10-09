package com.custardsource.scratchminder;

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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class AddPlayerActivity extends Activity {
	static final String PLAYER_ID = "PlayerID";

	private Player editingPlayer;
	private GlobalState state;

	private int itemBackground;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_player);
		state = (GlobalState) getApplication();

		TypedArray a = obtainStyledAttributes(R.styleable.GalleryTheme);
		itemBackground = a.getResourceId(
				R.styleable.GalleryTheme_android_galleryItemBackground, 0);
		a.recycle();

		
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
			int index = getIndexFromAdapter(imageAdapter, p.getAvatar());
			gridView.setSelection(index);
			gridView.setItemChecked(index, true);
			
			index = getIndexFromAdapter(colourAdapter,
					p.getColor());
			colours.setSelection(index);
			colours.setItemChecked(index, true);
			editingPlayer = p;
		}
	}

	private <T> int getIndexFromAdapter(BaseAdapter adapter, T object) {
		for (int position = 0; position < adapter.getCount(); position++)
			if (adapter.getItem(position).equals(object))
				return position;
		return 0;
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
		}
		return super.onOptionsItemSelected(item);
	}

	public class ImageAdapter extends BaseAdapter {
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
				i = new CheckableImageView(AddPlayerActivity.this);
				i.setAdjustViewBounds(true);
			}
			i.setImageResource(Avatar.values()[position].drawable());
			return i;
		}
	}

	public class ColourAdapter extends BaseAdapter {
		// TODO- refactor with above
		// TODO - consistent spelling of colour
		// tODO - consistent terminology with players enabled/disabled
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
			ImageView i = (ImageView) convertView;
			if (convertView == null) {
				i = new CheckableImageView(AddPlayerActivity.this);
				float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
				int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

				i.setLayoutParams(new GridView.LayoutParams((int) size, (int) size));
				i.setPadding(padding, padding, padding, padding);
			}
			Drawable color = new ColorDrawable(mColours[position]);
			i.setImageDrawable(color);
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
	
	public class CheckableImageView extends ImageView implements Checkable {
		private boolean checked = false;

		public CheckableImageView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		public CheckableImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public CheckableImageView(Context context) {
			super(context);
		}

		@Override
		public boolean isChecked() {
			return this.checked;
		}

		@Override
		public void setChecked(boolean checked) {
			if (this.checked == checked)
	            return;
	        this.checked = checked;
	        if (this.checked) {
	        	setBackgroundResource(android.R.color.darker_gray);
	        } else {
	        	setBackgroundResource(0);
	        }
	        refreshDrawableState();
		}

		@Override
		public void toggle() {
			setChecked(!checked);
		}

	}}
