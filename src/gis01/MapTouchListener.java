package gis01;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.eoe.leigo.splash.R;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;



public class MapTouchListener extends MapOnTouchListener {
	private MapView map;
	private GraphicsLayer layer;
	private Context context;
	   
	private Geometry.Type geoType = null;//�����ж���ǰѡ��ļ���ͼ������  
	private boolean IsMeasuringLength=false;
	private boolean IsMeasuringArea=false;
	
	//private boolean IsCorrectingtPlot=false;
	//private boolean IsCorrectingtPoint=false;
	
	private Point ptStart=null;//���
    private Point ptPrevious=null;//��һ��
    private ArrayList<Point> points=null;//��¼ȫ����
    private Polygon tempPolygon=null;
    public Callout resultCallOut;
    
	private SimpleLineSymbol lineSymbol;  
	private SimpleMarkerSymbol markerSymbol;  
	private SimpleFillSymbol fillSymbol;  
	private int index1;
	private int index2;
	private Graphic tempGraphic;
 
	public MapTouchListener(Context context, MapView view) {
		super(context, view);
		// TODO Auto-generated constructor stub
		points=new ArrayList<Point>();
		this.context=context;
		this.map=view;
		this.initSymbols();
	}
	public void getLayer(GraphicsLayer drawLayer)
	{
		this.layer=drawLayer;
	}
	// �����û�ѡ�����õ�ǰ���Ƶļ���ͼ������
	public void setType(String geometryType) 
	{
		if(geometryType.equalsIgnoreCase("Point"))
			this.geoType = Geometry.Type.POINT;
		else if(geometryType.equalsIgnoreCase("Polyline"))
			this.geoType = Geometry.Type.POLYLINE;
		else if(geometryType.equalsIgnoreCase("Polygon"))
			this.geoType = Geometry.Type.POLYGON;
	}
	public void setEmpty()
	{
	    ptStart = null;  
	    ptPrevious = null;  
	    points.clear();  
	    tempPolygon = null; 
	    tempGraphic=null;
	}
	public void getDrawType(Geometry.Type geotype)
	{
		this.geoType=geotype;
	}
	public void setMeasureStatus(boolean IsMeasuringLength,boolean IsMeasuringArea)
	{
		this.IsMeasuringLength=IsMeasuringLength;
		this.IsMeasuringArea=IsMeasuringArea;
	}
	/*
	public void setCorrectStatus(boolean IsCorrectingPlot,boolean IsCorrectingPoint)
	{
		this.IsCorrectingtPlot=IsCorrectingPlot;
		this.IsCorrectingtPoint=IsCorrectingPoint;
	}
	*/
    private void initSymbols()
    {
    	lineSymbol = new SimpleLineSymbol(Color.RED, 2, SimpleLineSymbol.STYLE.DASH);
    	markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
    	fillSymbol = new SimpleFillSymbol(Color.RED);
    	fillSymbol.setAlpha(33);
    } 
    
