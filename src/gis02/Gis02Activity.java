package gis02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;






import cn.eoe.leigo.splash.R;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnStatusChangedListener.STATUS;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.ags.na.NAFeaturesAsFeature;
import com.esri.core.tasks.ags.na.Route;
import com.esri.core.tasks.ags.na.RoutingDirection;
import com.esri.core.tasks.ags.na.RoutingParameters;
import com.esri.core.tasks.ags.na.RoutingResult;
import com.esri.core.tasks.ags.na.RoutingTask;
import com.esri.core.tasks.ags.na.StopGraphic;




public class Gis02Activity extends Activity {
	
		MapView mMapView ;
		private Button btnZoomIn = null;
		private Button btnZoomOut = null;
		private Button btnAnalysis=null;
		private TextView tv_label;
		private SpatialReference mapSR=SpatialReference.create(102100);;
		private SpatialReference NASR=SpatialReference.create(4326);
		private GraphicsLayer routeLayer;//��ѯ��������·��
		private GraphicsLayer semLayer;//��ѯ����·��Ƭ��
		private List<Point> stopPoints;//�������е�ͣ����
		private Symbol stopSymbol;//ͣ����ķ���
		 private SimpleLineSymbol hiderSym;//��ѯ����·����Ƭ�Σ�����͸�����䡰���ء�
		 private SimpleLineSymbol showSym;//��·��Ƭ�α�ѡ�е�ʱ����ʾ�ķ���
		 private int selectID=-1;//��ѡ�е�·��Ƭ�ε�ID��-1��ʾû�б�ѡ�У�
		 String urlImage;
		 Drawable drawable;
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.gis02_main);
	        
			mMapView = (MapView) findViewById(R.id.map);
			this.btnZoomIn = (Button) findViewById(R.id.btnZoomIn);
			this.btnZoomOut = (Button) findViewById(R.id.btnZoomOut);
			this.btnAnalysis = (Button) findViewById(R.id.btnAnalysis);
			tv_label=(TextView) findViewById(R.id.directionsLabel);
			drawable=this.getResources().getDrawable(R.drawable.loc);
			//ArcGISDynamicMapServiceLayer Imagine=new ArcGISDynamicMapServiceLayer("http://10.5.230.4:6080/arcgis/rest/services/Image/MapServer");
			ArcGISDynamicMapServiceLayer Imagine=new ArcGISDynamicMapServiceLayer(this.getResources().getString(R.string.urlImage));
			mMapView.addLayer(Imagine);
			//ArcGISTiledMapServiceLayer atl=new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer");
			ArcGISDynamicMapServiceLayer Road=new ArcGISDynamicMapServiceLayer(this.getResources().getString(R.string.urlVec102100));
			mMapView.addLayer(Road);
			
			this.btnZoomIn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Gis02Activity.this.mMapView.zoomin();
				}
				});
			this.btnZoomOut.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Gis02Activity.this.mMapView.zoomout();
				}
				});	
			
			//mapSR=mMapView.getSpatialReference();
			routeLayer=new GraphicsLayer();
			semLayer=new GraphicsLayer();
			stopPoints=new ArrayList<Point>();
			mMapView.addLayer(routeLayer);
			mMapView.addLayer(semLayer);
			//stopSymbol=new SimpleMarkerSymbol(Color.RED, 3, SimpleMarkerSymbol.STYLE.CIRCLE);
			stopSymbol=new PictureMarkerSymbol(drawable);
			hiderSym=new SimpleLineSymbol(Color.WHITE, 1);
			//���ó�͸����ʹ�䴦�ڡ����ء�״̬
			hiderSym.setAlpha(100);
			showSym=new SimpleLineSymbol(Color.RED, 4);
			
			this.btnAnalysis.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(stopPoints.size()<2){
						Toast.makeText(getApplicationContext(), "��������ѡ������ͣ����", 0).show();
						return;
		 			}
					Toast.makeText(getApplicationContext(), "��ʼ�������·��", 1).show();
						new Thread(){//����������������߳��н���
							public void run() {
								//׼������
								RoutingParameters rp=new RoutingParameters();
								NAFeaturesAsFeature naferture=new NAFeaturesAsFeature();
								//���ò�ѯͣ���㣬����Ҫ����
								for(Point p :stopPoints){
									StopGraphic sg=new StopGraphic(p);
									naferture.addFeature(sg);
								}
								rp.setStops(naferture);
								//���ò�ѯ���������ϵ����ͼһ��
								rp.setOutSpatialReference(mapSR);
								  RoutingTask rt = new RoutingTask( 
						                  "http://10.0.2.11:6080/arcgis/rest/services/Vec1/NAServer/%E8%B7%AF%E5%BE%84",
						                  null);
								  try {
									  //ִ�в���
									RoutingResult rr=rt.solve(rp);
									runOnUiThread(new MyRun(rr));
								} catch (Exception e) {
									e.printStackTrace();
									Looper.prepare();
									Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
									Looper.loop();
								}
							
							}
						}.start();
				}
				});	
			
			
			mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
				
				public void onStatusChanged(Object arg0, STATUS arg1) {
					if(arg0==mMapView&&arg1==STATUS.INITIALIZED){
						//Point p= new Point( -122.084095,37.422006);
						Point p= new Point(516286.216,4330724.422);
						mMapView.zoomToResolution((Point) GeometryEngine.project(p, NASR, mapSR), 20.0);
						Log.i("OK", "KKK");
						//������ͼ����Ҫ������λ��
						mMapView.setOnSingleTapListener(new MyOnSintTapLis());
						//������ʼִ��·������
						//mMapView.setOnLongPressListener(new MyOnPressLis());
						//����tv_label�ĵ����¼�Ϊ���������ѡ�еĺͲ�ѯ�Ľ��
						tv_label.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								semLayer.removeAll();
								routeLayer.removeAll();
								selectID=-1;
								tv_label.setText("");
							}
						});
					}
				}
			});

	    }
	    
	    /**
	     * ������ͼ�¼������ж���semLayer�����Ƿ�Ϊ�գ�
	     * ���Ϊ�վ�����ͣ���㣬�����Ϊ�������ѡ��semLayer�е����ݲ���
	     */
	    class MyOnSintTapLis implements OnSingleTapListener{
			public void onSingleTap(float arg0, float arg1) {
				
				if(semLayer.getNumberOfGraphics()==0){//����ͣ����
					Point mapPoint=mMapView.toMapPoint(arg0, arg1);
					routeLayer.addGraphic(new Graphic(mapPoint, stopSymbol));
					stopPoints.add((Point)GeometryEngine.project(mapPoint, mapSR, NASR));
				}else{//ѡ��·��Ƭ��
					int[] ids= semLayer.getGraphicIDs(arg0, arg1, 20);
					if(ids.length>0){//��·��Ƭ�α�ѡ��
						//�ظ��Ѿ���ѡ�е�·��Ƭ��Ϊ͸��
						semLayer.updateGraphic(selectID,hiderSym);
						//�����µı�ѡ�е�·��Ƭ��Ϊ��ѡ��״̬
						selectID=ids[0];
						semLayer.updateGraphic(selectID,showSym);
						//ȡ�ñ�ѡ�е�·��Ƭ�κ�������
						Graphic selectGraphic=semLayer.getGraphic(selectID);
						Geometry le=selectGraphic.getGeometry();
						String text=(String) selectGraphic.getAttributeValue("text");
						//double time=(Double) selectGraphic.getAttributeValue("time");
						String length= Double.toString(Math.round(le.calculateLength2D()));
						//double length=(Double) selectGraphic.getAttributeValue("Shape_Length");
						//tv_label.setText("���ȣ�"+length+"��,  ʱ�䣺"+time+"����,������"+text);
						tv_label.setText("���ȣ�"+length+"�ף�·�ߣ�"+text);
						//���ŵ���ѡ�е�Ƭ��
						mMapView.setExtent(selectGraphic.getGeometry(),50);
					}
				}
			}
	    	
	    }
	    /*
	    //������ͼ�¼�
	    class MyOnPressLis implements OnLongPressListener{
			public void onLongPress(float arg0, float arg1) {
			if(stopPoints.size()<2){
				Toast.makeText(getApplicationContext(), "��������ѡ������ͣ����", 0).show();
				return;
			}
			Toast.makeText(getApplicationContext(), "��ʼ�������·��", 1).show();
				new Thread(){//����������������߳��н���
					public void run() {
						//׼������
						RoutingParameters rp=new RoutingParameters();
						NAFeaturesAsFeature naferture=new NAFeaturesAsFeature();
						//���ò�ѯͣ���㣬����Ҫ����
						for(Point p :stopPoints){
							StopGraphic sg=new StopGraphic(p);
							naferture.addFeature(sg);
						}
						rp.setStops(naferture);
						//���ò�ѯ���������ϵ����ͼһ��
						rp.setOutSpatialReference(mapSR);
						  RoutingTask rt = new RoutingTask( 
				                  "http://10.5.230.4:6080/arcgis/rest/services/%E5%A4%87%E7%94%A84326net/NAServer/%E8%B7%AF%E5%BE%84",
				                  null);
						  try {
							  //ִ�в���
							RoutingResult rr=rt.solve(rp);
							runOnUiThread(new MyRun(rr));
						} catch (Exception e) {
							e.printStackTrace();
							Looper.prepare();
							Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
							Looper.loop();
						}
					
					}
				}.start();
			}
	    	
	    }
	    */
	    class MyRun implements Runnable{
	    	private RoutingResult rr;
			public MyRun(RoutingResult rr) {
				this.rr=rr;
			}
			public void run() {
					stopPoints.clear();
					//�õ���ѯ����·��
					Route r=rr.getRoutes().get(0);
					//��tv_label�и�ֵ
					//tv_label.setText("�г��ȣ�"+r.getTotalLength()+"��,  ��ʱ�䣺"+r.getTotalTime()+"����,������"+r.getRouteName());
					
					//��������ѯ��·������routeLayer��
					Graphic routeGraphic=new Graphic(r.getRoute().getGeometry(),new SimpleLineSymbol(Color.BLUE, 3));
					Geometry rou=routeGraphic.getGeometry();
					String TotalLength = Double.toString(Math.round(rou.calculateLength2D()));
					tv_label.setText("���ȣ�"+TotalLength+"�ף�·��Ϊ��"+r.getRouteName());
					routeLayer.addGraphic(routeGraphic);
					mMapView.setExtent(routeGraphic.getGeometry(),100);
					List<RoutingDirection>  routeDirs=rr.getRoutes().get(0).getRoutingDirections();

					//ȡ�ò�ѯ����·��Ƭ�Σ��������ԣ����뵽semLayer�У��������ѯÿ��Ƭ����Ϣ
					Map<String ,Object> atts=new HashMap<String,Object>();
					for(RoutingDirection routeDir: routeDirs){
					atts.put("text", routeDir.getText());
					atts.put("length", routeDir.getLength());
					//atts.put("time", routeDir.getTime());
					Graphic dirGraphic=new Graphic(routeDir.getGeometry(), hiderSym, atts, null);
					semLayer.addGraphic(dirGraphic);	
					}
				}
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