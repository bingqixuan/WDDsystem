package com.esri.FiveBigRoad;
import cn.eoe.leigo.splash.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Welcome extends Activity {
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_01);
        
        Button btnEnter =(Button)findViewById(R.id.btnEnter);
        
        btnEnter.setOnClickListener(listener);
	}
        
        private Button.OnClickListener listener = new Button.OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent();
    			intent.setClass(Welcome.this, FiveBigRoadActivity.class);
    			
    			startActivity(intent);
    		}
        };	
}