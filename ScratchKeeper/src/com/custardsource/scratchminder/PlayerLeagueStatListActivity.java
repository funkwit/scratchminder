package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An activity representing a list of Player League Stats. This activity has
 * different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a
 * {@link PlayerLeagueStatDetailActivity} representing item details. On tablets,
 * the activity presents the list of items and item details side-by-side using
 * two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PlayerLeagueStatListFragment} and the item details (if present) is a
 * {@link PlayerLeagueStatDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link PlayerLeagueStatListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class PlayerLeagueStatListActivity extends Activity implements
		PlayerLeagueStatListFragment.Callbacks {
	protected static final String PLAYER_ID = "PLAYER_ID";

	private static final int ACTION_EDIT = 1;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	private Player player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playerleaguestat_list);

		if (findViewById(R.id.playerleaguestat_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((PlayerLeagueStatListFragment) getFragmentManager()
					.findFragmentById(R.id.playerleaguestat_list))
					.setActivateOnItemClick(true);
		}

		final GlobalState globalState = (GlobalState) getApplication();
		this.player = globalState.getLobby().playerById(
				getIntent().getLongExtra(PLAYER_ID, -1));

		refreshDisplay();
	}

	private void refreshDisplay() {
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(player.getColor()));
		getActionBar().setIcon(player.getAvatar().drawable());
		getActionBar().setTitle(player.getName());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.player_stats, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.edit) {
			Intent intent = new Intent(this, AddPlayerActivity.class);
			intent.putExtra(AddPlayerActivity.PLAYER_ID, player.id());
			startActivityForResult(intent, ACTION_EDIT);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_EDIT && resultCode == RESULT_OK) {
			refreshDisplay();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	/**
	 * Callback method from {@link PlayerLeagueStatListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(long id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putLong(PlayerLeagueStatDetailFragment.PLAYER_ID, player.id());
			arguments.putLong(PlayerLeagueStatDetailFragment.LEAGUE_ID, id);
			PlayerLeagueStatDetailFragment fragment = new PlayerLeagueStatDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.playerleaguestat_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					PlayerLeagueStatDetailActivity.class);
			detailIntent.putExtra(PlayerLeagueStatDetailFragment.PLAYER_ID,
					player.id());
			detailIntent.putExtra(PlayerLeagueStatDetailFragment.LEAGUE_ID,
				id);
			startActivity(detailIntent);
		}
	}
}
