package gis01;


import gis02.Gis02Activity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


import cn.eoe.leigo.splash.R;
import android.view.MotionEvent;
import com.esri.FiveBigRoad.FiveBigRoadActivity;
import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import com.example.android_camera_upload.camera_MainActivity;

public class Gis01Activity extends Activity {
	
	private MapView mMapView;
	private GraphicsLayer drawLayer=null;
	private GraphicsLayer gpsLayer=null;
	private GraphicsLayer queryLayer=null;
	
	private Spinner spinnerDrawType;
	private Button buttonZoomIn=null;
	private Button buttonZoomOut=null;
	private Button btnMLength=null;
	private Button btnMArea=null;
	private Button btnClear=null;
	private Button btnGPS=null;
	private Button btnQuery=null;
	private Button btnPlotCorrection=null;
	private Button btnPointCorrection=null;
	private MapTouchListener mapTouchListener=null;
    private BtnGPSListener btnGPSListener=null;
    
    private Button btnGogis02;
	//private String URL="http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer";
    private String URL="http://10.0.2.11:6080/arcgis/rest/services/Z102100/MapServer";
	private String URL2="http://10.0.2.11:6080/arcgis/rest/services/Z102100/MapServer/6";
	private Geometry.Type geoType = null;//用于判定当前选择的几何图形类型  
	ProgressDialog progress;
	Drawable drawable;
	private Symbol symbol=null;
	private Symbol symbol2=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gis01_main);
       
		ArcGISDynamicMapServiceLayer worldLayer = new ArcGISDynamicMapServiceLayer(URL);
