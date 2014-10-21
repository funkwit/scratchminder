package com.custardsource.scratchminder;

import com.custardsource.scratchminder.BadgeSwipeWatcher.BadgeSwipeListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class LeagueActivity extends FragmentActivity implements
		ActionBar.TabListener, LeagueGameListener, BadgeSwipeListener {

	private Lobby lobby;
	private League league;
	protected static final String LEAGUE_ID = "LEAGUE_ID";
	private static final int ACTIVITY_RECORD_GAME = 1;
	private static final int ACTIVITY_EDIT_LEAGUE = 2;
	static final int RECENT_GAME_COUNT = 3;
	private ViewPager viewPager;
	private ActionBar actionBar;
	private LeagueRankingsFragment rankingsFragment;
	private LeagueHistoryFragment historyFragment;
	private LeagueGraphFragment graphFragment;
	private PeriodicUpdater periodicUpdater;
	private BadgeSwipeWatcher swipeWatcher = new BadgeSwipeWatcher(this);
	private boolean useTrueSkill = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_league);
		this.lobby = ((GlobalState) getApplication()).getLobby();

		this.league = lobby.leagueById(getIntent().getLongExtra(
				LeagueActivity.LEAGUE_ID, 0));

		viewPager = (ViewPager) findViewById(R.id.leaguePager);
		actionBar = getActionBar();
		updateLeagueDetails();

		viewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {



			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public Fragment getItem(int index) {
				switch (index) {
				case 0:
					rankingsFragment = new LeagueRankingsFragment(useTrueSkill);
					return rankingsFragment;
				case 1:
					historyFragment = new LeagueHistoryFragment();
					return historyFragment;
				case 2:
					graphFragment = new LeagueGraphFragment(useTrueSkill);
					return graphFragment;
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

		Switch ratingsSwitch = (Switch) findViewById(R.id.ratingSystemSwitch);
		ratingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked != useTrueSkill) {
					if (rankingsFragment != null) {
						rankingsFragment.setUseTrueSkill(isChecked);
					}
					if (graphFragment != null) {
						graphFragment.setUseTrueSkill(isChecked);
					}
				}
				useTrueSkill = isChecked;
			}
		});
		if (!league.supportsElo()) {
			ratingsSwitch.setVisibility(View.GONE);
		}
		// Adding Tabs
		for (String tab_name : getResources().getStringArray(
				R.array.league_play_tabs)) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		
	   
		periodicUpdater = new PeriodicUpdater(new Runnable() {
			@Override
			public void run() {
				if (rankingsFragment != null) {
					rankingsFragment.periodicRefresh();
				}
				if (historyFragment != null) {
					historyFragment.periodicRefresh();
				}
			}
		}, 60000);
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
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onGameAdded(LeagueGame game) {
		if (rankingsFragment != null) {
			rankingsFragment.refreshData();
		}
		if (historyFragment != null) {
			historyFragment.refreshData();
		}
		if (graphFragment != null) {
			graphFragment.refreshData();
		}
	}

	@Override
	public void onGameDeleted(LeagueGame g) {
		if (rankingsFragment != null) {
			rankingsFragment.refreshData();
		}
		if (historyFragment != null) {
			historyFragment.refreshData();
		}
		if (graphFragment != null) {
			graphFragment.refreshData();
		}
	}

	@Override
	protected void onPause() {
		periodicUpdater.pause();
		super.onPause();
	}


	@Override
	protected void onResume() {
		super.onResume();
		periodicUpdater.resume();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (swipeWatcher.onKeyUp(keyCode, event)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void onBadgeSwipe(String badgeCode) {
		Intent intent = new Intent(this, RecordGameActivity.class);
		intent.putExtra(RecordGameActivity.INITIAL_BADGE_SWIPE, badgeCode);
		startActivityForResult(intent, ACTIVITY_RECORD_GAME);
	}
}
