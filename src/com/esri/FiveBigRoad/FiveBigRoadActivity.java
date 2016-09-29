package com.esri.FiveBigRoad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Point;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.FeatureSet;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import com.esri.core.tasks.SpatialRelationship;
import com.example.android_camera_upload.camera_MainActivity;

import esri.bing.gis_weather.*;
import esri.bing.lixian.*;
import gis01.Gis01Activity;
//import gis01.Gis01Activity;
import cn.eoe.leigo.splash.*;

public class FiveBigRoadActivity extends Activity {

	//protected static final double SEARCH_RADIUS = 0;
	MapView mMapView;
	private Button btnZoomIn = null;
	private Button btnZoomOut = null;
	private Button btnConvert = null;
	private TextView label = null;
	GraphicsLayer gLayer;
	private Button btnGoLixian;
	@SuppressWarnings("unused")
	private Button btnCamera ;
	private Button btnGotoWheather;
	@SuppressWarnings("unused")
	private Button btnGogis01 ;
	/** Called when the activity is first created. */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

		this.mMapView = new MapView(this);
		mMapView.addLayer(new GraphicsLayer());
		mMapView = (MapView)findViewById(R.id.map);
		this.btnZoomIn = (Button) findViewById(R.id.btnZoomIn);
		this.btnZoomOut = (Button) findViewById(R.id.btnZoomOut);
		this.btnConvert = (Button) findViewById(R.id.btnConvert);
		this.btnGogis01=(Button) findViewById(R.id.btnGogis01);
	    this.btnGotoWheather =(Button) findViewById(R.id.btnGotoWeather);
		this.btnCamera=(Button) findViewById(R.id.btnCamera);
		this.btnGoLixian = (Button) findViewById(R.id.btnGoLixian);
		this.label=(TextView)findViewById(R.id.label);
		
		
		String urlImage4326=(String) this.getResources().getText(R.string.urlImage1);
		String urlStandard=(String) this.getResources().getText(R.string.urlStandard);
		String urlVec4326=(String) this.getResources().getText(R.string.urlVec4326);
		ArcGISDynamicMapServiceLayer tileLayer = new
				ArcGISDynamicMapServiceLayer(urlImage4326);
		this.mMapView.addLayer(tileLayer);
		ArcGISDynamicMapServiceLayer tileLayer2 = new
				ArcGISDynamicMapServiceLayer(urlStandard);
		this.mMapView.addLayer(tileLayer2);
		ArcGISDynamicMapServiceLayer tileLayer3 = new
				ArcGISDynamicMapServiceLayer(urlVec4326);
		this.mMapView.addLayer(tileLayer3);
		
		/*
		ArcGISFeatureLayer featureLayer=new 
				ArcGISFeatureLayer("http://10.5.230.250:6080/arcgis/rest/services/feature/MapServer", MODE.ONDEMAND);
		mMapView.addLayer(featureLayer);
		*/
	 btnGotoWheather.setOnClickListener(listener);
	 btnGogis01.setOnClickListener(listener2);	
	btnCamera.setOnClickListener(listener3);
	btnGoLixian.setOnClickListener(listener4);
		this.btnZoomIn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FiveBigRoadActivity.this.mMapView.zoomin();
			}
			});
		this.btnZoomOut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FiveBigRoadActivity.this.mMapView.zoomout();
			}
			});	
		this.btnConvert.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mMapView.isLoaded()){
					Layer[] layers= mMapView.getLayers();
					if(layers[1].isVisible()){
						layers[1].setVisible(false);
					}
					else{
						layers[1].setVisible(true);
					}
					
				}
			}
			});	
		this.mMapView.setOnLongPressListener(new OnLongPressListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void onLongPress(float x, float y) {
				com.esri.core.geometry.Point pt = FiveBigRoadActivity.this.mMapView.toMapPoint(x, y);
				FiveBigRoadActivity.this.label.setText("X:"+pt.getX() + " Y:" + pt.getY());
			}
			});
		this.mMapView.setOnSingleTapListener(new OnSingleTapListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			//点击地图后自动执行的方法
			public void onSingleTap(float x, float y) {
			// TODO Auto-generated method stub
				
			}
			});
		/*
		String targetServerURL = "http://10.5.230.250:6080/arcgis/rest/services/wudadao/MapServer";
		String targetLayer = targetServerURL.concat("/3");//服务图层
		String[] queryParams = { targetLayer, "AVGHHSZ_CY>3.5" };
		AsyncQueryTask ayncQuery = new AsyncQueryTask();
		ayncQuery.execute(queryParams);
		*/

		/*
		LocationService ls = mMapView.getLocationService();//通过map对象获取定位服务
		ls.setAutoPan(false);//设置不自动平移
		ls.setLocationListener(new LocationListener() {//设置定位监听器
		boolean locationChanged = false;
		//当坐标改变时触发该方法
		public void onLocationChanged(Location loc) {
		if (!locationChanged) {
		locationChanged = true;
		double locy = loc.getLatitude();
		double locx = loc.getLongitude();
		com.esri.core.geometry.Point wgspoint = new com.esri.core.geometry.Point(locx, locy);
		com.esri.core.geometry.Point mapPoint = (com.esri.core.geometry.Point) GeometryEngine
		.project(wgspoint,
		SpatialReference.create(4326),
		mMapView.getSpatialReference());
		Unit mapUnit = mMapView.getSpatialReference()
		.getUnit();
		double zoomWidth = Unit.convertUnits(
		SEARCH_RADIUS,
		Unit.create(LinearUnit.Code.MILE_US),
		mapUnit);
		Envelope zoomExtent = new Envelope(mapPoint,
		zoomWidth, zoomWidth);
		mMapView.setExtent(zoomExtent);
		}
		}
		public void onProviderDisabled(String arg0) { }
		public void onProviderEnabled(String arg0) { }
		public void onStatusChanged(String arg0, int arg1,
		Bundle arg2) { }
		});
		ls.start();
		*/
    }

	protected void btnGotoWheather() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.unpause();
	}

	  private Button.OnClickListener listener = new Button.OnClickListener(){
  		public void onClick(View v){
  			Intent intent = new Intent();
  			intent.setClass(FiveBigRoadActivity.this, MainActivity.class);
  			
  			startActivity(intent);
  		}
      };
      private Button.OnClickListener listener2 = new Button.OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent();
    			intent.setClass(FiveBigRoadActivity.this, Gis01Activity.class);
    			
    			startActivity(intent);
    		}
        };
        private Button.OnClickListener listener3 = new Button.OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent();
    			intent.setClass(FiveBigRoadActivity.this, camera_MainActivity.class);
    			
    			startActivity(intent);
    		}
        };
        private Button.OnClickListener listener4 = new Button.OnClickListener(){
    		public void onClick(View v){
    			Intent intent = new Intent();
    			intent.setClass(FiveBigRoadActivity.this, GIS_LixianActivity.class);
    			
    			startActivity(intent);
    		}
        };
}