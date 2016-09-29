package esri.bing.lixian;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


import cn.eoe.leigo.splash.R;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.ags.na.NAFeaturesAsFeature;
import com.esri.core.tasks.ags.na.RoutingParameters;
import com.esri.core.tasks.ags.na.RoutingResult;
import com.esri.core.tasks.ags.na.RoutingTask;
import com.esri.core.tasks.ags.na.StopGraphic;

public class GIS_LixianActivity extends Activity {
	
	MapView map ;
	private Button btnClear;
	private GraphicsLayer graphicsLayer;
	//	设定绘制的类型
	private Geometry.Type drawType=null;
	private Symbol symbol=null;
	private SimpleFillSymbol fillSymbol=null;
	private Spinner spBuffer;
	private int number =-1;
	String[] Buffer =new String[]{"100","200","500","1000"};
	//SimpleLineSymbol hiderSym;//查询到的路径的片段，设置透明将其“隐藏”
	//private SimpleLineSymbol showSym;//当路径片段被选中的时候，显示的符号
	
	public static final LinearUnit LINEARUNIT_METER = (LinearUnit) Unit
			.create(LinearUnit.Code.METER);
	public int bfarea;
	
	//判断是否发生菜单选择事件
	private boolean isChoose;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lixian_main);

        map = (MapView)findViewById(R.id.map);
        btnClear=(Button)findViewById(R.id.btnClear);
        spBuffer=(Spinner)findViewById(R.id.spBuffer);
        //hiderSym=new SimpleLineSymbol(Color.WHITE, 1);
		//设置成透明，使其处于“隐藏”状态
		//hiderSym.setAlpha(100);
		//showSym=new SimpleLineSymbol(Color.RED, 4);
        //创建ArrayAdapter
        ArrayAdapter<String> adapterBuffer=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Buffer);
        adapterBuffer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBuffer.setAdapter(adapterBuffer);
        
        
		//加载离线切片地图
		ArcGISLocalTiledLayer localtitleLayer=new ArcGISLocalTiledLayer("file:///mnt/sdcard/Movies/18.tpk");
		map.addLayer(localtitleLayer);
		map.enableWrapAround(true);
		map.setEsriLogoVisible(true);
		//加载graphiclayer
		graphicsLayer=new GraphicsLayer();
		map.addLayer(graphicsLayer);
		map.setOnLongPressListener(new MyOnPressLis());
		//设定绘制的监听事件，让其能够绘制
		DrawGraphicTouchListener drawgraphictouchlistener=new DrawGraphicTouchListener(this,map);
		map.setOnTouchListener(drawgraphictouchlistener);
		this.btnClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				graphicsLayer.removeAll();
			}
		});
		
		spBuffer.setOnItemSelectedListener(spBufferListener);
		
	
    }
    private Spinner.OnItemSelectedListener spBufferListener = new Spinner.OnItemSelectedListener(){
    	public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3){
    		String area = arg0.getItemAtPosition(arg2).toString();
    		bfarea=Integer.parseInt(area);
    	}
    	public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
    public class MyOnPressLis implements OnLongPressListener{
		public void onLongPress(float x, float y) {
			int[] ids=	graphicsLayer.getGraphicIDs(x, y, 10);
			Graphic g=graphicsLayer.getGraphic(ids[0]);
			Geometry geo= g.getGeometry();
			Point pt = map.toMapPoint(new Point(x, y));
			SpatialReference spatialRef = SpatialReference.create(102100);
			Polygon pg = GeometryEngine.buffer(geo, spatialRef, bfarea, LINEARUNIT_METER); //null 表示采用地图单位
			SimpleFillSymbol sfs = new SimpleFillSymbol(Color.GREEN);
			sfs.setOutline(new SimpleLineSymbol(Color.RED, 1,
					com.esri.core.symbol.SimpleLineSymbol.STYLE.SOLID));
			sfs.setAlpha(25);
			Graphic result = new Graphic(pg, sfs);
			graphicsLayer.addGraphic(result);
		}
    }
     //菜单的加载
  	@Override
  	public boolean onCreateOptionsMenu(Menu menu){
  		//加载点选择菜单
  		SubMenu pointSubMenu=menu.addSubMenu("点");
  		pointSubMenu.setHeaderTitle("选择绘制的点");
  		pointSubMenu.add(0, 0, 0, "图点");
  		pointSubMenu.add(0, 1, 0, "红点");
  		pointSubMenu.add(0, 2, 0, "蓝点");
  		pointSubMenu.add(0, 3, 0, "绿点");
  		//加载线选择菜单
  		SubMenu lineSubMenu=menu.addSubMenu("线");
  		lineSubMenu.setHeaderTitle("选择绘制的线");
  		lineSubMenu.add(1,0,0,"白线");
  		lineSubMenu.add(1, 1, 0, "红线");
  		lineSubMenu.add(1, 2, 0, "蓝虚线");
  		lineSubMenu.add(1, 3, 0, "黄粗线");
  		//加载面选择菜单
  		SubMenu gonMenu=menu.addSubMenu("面");
  		gonMenu.setHeaderTitle("选择绘制的面");
  		gonMenu.add(2, 0, 0, "红面");
  		gonMenu.add(2, 1, 0, "绿面半透明");
  		gonMenu.add(2,2,0,"蓝面虚线填充");
  		
  		return super.onCreateOptionsMenu(menu);
  	}
  	
  //对菜单的点击事件
  	//菜单项被单击的事件
  	@Override
  	public boolean onOptionsItemSelected(MenuItem mi){
  		isChoose=true;
  		//判断是在哪个groupid里面的
  		switch (mi.getGroupId()) {
  		case 0:
  			drawType=Geometry.Type.POINT;
  			switch (mi.getItemId()) {
  				case 0:
  						Drawable drawable=this.getResources().getDrawable(R.drawable.loc);
  						symbol=new PictureMarkerSymbol(drawable);
  					break;
  					
  				case 1:
  					symbol=new SimpleMarkerSymbol(Color.RED, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
  					break;
  					
  				case 2:
  					symbol=new SimpleMarkerSymbol(Color.BLUE, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
  					break;
  					
  				case 3:
  					symbol=new SimpleMarkerSymbol(Color.GREEN, 14, SimpleMarkerSymbol.STYLE.CIRCLE);
  					break;
  	
  				default:
  					break;
  			}
  			break;
  		case 1:
  			drawType=Geometry.Type.POLYLINE;
  			switch (mi.getItemId()) {
  				case 0:
  					symbol=new SimpleLineSymbol(Color.WHITE, 8, SimpleLineSymbol.STYLE.SOLID);
  					break;
  					
  				case 1:
  					symbol=new SimpleLineSymbol(Color.RED, 8, SimpleLineSymbol.STYLE.SOLID);
  					break;
  					
  				case 2:
  					symbol=new SimpleLineSymbol(Color.BLUE, 10, SimpleLineSymbol.STYLE.DASH);
  					break;
  					
  				case 3:
  					symbol=new SimpleLineSymbol(Color.YELLOW, 18, SimpleLineSymbol.STYLE.SOLID);
  					break;
  	
  				default:
  					break;
  			}
  			
  			break;
  		case 2:
  			drawType=Geometry.Type.POLYGON;
  			switch (mi.getItemId()) {
  				case 0:
  					fillSymbol=new SimpleFillSymbol(Color.RED, SimpleFillSymbol.STYLE.SOLID);
  					fillSymbol.setAlpha(100);
  					break;
  					
  				case 1:
  					fillSymbol=new SimpleFillSymbol(Color.GREEN);
  					fillSymbol.setAlpha(50);
  					break;
  					
  				case 2:
  					fillSymbol=new SimpleFillSymbol(Color.BLUE,SimpleFillSymbol.STYLE.BACKWARD_DIAGONAL);
  					fillSymbol.setAlpha(100);
  					break;

  				default:
  					break;
  			}
  			
  			break;
  		
  		default:
  			break;
  		}
  		
  		
  		return false;
  		
  	}
  	
  
  		
  	public class DrawGraphicTouchListener extends MapOnTouchListener{
//		List<Point> pointsList=new ArrayList<Point>();
		Point ptStart=null;
		Point ptPrevious=null;
		Polygon polygon=null;
		public DrawGraphicTouchListener(Context context, MapView view) {
			super(context, view);
		}
		@Override
		public  boolean onSingleTap (MotionEvent point){
			if(isChoose==true){
				ptPrevious=null;
				ptStart=null;
				polygon=null;
			}
			float x=point.getX();
			float y=point.getY();
			Point ptCurrent=map.toMapPoint(x,y);
			if (drawType==Geometry.Type.POINT) {
				Graphic pGraphic=new Graphic(ptCurrent, symbol);
				graphicsLayer.addGraphic(pGraphic);
			}
			else {
				if (ptStart==null) {
					ptStart=ptCurrent;
					Graphic pgraphic=new Graphic(ptStart,new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
					graphicsLayer.addGraphic(pgraphic);
				}
				else {
					Graphic pGraphic=new Graphic(ptCurrent,new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
					graphicsLayer.addGraphic(pGraphic);
					Line line=new Line();
					line.setStart(ptPrevious);
					line.setEnd(ptCurrent);
					if(drawType==Geometry.Type.POLYLINE){
						Polyline polyline=new Polyline();
						polyline.addSegment(line, true);
						Graphic iGraphic=new Graphic(polyline, symbol);
						graphicsLayer.addGraphic(iGraphic);
					}
					else if (drawType==Geometry.Type.POLYGON) {
						if(polygon==null){
							polygon=new Polygon();
						}
						polygon.addSegment(line, false);
						Graphic gGraphic=new Graphic(polygon, fillSymbol);
						graphicsLayer.addGraphic(gGraphic);
					}
				}
			}
			ptPrevious=ptCurrent;
			isChoose=false;
			return false;
			
		}
		
	}
	@Override 
	protected void onDestroy() { 
		super.onDestroy();
 }
	@Override
	protected void onPause() {
		super.onPause();
		map.pause();
 }
	@Override 	protected void onResume() {
		super.onResume(); 
		map.unpause();
	}

}