//		ArcGISDynamicMapServiceLayer worldLayer = new ArcGISDynamicMapServiceLayer("http://10.5.230.4:6080/arcgis/rest/services/Image/MapServer");
//		ArcGISDynamicMapServiceLayer worldLayer = new ArcGISDynamicMapServiceLayer("http://10.5.230.4:6080/arcgis/rest/services/Image/MapServer");
//		ArcGISDynamicMapServiceLayer worldLayer = new ArcGISDynamicMapServiceLayer("http://10.5.230.4:6080/arcgis/rest/services/Image/MapServer");

		
		mMapView = (MapView)findViewById(R.id.map);
	    
		//mMapView.addLayer(Road);
		drawLayer=new GraphicsLayer();
		gpsLayer=new GraphicsLayer();
		queryLayer=new GraphicsLayer();
		gpsLayer.setVisible(false);
		mMapView.addLayer(worldLayer);
		mMapView.addLayer(drawLayer);
		mMapView.addLayer(gpsLayer);
		mMapView.addLayer(queryLayer);
	
		buttonZoomIn=(Button)findViewById(R.id.buttonZoomIn);
		buttonZoomOut=(Button)findViewById(R.id.buttonZoomOut);
		btnMLength=(Button)findViewById(R.id.btnMlength);
		btnMArea=(Button)findViewById(R.id.btnMArea);
		btnClear=(Button)findViewById(R.id.btnClear);
		btnGPS=(Button)findViewById(R.id.btnGPS);
		btnQuery=(Button)findViewById(R.id.btnQuery);	
		this.initSelections();
		this.btnGogis02 =(Button) findViewById(R.id.btnGogis02);
		
		buttonZoomIn.setOnClickListener(new ZoomInButtonListener());
		buttonZoomOut.setOnClickListener(new ZoomOutButtonListener());
		btnMLength.setOnClickListener(new btnMLengthListener());
		btnMArea.setOnClickListener(new btnMAreaListener());	
		btnClear.setOnClickListener(new btnClearListener());
		btnGPSListener=new BtnGPSListener(Gis01Activity.this,mMapView,gpsLayer);
		btnGPS.setOnClickListener(btnGPSListener);
		btnQuery.setOnClickListener(new btnQueryListener());
		btnGogis02.setOnClickListener(Listener1);
		//btnPlotCorrection.setOnClickListener(new btnPlotCorrectionListener());
		//btnPointCorrection.setOnClickListener(new btnPointCorrectionListener());

		//绑定触摸事件监听器
		mapTouchListener=new MapTouchListener(Gis01Activity.this,mMapView);
		mapTouchListener.getLayer(drawLayer);
        mMapView.setOnTouchListener(mapTouchListener);
        drawable=this.getResources().getDrawable(R.drawable.loc);
        
    }  
    class ZoomInButtonListener implements OnClickListener{

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mMapView.zoomin();
		}}
    class ZoomOutButtonListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			mMapView.zoomout();
		}}
    class btnMLengthListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			geoType = Geometry.Type.POLYLINE; 
			mapTouchListener.getDrawType(geoType);
			mapTouchListener.setMeasureStatus(true,false);
			//mapTouchListener.setCorrectStatus(false,false);
		}}
    class btnMAreaListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			geoType = Geometry.Type.POLYGON; 
			mapTouchListener.getDrawType(geoType);
			mapTouchListener.setMeasureStatus(false,true);
			//mapTouchListener.setCorrectStatus(false,false);
		}}
    class btnClearListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mapTouchListener.resultCallOut.isShowing()==true)
			{
				mapTouchListener.resultCallOut.hide();	
			}
			drawLayer.removeAll();
			queryLayer.removeAll();
			geoType=null;
			mapTouchListener.setEmpty();
			mapTouchListener.getDrawType(geoType);
			//mapTouchListener.setCorrectStatus(false,false);
		}}
    class btnQueryListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			queryLayer.removeAll();
			EditText cdTxt = (EditText)findViewById(R.id.condition);
     	    String condition = "name like '%"+cdTxt.getText()+"%'";
			new AsyncQueryAttributeTask().execute(URL2,condition);
		}
	}
  //从gis01跳到gis02  
    private Button.OnClickListener Listener1 = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(Gis01Activity.this, Gis02Activity.class);
			
			startActivity(intent);
		}
    };
    
   
    
    private class AsyncQueryAttributeTask extends AsyncTask<String, Void, FeatureSet> {

		
		protected void onPreExecute() {
			progress = ProgressDialog.show(Gis01Activity.this, "",
					"请等待....查询正在进行中.");

		}

		/**
		 * First member in parameter array is the query URL; second member is
		 * the where clause.
		 */
		protected FeatureSet doInBackground(String... queryParams) {
			if (queryParams == null || queryParams.length <= 1)
				return null;

			String url = queryParams[0];
			Query query = new Query();
			String whereClause = queryParams[1];
			query.setGeometry(Gis01Activity.this.mMapView.getExtent());
		
			query.setReturnGeometry(true);
			query.setWhere(whereClause);

			QueryTask qTask = new QueryTask(url);
			FeatureSet featureSet = null;

			try {
				featureSet = qTask.execute(query);
			} catch (Exception e) {
				e.printStackTrace();
				return featureSet;
			}
			return featureSet;

		}

		protected void onPostExecute(FeatureSet result) {

			String message = "No result comes back";
			if (result != null) {
				Graphic[] grs = result.getGraphics();
				Geometry.Type a=result.getGeometryType();
				//symbol2=new SimpleMarkerSymbol(Color.GREEN, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
					symbol=new PictureMarkerSymbol(drawable);
	               //SimpleFillSymbol symbol = new SimpleFillSymbol(Color.YELLOW);
	               Gis01Activity.this.queryLayer.setRenderer(new SimpleRenderer(symbol));
	               //Graphic re=new Graphic(a,symbol2);
	               //Graphic re2=new Graphic(a,symbol);
	               //Gis01Activity.this.queryLayer.removeAll();
				
				if (grs.length > 0) {
					Gis01Activity.this.queryLayer.addGraphics(grs);
					//Gis01Activity.this.queryLayer.addGraphic(re);
					//Gis01Activity.this.queryLayer.addGraphic(re2);
					Gis01Activity.this.mMapView.setExtent(Gis01Activity.this.queryLayer.getExtent());
					message = (grs.length == 1 ? "1 条符合条件记录 " : Integer
							.toString(grs.length) + "条符合条件记录")
							+ "返回";
				}

			}
			progress.dismiss();

			Toast toast = Toast.makeText(Gis01Activity.this, message,
					Toast.LENGTH_LONG);
			toast.show();
			

		}

	}
    /*
    class AsyncQueryTask extends AsyncTask<Float, Void, FeatureSet> {
        String errMsg = null;
        Point mapPoint;
        @Override
        protected FeatureSet doInBackground(Float... params) {
            float x = params[0];
            float y = params[1];
            if (mMapView.isLoaded()) {
                // AlertMsg("单击，屏幕像素坐标点：  x=%s,y=%s", x, y);
                // Point mapPoint = mMapView.toMapPoint(new Point(x, y));

            	mapPoint = mMapView.toMapPoint(x, y);
                Query query = new Query();
                query.setGeometry(mapPoint);
                query.setReturnGeometry(true);
                //query.setOutFields(new String[] {"*"});
                query.setOutFields(new String[] { "Tree_No", "Tree_Hole_", "Type_Eng","Height", "DBH" });
                QueryTask queryTask = new QueryTask("http://192.168.95.1:6080/arcgis/rest/services/Tree/MapServer/3");//这里参数是地图的服务地址，后面的4，标识了是其中某一个图层。
                try {
                    FeatureSet fs = queryTask.execute(query);
                    return fs;
                } catch (Exception e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                }
            }
            return null;
        }
    }
    */
    private void initSelections(){
		spinnerDrawType = (Spinner)findViewById(R.id.spinnerDrawType);
	    ArrayAdapter<CharSequence> adapterDrawType = ArrayAdapter.createFromResource(
	            this, R.array.drawtypes_array, android.R.layout.simple_spinner_item);
	    adapterDrawType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinnerDrawType.setAdapter(adapterDrawType);
	    spinnerDrawType.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
	        	String drawType = arg0.getItemAtPosition(arg2).toString();
	        	mapTouchListener.setType(drawType);
	        	mapTouchListener.setMeasureStatus(false,false);
	        	//mapTouchListener.setCorrectStatus(false,false);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
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
	@Override 	protected void onResume() {
		super.onResume(); 
		mMapView.unpause();
	}

}