	@Override
	public boolean onSingleTap(MotionEvent point) {
		// TODO Auto-generated method stub
		Point dp=map.toMapPoint(point.getX(), point.getY());
		int[] IDs=new int[]{};
		resultCallOut=map.getCallout();
		IDs=layer.getGraphicIDs(point.getX(), point.getY(),20);
		if(IDs.length>0)
		{
			
			
			if(IsMeasuringLength==true||IsMeasuringArea==true)
			{
				Geometry sGeometry;
				resultCallOut.setStyle(R.layout.calloutstyle);
				LayoutInflater inflater = LayoutInflater.from(this.context);  
				View view = inflater.inflate(R.layout.showresult_layout, null);
				TextView text1=(TextView)view.findViewById(R.id.callTitle);
				TextView text2=(TextView)view.findViewById(R.id.callContext);
				text1.setTextColor(Color.BLACK);
				text1.setTextSize(15);
				text2.setTextColor(Color.BLACK);
				text2.setTextSize(15);
				Graphic sGrapdic=layer.getGraphic(IDs[0]);
				sGeometry=sGrapdic.getGeometry();
				if(sGeometry.getType()==Geometry.Type.POLYLINE&&IsMeasuringLength==true)
				{
					String length = Double.toString(Math.round(sGeometry.calculateLength2D())) + " ��";
					text1.setText("����Ϊ��");
					text2.setText(length);
					resultCallOut.show(dp,view);
				}
				else if(sGeometry.getType()==Geometry.Type.POLYGON&&IsMeasuringArea==true)
				{
					String sArea = getAreaString(sGeometry.calculateArea2D());  
					text1.setText("���Ϊ��");
					text2.setText(sArea);
					resultCallOut.show(dp,view);
				}
			}
		}
		else
		{
			if(geoType!=null)
			{
				Point ptCurrent=map.toMapPoint(new Point(point.getX(),point.getY()));
			    points.add(ptCurrent);//����ǰ�����㼯����      
			    if(ptStart == null)
			    {//���߻����εĵ�һ����  
			        ptStart = ptCurrent; 
			        if(geoType==Geometry.Type.POINT)
			        {
				        //���Ƶ�һ����  
				        Graphic graphic = new Graphic(ptStart,markerSymbol);  
				        layer.addGraphic(graphic);   
			        }
                                 
			    }  
			    else
			    {//���߻����ε�������  
			     //����������  
			        if(geoType==Geometry.Type.POINT)
			        {
				        //���Ƶ�һ����  
				        Graphic graphic = new Graphic(ptCurrent,markerSymbol);  
				        layer.addGraphic(graphic);   
			        }
			              
			        //���ɵ�ǰ�߶Σ��ɵ�ǰ�����һ���㹹�ɣ�  
			        Line line = new Line();  
			        line.setStart(ptPrevious);  
			        line.setEnd(ptCurrent);  
			              
			        if(geoType == Geometry.Type.POLYLINE)
			        {  
			        	if(tempGraphic!=null)
			        	{
			        		layer.removeGraphic(index1);
			        	}
			        	
			            Polyline polyline = new Polyline();  
			            Point startPoint = null;  
			            Point endPoint = null;  		          
			            // �����������߶�  
			            for(int i=1;i<points.size();i++)
			            {  
			                startPoint = points.get(i-1);  
			                endPoint = points.get(i);  
			                  
			                Line line1 = new Line();  
			                line1.setStart(startPoint);  
			                line1.setEnd(endPoint);  
			      
			                polyline.addSegment(line1, false);  
			            } 	
			            tempGraphic = new Graphic(polyline, lineSymbol);  
			            index1 = layer.addGraphic(tempGraphic); 
			            // ���㵱ǰ�߶εĳ���  
			            if(IsMeasuringLength==true)
						{
				            String length = Double.toString(Math.round(line.calculateLength2D())) + " ��";                   
				            Toast.makeText(map.getContext(), length, Toast.LENGTH_SHORT).show();  
						}
			            
			        }  
			        if(geoType == Geometry.Type.POLYGON)
			        {  
			        	if(tempGraphic!=null)
			        	{
			        		layer.removeGraphic(index2);
			        	}
			            //������ʱ�����  
			            if(tempPolygon == null) tempPolygon = new Polygon();  
			            tempPolygon.addSegment(line, false);  
			                 
			            Polygon polygon = new Polygon();  	  
			            Point startPoint = null;  
			            Point endPoint = null;  
			            // ���������Ķ����  
			            for(int i=1;i<points.size();i++)
			            {  
			                startPoint = points.get(i-1);  
			                endPoint = points.get(i);  
			                 
			                Line line1 = new Line();  
			                line1.setStart(startPoint);  
			                line1.setEnd(endPoint);  
			      
			                polygon.addSegment(line1, false);  
			            }  			        
			            tempGraphic = new Graphic(polygon, fillSymbol);  
			            index2 = layer.addGraphic(tempGraphic); 
			            //���㵱ǰ���  
			            if(IsMeasuringArea==true)
						{
				            String sArea = getAreaString(tempPolygon.calculateArea2D());  		                  
				            Toast.makeText(map.getContext(), sArea, Toast.LENGTH_SHORT).show();  
						}
					}  
			    }       
			    ptPrevious = ptCurrent;  
			   
			}
		}
		return true; 
	}
	/*
	@Override
	
	public boolean onDoubleTap(MotionEvent point) {
		// TODO Auto-generated method stub
		if(geoType==null)
		{ return true;}
		else
		{    
            this.setEmpty(); 
            if(IsCorrectingtPlot==true)
    		{
    			Intent intent=new Intent();
    			intent.setClass(context,PlotCorrectionActivity.class);
    			context.startActivity(intent);
    		}
		    return false;  
	   }
	}
	*/
	
	@Override
	public void onLongPress(MotionEvent point) {
		// TODO Auto-generated method stub
		super.onLongPress(point);
		Point p=map.toMapPoint(point.getX(), point.getY());
		int[] IDs=new int[]{};
		IDs=layer.getGraphicIDs(point.getX(), point.getY(),20);
		if(IDs.length>0)
		{
			final Callout jiaohuCallOut=map.getCallout();
			jiaohuCallOut.setStyle(R.layout.calloutstyle);
			LayoutInflater inflater = LayoutInflater.from(this.context);  
			View view = inflater.inflate(R.layout.jiaohu_layout, null);
			jiaohuCallOut.show(p,view);
			Button btnYes=(Button)view.findViewById(R.id.btnYes);
			Button btnNo=(Button)view.findViewById(R.id.btnNo);
			final int m=IDs[0];
			btnYes.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					layer.removeGraphic(m);
					jiaohuCallOut.hide();
					setEmpty();
				}});
			btnNo.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					jiaohuCallOut.hide();
				}});
		}
	}
    private String getAreaString(double dValue){  
        long area = Math.abs(Math.round(dValue));  
        String sArea = "";  
        // ˳ʱ����ƶ���Σ����Ϊ������ʱ����ƣ������Ϊ��  
        if(area >= 1000000){                   
            double dArea = area / 1000000.0;  
            sArea = Double.toString(dArea) + " ƽ������";  
        }  
        else  
            sArea = Double.toString(area) + " ƽ����";  
        return sArea;  
    }  
}
