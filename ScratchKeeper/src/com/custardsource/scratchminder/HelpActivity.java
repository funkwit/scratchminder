package com.custardsource.scratchminder;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		TextView text = ((TextView) findViewById(R.id.help_text));
		text.setText(Html
				.fromHtml(getString(R.string.help_text)));
		text.setMovementMethod(LinkMovementMethod.getInstance());
	}
}