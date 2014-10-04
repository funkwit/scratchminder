package com.custardsource.scratchminder;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Application;
import android.content.Context;
import android.view.View;

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
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					openFileInput(STATE_FILE));
			lobby = (Lobby) inputStream.readObject();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			lobby = new Lobby();
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
