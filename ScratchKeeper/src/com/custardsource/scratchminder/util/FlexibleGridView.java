package com.custardsource.scratchminder.util;

import com.custardsource.scratchminder.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.GridView;

public class FlexibleGridView extends GridView {
	int maxHeight = -1;
	
    public FlexibleGridView(Context context) {
        super(context);
    }
 
    public FlexibleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxHeight(context,attrs);
    }
 
    public FlexibleGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMaxHeight(context, attrs);
    }

	private void setMaxHeight(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlexibleGridView);
		try {
			maxHeight = a.getDimensionPixelSize(
					R.styleable.FlexibleGridView_android_maxHeight, -1);
		} finally {
			a.recycle();
		}
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
 
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
        	int max = maxHeight == -1 ? Integer.MAX_VALUE >> 2 : maxHeight;
            heightSpec = MeasureSpec.makeMeasureSpec(
                    max, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }
 
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}