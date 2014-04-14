package com.marakana.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class StatusActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState == null) {
        	StatusFragment fragment = new StatusFragment();
        	getFragmentManager()
        		.beginTransaction()
        		.add(android.R.id.content, fragment,
        				fragment.getClass().getSimpleName())
        	.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		
		default:
			return false;	
		}
	}

}
