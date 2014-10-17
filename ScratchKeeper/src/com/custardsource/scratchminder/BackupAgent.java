package com.custardsource.scratchminder;

import java.io.File;
import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class BackupAgent extends BackupAgentHelper {
	static final String FILES_BACKUP_KEY = "myfiles";
	static final String PREFS_BACKUP_KEY = "myprefs";

	public BackupAgent() {
		super();
		Log.d(Constants.TAG, "Constructing agent");
	}

	@Override
	public void onCreate() {
		Log.d(Constants.TAG, "onCreate of agent");
		debugFiles();

		FileBackupHelper fileHelper = new FileBackupHelper(this,
				GlobalState.STATE_FILE);
		addHelper(FILES_BACKUP_KEY, fileHelper);
		SharedPreferencesBackupHelper prefsHelper = new SharedPreferencesBackupHelper(
				this, this.getPackageName() + "_preferences");
		addHelper(PREFS_BACKUP_KEY, prefsHelper);
	}

	private void debugFiles() {
		for (File f : getFilesDir().listFiles()) {
			if (f.isFile()) {
				Log.d(Constants.TAG, "    " + f.getAbsolutePath());
			}
		}
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {
		Log.d(Constants.TAG, "onRestore of agent");
		debugFiles();
		super.onRestore(data, appVersionCode, newState);
		Log.d(Constants.TAG, "done super.onRestore");
		debugFiles();
		((GlobalState) getApplicationContext()).restoreGameState();
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {
		Log.d(Constants.TAG, "onBackup of agent");
		super.onBackup(oldState, data, newState);
		Log.i(Constants.TAG, "done onBackup of parent");
	}



}
