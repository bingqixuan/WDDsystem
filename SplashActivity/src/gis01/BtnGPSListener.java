package gis01;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;

public class BtnGPSListener implements OnClickListener{
	private LocationManager locationManger;
	private LocationService locationService = null;
	
	private MapView map;
	private GraphicsLayer layer;
	private Context context;
	private ArrayList<Point> points=null;//记录全部点

	private SimpleLineSymbol lineSymbol;  
	private SimpleMarkerSymbol markerSymbol; 
	public  BtnGPSListener(Context xcontext,MapView view,GraphicsLayer Layer)
	{
		context=xcontext;
		map=view;
		layer=Layer;
	}
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		
		locationService = map.getLocationService();
		locationService.setLocationListener(new MyLocationListener());
		Toast.makeText(map.getContext(), "开始定位……", Toast.LENGTH_SHORT).show();
		locationService.start();
	}
	private void initial()
	{
        markerSymbol=new SimpleMarkerSymbol(Color.RED,4,STYLE.CIRCLE);
        lineSymbol=new SimpleLineSymbol(Color.RED,2); 
		points=new ArrayList<Point>();
        layer.setVisible(true);
	}
	class MyLocationListener implements LocationListener
	{

		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			initial();
			//Point ptLatLon = new Point(arg0.getLongitude(), arg0 .getLatitude()); 
			double dLong = arg0.getLongitude();
			double dLat = arg0.getLatitude();
			Point ptMap = (Point)GeometryEngine.project(dLong, dLat, map.getSpatialReference());
			points.add(ptMap);
			Graphic graphic = new Graphic(ptMap,markerSymbol);  
	        layer.addGraphic(graphic); 
			map.centerAt(ptMap,false);
			map.zoomTo(ptMap, 8);
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(map.getContext(), "定位功能已禁用！", Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(map.getContext(), "定位功能已启用！", Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}

}