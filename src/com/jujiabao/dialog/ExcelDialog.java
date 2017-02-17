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
import com.jujiabao.main.History;

public class ExcelDialog extends Dialog {
	public ExcelDialog(Context context) {
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
		private ExcelDialog mAlertDialog;
		
		public Builder(Context context) {
			this.mContext = context;
		}

		@SuppressLint("InflateParams")
		private View createView() {
			View inflate = LayoutInflater.from(mContext).inflate(R.layout.excel_success, null);
			final TextView content = (TextView) inflate.findViewById(R.id.show_excel_content);
			content.setText(History.isSuccess 
					? content.getResources().getString(R.string.excel_success)
							: content.getResources().getString(R.string.excel_failed));
			inflate.findViewById(R.id.btn_dialog_excel_confirm).setOnClickListener(this);
			return inflate;
		}

		public ExcelDialog create() {
			mAlertDialog = new ExcelDialog(mContext);
			mAlertDialog.setContentView(createView());
			mAlertDialog.setOnKeyListener(this);
			return mAlertDialog;
		}

		public ExcelDialog show() {
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
				case R.id.btn_dialog_excel_confirm:
					mAlertDialog.cancel();
					break;
	
				default:
					break;
			}
		}
	}
}

