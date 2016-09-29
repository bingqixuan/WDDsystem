package esri.bing.gis_weather;



import java.io.UnsupportedEncodingException;

import cn.eoe.leigo.splash.R;








import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private TextView show = null;
	private Button btnGetWeather = null;
	String strlocation = "";
	String weather="";
    String air="";
    //LocationTHread LThread=null;
    GetWeather gw;
    GetPMInfo gi;
    //GetAir ga;
    Toast toast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_weather);
		
		gw=new GetWeather();
		gi=new GetPMInfo();
		//ga=new GetAir();
		show=(TextView)findViewById(R.id.show);
		btnGetWeather = (Button) MainActivity.this.findViewById(R.id.btnGetWeather);
		
		this.btnGetWeather.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetWorkToastView()){
					strlocation="天津市";
					 myHandler.sendEmptyMessage(0x123);
				}
			}
		});
	}
	/*
	class LocationTHread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            if(myLotion != null)
            while(!myLotion.getIsFinish()){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }//获取到经纬度后执行获取地方名称
            if(myLotion.myBDcoordinate != null){
            	myHandler.sendEmptyMessage(0x147);
                strlocation =  myLocation.getAddress(myLotion.getLatValue() +"", myLotion.getLongValue() + "");
                myHandler.sendEmptyMessage(0x123);
            }
                                                                     
        }
                                                                 
    }
    */
	class weatherThread extends Thread{
    	
    	public void run(){
    		while(strlocation.equals("")){
    			try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		try {
            	weather=gw.getWeather(new Configration()
            	.EcodingGB2312(strlocation.substring(0, strlocation.length()-1)));
            	myHandler.sendEmptyMessage(0x456);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
	class airThread extends Thread{
    	
    	public void run(){
    		while(weather.equals("")){
    			try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		try {
            	//air=ga.getPMInfoImpl(strlocation);
            	air=gi.getPMInfoImpl(strlocation.substring(0, strlocation.length()-1));
            	myHandler.sendEmptyMessage(0x258);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
	Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what==0x123){
            	show.setText("定位完成，地点---"+strlocation+" 获取天气中...");
            	new weatherThread().start();
            }else if(msg.what==0x456){
            	show.setText("天气获取完成，获取空气质量中...");
            	new airThread().start();
            }else if(msg.what==0x258){
            	show.setText("五大道天气：\n"+weather);
            }else{
            	Log.i("11111111111","Handle ERROR message!");
            }
        }
                                                                 
    };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	public boolean NetWorkToastView(){
    	boolean netWork=Configration.netWorkIsOPen(MainActivity.this);
    	if(toast!=null) toast.cancel();
    	if(netWork){
    		return true;
    	}else{
			toast=Toast.makeText(MainActivity.this, "网络未开启，请开启网络", Toast.LENGTH_SHORT);
    		toast.show();
    		return false;
    	}
    }

}
