package esri.bing.gis_weather;

public class Weather {
	private String city;
	private int temperature2;
	private int temperature1;
	private String status1;
	private String status2;
	private String direction1;
	private String direction2;
	private String power1;
	private String power2;
	private String xcz_s;
	private String yd_s;
	private String udatetime;
	private String gm;
	
	public String getUdatetime() {
		return udatetime;
	}
	public void setUdatetime(String udatetime) {
		this.udatetime = udatetime;
	}
	public String getYd_s() {
		return yd_s;
	}
	public void setYd_s(String yd_s) {
		this.yd_s = yd_s;
	}
	public String getXcz_s() {
		return xcz_s;
	}
	public void setXcz_s(String xcz_s) {
		this.xcz_s = xcz_s;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getTemperature2() {
		return temperature2;
	}
	public void setTemperature2(int temperature2) {
		this.temperature2 = temperature2;
	}
	public int getTemperature1() {
		return temperature1;
	}
	public void setTemperature1(int temperature1) {
		this.temperature1 = temperature1;
	}
	public String getStatus1() {
		return status1;
	}
	public void setStatus1(String status1) {
		this.status1 = status1;
	}
	public String getStatus2() {
		return status2;
	}
	public void setStatus2(String status2) {
		this.status2 = status2;
	}
	public String getDirection1() {
		return direction1;
	}
	public void setDirection1(String direction1) {
		this.direction1 = direction1;
	}
	public String getDirection2() {
		return direction2;
	}
	public void setDirection2(String direction2) {
		this.direction2 = direction2;
	}
	public String getPower1() {
		return power1;
	}
	public void setPower1(String power1) {
		this.power1 = power1;
	}
	public String getPower2() {
		return power2;
	}
	public void setPower2(String power2) {
		this.power2 = power2;
	}
}
