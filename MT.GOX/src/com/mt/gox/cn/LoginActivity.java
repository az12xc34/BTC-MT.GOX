package com.mt.gox.cn;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mt.gox.cn.pattern.LockPatternUtils;
import com.mt.gox.cn.pattern.LockPatternView;
import com.mt.gox.cn.pattern.LockPatternView.Cell;
import com.mt.gox.cn.pattern.LockPatternView.DisplayMode;
import com.mt.gox.cn.pattern.LockPatternView.OnPatternListener;

public class LoginActivity extends Activity{
	
	private LockPatternView lockPattern;
	private LockPatternUtils lockPatternUtils;
	
	private Context mContext;
	
	private String lockPatternString = "";
	
	private boolean isLock = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		lockPatternUtils = new LockPatternUtils(this);
		lockPatternString = lockPatternUtils.getLockPaternString();
		isLock = !lockPatternString.equals("");
		mContext = this;
		lockPattern = (LockPatternView)findViewById(R.id.lockpattern);
		lockPattern.setOnPatternListener(new OnPatternListener() {
			public void onPatternStart() { }
			public void onPatternDetected(List<Cell> pattern) {
				if(isLock){
					int result = lockPatternUtils.checkPattern(pattern);
					if (result!= 1) {
						if(result==0){
							lockPattern.setDisplayMode(DisplayMode.Wrong);
							Toast.makeText(mContext, "√‹¬Î¥ÌŒÛ", Toast.LENGTH_LONG)
							.show();
						}else{
							lockPattern.clearPattern();
							Toast.makeText(mContext, "«Î…Ë÷√√‹¬Î", Toast.LENGTH_LONG)
							.show();
						}
						
					} else {
						MTApplication.getInstance().isUnlock = true;
						mContext.startActivity(new Intent(mContext, MainActivity.class));
					}
				}else{
					lockPatternUtils.saveLockPattern(pattern);
					Toast.makeText(mContext, "√‹¬Î“—±£¥Ê", Toast.LENGTH_LONG)
					.show();
					lockPattern.clearPattern();
					MTApplication.getInstance().isUnlock = true;
					mContext.startActivity(new Intent(mContext, MainActivity.class));
				}
			
			}

			public void onPatternCleared() { }

			public void onPatternCellAdded(List<Cell> pattern) { }
		});
	}
	
}
