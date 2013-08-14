package com.mt.gox.cn;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mt.gox.cn.actionbar.ActionBarActivity;
import com.mt.gox.cn.utils.Global;

/**
 * @ClassName: BaseActivity 
 * @Description:  activity
 * @author LEIKANG 
 */
public class BaseActivity extends ActionBarActivity{

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater menuInflater = getMenuInflater();
	        menuInflater.inflate(R.menu.main, menu);
	        return super.onCreateOptionsMenu(menu);
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	                Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
	                break;

	            case R.id.menu_refresh:
	                getActionBarHelper().setRefreshActionItemState(true);
	                getWindow().getDecorView().postDelayed(
	                        new Runnable() {
	                            @Override
	                            public void run() {
	                    			Intent intent = new Intent(Global.ACTION_REFRESH_BTN);
	                    			sendBroadcast(intent);
	                            }
	                        }, 1000);
	                break;
	                
	            case R.id.menu_share:
	                Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
	                break;
	        }
	        return super.onOptionsItemSelected(item);
	    }
}
