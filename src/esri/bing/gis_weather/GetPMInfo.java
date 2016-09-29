package esri.bing.gis_weather;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class GetPMInfo {
	public String getPMInfoImpl(String city){
		 String url = "http://api.thinkpage.cn/v2/weather/air.json?city="+city+"&language=zh-chs&unit=c&aqi=city&key=110QAGUDVO";
		 HttpResponse httpResponse = null;
		 StringBuffer result=null;
		 DefaultHttpClient client=null;
		 JSONObject airJson=null;
		 HttpGet httpGet=null;
		 
		 try {
			client=new DefaultHttpClient();
			 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			 httpGet = new HttpGet(url);
			 httpResponse = client.execute(httpGet);
			 if(httpResponse.getStatusLine().getStatusCode()==200){
				 result=new StringBuffer();
				 String air=EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				 airJson=new JSONObject(air);
				 if(airJson.getString("status").equals("OK")){
					 String weather=airJson.getString("weather");
					 weather=weather.substring(1, weather.length()-1);
					 JSONObject cityAir=new JSONObject(weather).getJSONObject("air_quality").getJSONObject("city");
					 result.append(cityAir.get("aqi")+"---"+cityAir.get("quality")+"\nPM2.5:"+
							 cityAir.get("pm25")+"\nPM10:"+cityAir.get("pm10")+"\nSO2:"+cityAir.get("so2")
							 +"\nNO2:"+cityAir.get("no2")+"\nCO:"+cityAir.get("co")+"\nO3:"+cityAir.get("o3"));
				 }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(result!=null){
			 return result.toString();
		 }else return null;
	}
}
