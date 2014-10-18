package com.custardsource.scratchminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class RegisterPlayerBadgeActivity extends Activity {

	static final String BADGE_CODE = "BADGE_CODE";
	private boolean swipeInProgress = false;
	private StringBuilder code = new StringBuilder();
	private TextView statusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_player_badge);
		statusView = (TextView) findViewById(R.id.swipeInstructionsStatus);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int pressed = event.getUnicodeChar();
		if (pressed == Constants.BADGE_START) {
			swipeInProgress = true;
			statusView.setText(R.string.status_in_progress);
			code = new StringBuilder();
			return true;
		} else if (swipeInProgress) {
			if (pressed == Constants.BADGE_END) {
				swipeInProgress = false;
				if (code.length() == 0) {
					statusView.setText(R.string.status_error);
				} else {
					Intent intent = new Intent("foo");
					intent.putExtra(BADGE_CODE, code.toString());
					setResult(RESULT_OK, intent);
					finish();
				}
				return true;
			} else if (pressed != 0) {
				code.appendCodePoint(pressed);
				return true;
			}
			return super.onKeyUp(keyCode, event);
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

}
