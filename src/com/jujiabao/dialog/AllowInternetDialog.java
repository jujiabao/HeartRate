package com.jujiabao.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jujiabao.heartrate.R;

public class AllowInternetDialog extends Dialog {
	public AllowInternetDialog(Context context) {
		super(context, R.style.dialog);//调用自定义的类型 R.style.dialog
		setCanceledOnTouchOutside(false);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("unused")
		Display d = wm.getDefaultDisplay();
		Window window = getWindow();
		WindowManager.LayoutParams p = window.getAttributes();
		window.setAttributes(p);
	}
	
	public static final class Builder implements android.view.View.OnClickListener,OnKeyListener{

		private Context mContext;
		private AllowInternetDialog mAlertDialog;
		
		public Builder(Context context) {
			this.mContext = context;
		}

		@SuppressLint("InflateParams")
		private View createView() {
			View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_allow_internet, null);
			final TextView content = (TextView) inflate.findViewById(R.id.show_allow_internet_content);
			content.setText("此功能暂未开通，请联系开发者开通！");
			inflate.findViewById(R.id.btn_dialog_allinternet_confirm).setOnClickListener(this);
			return inflate;
		}

		public AllowInternetDialog create() {
			mAlertDialog = new AllowInternetDialog(mContext);
			mAlertDialog.setContentView(createView());
			mAlertDialog.setOnKeyListener(this);
			return mAlertDialog;
		}

		public AllowInternetDialog show() {
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
				case R.id.btn_dialog_allinternet_confirm:
					mAlertDialog.cancel();
					break;
	
				default:
					break;
			}
		}
	}
}

