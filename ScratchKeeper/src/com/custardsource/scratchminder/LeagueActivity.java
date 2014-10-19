package com.custardsource.scratchminder;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class LeagueActivity extends FragmentActivity implements
		ActionBar.TabListener, LeagueHistoryFragment.LeagueGameListener {

	private Lobby lobby;
	private League league;
	protected static final String LEAGUE_ID = "LEAGUE_ID";
	private static final int ACTIVITY_RECORD_GAME = 1;
	private static final int ACTIVITY_EDIT_LEAGUE = 2;
	static final int RECENT_GAME_COUNT = 3;
	private ViewPager viewPager;
	private ActionBar actionBar;
	private LeagueRankingsFragment rankingsFragment;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_league);
		this.lobby = ((GlobalState)getApplication()).getLobby();

		this.league = lobby.leagueById(getIntent().getLongExtra(
				LeagueActivity.LEAGUE_ID, 0));

		viewPager = (ViewPager) findViewById(R.id.leaguePager);
		actionBar = getActionBar();
		updateLeagueDetails();
		viewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Fragment getItem(int index) {
				switch (index) {
				case 0:
					rankingsFragment = new LeagueRankingsFragment();
					return rankingsFragment;
				case 1:
					return new LeagueHistoryFragment();
				}

				return null;
			}
		});
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		    @Override
		    public void onPageSelected(int position) {
		        actionBar.setSelectedNavigationItem(position);
		    }
		 
		    @Override
		    public void onPageScrolled(int arg0, float arg1, int arg2) {
		    }
		 
		    @Override
		    public void onPageScrollStateChanged(int arg0) {
		    }
		});
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : getResources().getStringArray(
				R.array.league_play_tabs)) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.league, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.record_game) {
			Intent intent = new Intent(this, RecordGameActivity.class);
			startActivityForResult(intent, ACTIVITY_RECORD_GAME);
			return true;
		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.edit_league) {
			Intent intent = new Intent(this, NewLeagueActivity.class);
			intent.putExtra(NewLeagueActivity.LEAGUE_ID, league.id());
			startActivityForResult(intent, ACTIVITY_EDIT_LEAGUE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_RECORD_GAME) {
			if (resultCode == RESULT_OK) {
				Player winner = lobby.playerById(data.getLongExtra(
						RecordGameActivity.WINNER_ID, 0));
				Player loser = lobby.playerById(data.getLongExtra(
						RecordGameActivity.LOSER_ID, 0));
				league.recordResult(winner, loser);
				winner.recordPlay();
				loser.recordPlay();
			}
		} else if (requestCode == ACTIVITY_EDIT_LEAGUE) {
			if (resultCode == RESULT_OK) {
				updateLeagueDetails();
			}
		}
	}

	private void updateLeagueDetails() {
		setTitle(league.name());
		actionBar.setIcon(league.getDrawable());
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onGameAdded(LeagueGame game) {
		if (rankingsFragment != null) {
			rankingsFragment.refreshData();
		}
	}
}
