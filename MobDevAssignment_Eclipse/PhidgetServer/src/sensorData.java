
public class sensorData {
	//class to store sensor name and value when data is being sent/received as JSON
	String name,value;

	public sensorData(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public sensorData() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
