package com.custardsource.scratchminder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;

import android.app.Application;
import android.content.Context;

public class GlobalState extends Application {
	private static final String STATE_FILE = "savedstate";

	private Lobby lobby;

	public GlobalState() {
		super();
	}

	Lobby getLobby() {
		return this.lobby;
	}

	private void restoreGameState() {
		doBackup();
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					openFileInput(STATE_FILE));

			lobby = (Lobby) inputStream.readObject();
			inputStream.close();
			lobby.resetIfNecessary();
		} catch (FileNotFoundException e) {
			// OK; new install, no data.
			lobby = new Lobby();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	void flush() {
		saveGameState();
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
	}

	@Override
	public void onCreate() {
		super.onCreate();
		restoreGameState();
	}

	@Override
	public void onTerminate() {
		saveGameState();
		super.onTerminate();
	}

}
