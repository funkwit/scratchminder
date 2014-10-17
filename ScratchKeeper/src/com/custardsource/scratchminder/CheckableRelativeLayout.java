package com.custardsource.scratchminder;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements
		Checkable {
	private boolean checked = false;

	public CheckableRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableRelativeLayout(Context context) {
		super(context);
	}

	@Override
	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (this.checked == checked)
			return;
		this.checked = checked;
		if (this.checked) {
			setBackgroundResource(android.R.color.darker_gray);
		} else {
			setBackgroundResource(0);
		}
		refreshDrawableState();
	}

	@Override
	public void toggle() {
		setChecked(!checked);
	}

}