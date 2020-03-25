import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

import com.phidget22.*;
import com.google.gson.Gson;

public class MotorFromServer{

	static RCServo servo;
	public static String sensorServerURL = "http://localhost:8080/PhidgetServer/sensorToDB";

 
	public static void main(String[] args) throws Exception {
         
        servo = new RCServo();
       
        servo.addTargetPositionReachedListener(new RCServoTargetPositionReachedListener() {
			public void onTargetPositionReached(RCServoTargetPositionReachedEvent e) {
				System.out.printf("Target Position Reached: %.3g\n", e.getPosition());
			}
        });
        
        try {

            System.out.println("MFS-Opening and waiting for 15 seconds");
            int i = 0;
            int lastSlideVal = 0;
            while((i++) < 15) {
	            servo.open(15000);
	            //reads in slider data for motor position 
	            String motorPosStr = getFromServer("slider");
	            Gson gson = new Gson();
	            sensorData sData = gson.fromJson(motorPosStr, sensorData.class);
	            int motorPos = Integer.parseInt(sData.getValue());
	            
	            //mobile app open door button sends the value 200 as sensors are unable to send this value
	            if(motorPos == 200)
	            {
	            	//opens door for 3 seconds when value is received 
	            	System.out.println("\n\nOpening lock to position to 180 for 3 seconds\n\n");
	                servo.setTargetPosition(180.0);
	                servo.setEngaged(true);
	                Thread.sleep(3000);
	                
	                System.out.println("\n\nSetting lock closed, position 0\n\n");
	                servo.setTargetPosition(0);
	                servo.setEngaged(true);
	                //sets last slider value back to a valid slider value
	                sendToServer(lastSlideVal,"slider");
	            }
	            else if (motorPos != lastSlideVal)
	            {//if the read value isn't the same as the last value
	            System.out.println("Retrieved data from server: " + motorPosStr);
	            //moves motor to read value and sets as new last value
	            servo.setTargetPosition(motorPos);
	            servo.setEngaged(true);
	            lastSlideVal = motorPos;
	            }
	            Thread.sleep(1000);
	            servo.close();
	            System.out.println("\nClosed Motor");
            }
            
        } catch (PhidgetException ex) {
            System.out.println(ex.getDescription());
        }
    }
	
	public static String getFromServer(String sensorName){
		//creates URL to receive values from server, using sensors name
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = sensorServerURL + "?sensorname="+sensorName+"&getdata=true";
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
	
	public static String sendToServer(int sensorValue, String sensorName){
		//creates URL to send data to server, using sensors name and value
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
	
}
