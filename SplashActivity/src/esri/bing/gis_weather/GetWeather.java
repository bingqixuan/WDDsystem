package esri.bing.gis_weather;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;



public class GetWeather {
	/**
	 * ��ȡ����tips
	 * @return ������tips�ַ���
	 */
	public String getWeather(String city) {
		 String url = "http://php.weather.sina.com.cn/xml.php?city="+city+"&password=DJOYnieT8234jlsK&day=0";
		 HttpResponse httpResponse = null;
		 StringBuffer result=null;
		 DefaultHttpClient client=null;
		 try {
			 client=new DefaultHttpClient();
			 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			 HttpGet httpGet = new HttpGet(url);
			 httpResponse = client.execute(httpGet);
			 if (httpResponse.getStatusLine().getStatusCode() == 200){
				 result=new StringBuffer("");
				 String s=EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				InputStream   in_withcode   =   new   ByteArrayInputStream(s.getBytes("UTF-8"));
				Weather w=new Configration().readInfo(in_withcode);
				if(w!=null){
					 //result.append(w.getCity());//����
					 result.append("����״��:"+w.getStatus1());//����״��
					 result.append("ת"+w.getStatus2()+" ");//����״��
					 result.append("\n����"+w.getDirection1()+w.getPower1()+"��~");//��
					 result.append(w.getDirection2()+w.getPower2()+"�� ");//��
					 result.append("\n�¶�:"+w.getTemperature2()+"~"+w.getTemperature1()+"��");//����
					 result.append("\n�����˶�:"+w.getYd_s());
					 result.append("\n����ʱ�䣺"+w.getUdatetime());
				}
			 }else{
				 Log.i("note", "���������쳣��");
				 result=null;
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result!=null)
			return result.toString();
		else return null;
	}
}
