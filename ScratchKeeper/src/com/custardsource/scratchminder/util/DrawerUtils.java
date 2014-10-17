package com.custardsource.scratchminder.util;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.custardsource.scratchminder.LeaguePlayActivity;
import com.custardsource.scratchminder.LobbyActivity;
import com.custardsource.scratchminder.PlayerChooserActivity;
import com.custardsource.scratchminder.R;
import com.google.common.collect.ImmutableList;

public class DrawerUtils {

	private static final List<Class<? extends Activity>> DESTINATIONS = ImmutableList
			.<Class<? extends Activity>> of(LobbyActivity.class,
					LeaguePlayActivity.class, PlayerChooserActivity.class);
	private static final int[] ICONS = { R.drawable.ic_fa_table,
			R.drawable.ic_fa_trophy, R.drawable.ic_fa_users };

	public static ActionBarDrawerToggle configureDrawer(final Activity activity) {

		final DrawerLayout drawerLayout = (DrawerLayout) activity
				.findViewById(R.id.lobby_drawer);
		final ListView drawerList = (ListView) activity
				.findViewById(R.id.left_drawer_navigation);
		// Set the adapter for the list view
		drawerList.setAdapter(new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_1, activity.getResources()
						.getStringArray(R.array.drawer_items)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView text = (TextView) v.findViewById(android.R.id.text1);
				text.setCompoundDrawablesWithIntrinsicBounds(ICONS[position],
						0, 0, 0);
				return v;
			}
		});
		// Set the list's click listener
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectLeftDrawerItem(position);
			}

			private void selectLeftDrawerItem(int position) {
				drawerList.setItemChecked(position, true);
				// TODO: call setTitle();
				drawerLayout.closeDrawers();
				Class<? extends Activity> destination = DESTINATIONS
						.get(position);
				if (!destination.equals(activity.getClass())) {
					Intent intent = new Intent(activity, destination);
					if (destination.equals(PlayerChooserActivity.class)) {
						intent.putExtra(PlayerChooserActivity.ACTION,
								PlayerChooserActivity.BROWSE);
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					activity.startActivity(intent);
				}
			}
		});
		final CharSequence title = activity.getTitle();
		final CharSequence drawerTitle = title;
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
				activity, drawerLayout, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				activity.getActionBar().setTitle(title);
				activity.invalidateOptionsMenu();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				activity.getActionBar().setTitle(drawerTitle);
				activity.invalidateOptionsMenu();
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(drawerToggle);
		drawerList.setItemChecked(0, true);
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getActionBar().setHomeButtonEnabled(true);
		return drawerToggle;
	}
}
