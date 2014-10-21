package com.custardsource.scratchminder;

import android.view.KeyEvent;

public class BadgeSwipeWatcher {
	public interface BadgeSwipeListener {
		public void onBadgeSwipe(String badgeCode);
	}
	private final BadgeSwipeListener listener;
	private boolean swipeInProgress;
	private StringBuilder code;
	
	public BadgeSwipeWatcher(BadgeSwipeListener listener) {
		this.listener = listener;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		int pressed = event.getUnicodeChar();
		if (pressed == Constants.BADGE_START) {
			swipeInProgress = true;
			code = new StringBuilder();
			return true;
		} else if (swipeInProgress) {
			if (pressed == Constants.BADGE_END) {
				swipeInProgress = false;
				if (code.length() != 0) {
					listener.onBadgeSwipe(code.toString());
				}
				return true;
			} else if (pressed != 0) {
				code.appendCodePoint(pressed);
				return true;
			}
			return false;
		}
		swipeInProgress = false;
		return false;
	}

}
