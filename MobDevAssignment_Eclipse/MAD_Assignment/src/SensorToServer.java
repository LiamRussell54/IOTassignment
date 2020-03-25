import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.phidget22.*;

public class SensorToServer  {
    
    VoltageRatioInput slider = new VoltageRatioInput();
    VoltageRatioInput rotate = new VoltageRatioInput();
    int slideValue = 0;
    int rotateValue = 0;

    public static String sensorServerURL = "http://localhost:8080/PhidgetServer/sensorToDB";
    
    public static void main(String[] args) throws PhidgetException {
        new SensorToServer();
    }

    public SensorToServer() throws PhidgetException {
    	
    	//open sensors
    	System.out.println("STS-Opening and waiting for 15 seconds");
        slider.setDeviceSerialNumber(314744);
        slider.setChannel(3);
        slider.open(15000);
        
        rotate.setDeviceSerialNumber(314744);
        rotate.setChannel(2);
        rotate.open(15000);
       
       //listener to detect a change in sliders voltage 
        slider.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
  			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
  				//reads in voltage and scales by 180
  				//voltages are scaled to 180 as thats how far the motor can rotate
  				double sensorReading = e.getVoltageRatio();
  				int scaledSensorReading = (int) (sensorReading*180.0);
  				//if the value is different to the last, send value to server and set as new last value
  				if (scaledSensorReading != slideValue ) {
  					System.out.println("Sending new sensor value : " + scaledSensorReading);
  					sendToServer(scaledSensorReading,"slider");
  					slideValue = scaledSensorReading;
  				}
  			}
         });
        
        //listener to detect a change in rotators voltage 
        rotate.addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
  			public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent e) {
  				//reads in voltage and scales by 180
  				//voltages are scaled to 180 as thats how far the motor can rotate
  				double sensorReading = e.getVoltageRatio();
  				int scaledSensorReading = (int) (sensorReading*180.0);
  				//if the value is different to the last, send value to server and set as new last value
  				if (scaledSensorReading != rotateValue ) {
  					System.out.println("Sending new sensor value : " + scaledSensorReading);
  					sendToServer(scaledSensorReading,"rotate");
  					rotateValue = scaledSensorReading;
  				}
  			}
         });
 
        try {      
                            
            System.out.println("\n\nGathering data for 15 seconds\n\n");
            pause(15);
            slider.close();
            rotate.close();
            System.out.println("\nClosed Slider and Rotate Voltage Ratio Input");
            
        } catch (PhidgetException ex) {
            System.out.println(ex.getDescription());
        }

    }

    public String sendToServer(int sensorValue, String sensorName){
    	//creates the URL using the info that is passed from the sensors 
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = sensorServerURL + "?sensorname="+sensorName+"&sensorvalue="+sensorValue;
        System.out.println("Sending data to: "+fullURL);
        String line;
        String result = "";
        try {
           url = new URL(fullURL);
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           while ((line = rd.readLine()) != null) {
              result += line;
           }
           rd.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return result;
    	
    }
    
	private void pause(int secs){
        try {
			Thread.sleep(secs*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}