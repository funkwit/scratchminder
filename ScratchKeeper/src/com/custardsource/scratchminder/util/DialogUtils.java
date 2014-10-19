package com.custardsource.scratchminder.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

	public static void confirmDialog(Context context, final Runnable runnable,
			int title, int message, Object... messageArgs) {
		confirmDialog(context, runnable, title, context.getResources()
				.getString(message, messageArgs));
	}

	public static void confirmDialog(Context context, final Runnable runnable,
			int title, String message) {
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
