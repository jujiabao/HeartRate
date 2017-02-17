package com.jujiabao.main;

import com.jujiabao.heartrate.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class AboutAuthor extends Activity implements OnTouchListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_author);
		
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.aboutauthorRelativeLayout);
		relativeLayout.setOnTouchListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		finish();
		overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
		}
		return super.onKeyDown(keyCode, event);
	}
}
