package com.jujiabao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jujiabao.heartrate.R;
import com.jujiabao.login.MainLogin;

public class DialogLoginTips extends Dialog {
	
	private static boolean isMainLogin;
	private static Context context;

	@SuppressWarnings("static-access")
	public DialogLoginTips(Context context) {
		
		super(context, R.style.dialog);//调用自定义的类型 R.style.dialog
		
		setCanceledOnTouchOutside(true);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		Window window = getWindow();
		WindowManager.LayoutParams p = window.getAttributes();
		window.setAttributes(p);
		
		this.context = context;
		isMainLogin = this.context instanceof MainLogin;
	}
	
	public static final class Builder implements android.view.View.OnClickListener,OnKeyListener{

		private Context mContext;
		private DialogLoginTips mAlertDialog;
		
		public Builder(Context context) {
			this.mContext = context;
		}

		private View createView() {
			View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_login_tips, null);
			inflate.findViewById(R.id.btn_dialog_note_confirm).setOnClickListener(this);
			return inflate;
		}

		public DialogLoginTips create() {
			mAlertDialog = new DialogLoginTips(mContext);
			mAlertDialog.setContentView(createView());
			mAlertDialog.setOnKeyListener(this);
			return mAlertDialog;
		}

		public DialogLoginTips show() {
			mAlertDialog = create();
			mAlertDialog.show();
			return mAlertDialog;
		}
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			return false;
		}

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
				case R.id.btn_dialog_note_confirm:
					mAlertDialog.cancel();
					break;
	
				default:
					break;
			}
		}
	}
}

