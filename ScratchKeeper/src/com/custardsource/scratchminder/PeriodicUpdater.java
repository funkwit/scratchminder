package com.custardsource.scratchminder;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class PeriodicUpdater {

	private final Runnable runnable;
	private final Handler handler;
	private TimerTask timerTask;
	private int periodicity;

	private static final Timer REFRESH_TIMER = new Timer();

	public PeriodicUpdater(Runnable runnable, int periodicity) {
		this.runnable = runnable;
		handler = new Handler();
		this.periodicity = periodicity;
	}

	public void resume() {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(runnable);

			}
		};
		REFRESH_TIMER.schedule(timerTask, 0, periodicity);

	}

	public void pause() {
		timerTask.cancel();
	}
}
