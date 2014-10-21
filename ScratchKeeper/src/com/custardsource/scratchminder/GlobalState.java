package com.custardsource.scratchminder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Context;
import android.util.Log;

import com.google.common.io.ByteStreams;

public class GlobalState extends Application {
	static final String STATE_FILE = "savedstate";

	private Lobby lobby;

	private Timer timer;

	private static final long SAVE_PERIODICITY = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
	
	public GlobalState() {
		super();
		this.timer = new Timer();
		Log.d(Constants.TAG, "constructor of GlobalState");

	}

	Lobby getLobby() {
		return this.lobby;
	}

	void restoreGameState() {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					openFileInput(STATE_FILE));

			lobby = (Lobby) inputStream.readObject();
			inputStream.close();
		} catch (FileNotFoundException e) {
			// OK; new install, no data.
			lobby = new Lobby();
		} catch (Exception e) {
			try {
				ObjectInputStream inputStream = new ObjectInputStream(
						openFileInput(STATE_FILE + ".bak"));

				lobby = (Lobby) inputStream.readObject();
				inputStream.close();
			} catch (FileNotFoundException e2) {
				// OK; new install, no data.
				lobby = new Lobby();
			} catch (Exception e2) {
				lobby = new Lobby();
			}
		}
		lobby.resetIfNecessary();
		doBackup();
		Log.d(Constants.TAG, "restoreGameState of GlobalState");
	}

	private void doBackup() {
		try {
			InputStream inputStream = 
					openFileInput(STATE_FILE);
			OutputStream outputStream = 
					openFileOutput(STATE_FILE + ".bak", Context.MODE_PRIVATE);
			ByteStreams.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// OK; new install, no data.
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void flush() {
		saveGameState();
		Log.d(Constants.TAG, "Flushed game data to disk");
	}

	private void saveGameState() {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					openFileOutput(STATE_FILE, Context.MODE_PRIVATE));
			outputStream.writeObject(lobby);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new BackupManager(this).dataChanged();
	}

	@Override
	public void onCreate() {
		Log.d(Constants.TAG, "onCreate() of GlobalState");
		super.onCreate();
		restoreGameState();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				flush();
			}
		}, SAVE_PERIODICITY, SAVE_PERIODICITY);
	}

	@Override
	public void onTerminate() {
		saveGameState();
		super.onTerminate();
	}

}
