package com.custardsource.scratchminder.util;

import android.R;
import android.R.drawable;
import android.R.string;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogUtils {

	public static void confirmDialog(Context context, final Runnable runnable,
			int title, int message) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(message)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								runnable.run();
	
							}
						}).setNegativeButton(android.R.string.no, null).show();
	}

}
