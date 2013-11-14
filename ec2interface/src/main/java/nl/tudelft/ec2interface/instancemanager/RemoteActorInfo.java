package nl.tudelft.ec2interface.instancemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import nl.tudelft.ec2interface.sysmonitor.SystemUsageInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class RemoteActorInfo {
	
	private String publicIP;
	private String actorPath;
	
	public String getPublicIP() {
		return publicIP;
	}

	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}

	public String getActorPath() {
		return actorPath;
	}

	public void setActorPath(String actorPath) {
		this.actorPath = actorPath;
	}

	public String ToJson(RemoteActorInfo maInfo)
	{
		try {
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(maInfo);
			return json;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
			
		}
		
	}
	
	public RemoteActorInfo FromJson(String jsonString)
	{
		try {
			
			RemoteActorInfo maInfo = new ObjectMapper().readValue(jsonString, RemoteActorInfo.class);
			return maInfo;
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public RemoteActorInfo setInfo(String publicIP, String actorPath)
	{
		RemoteActorInfo maInfo = new RemoteActorInfo();
		maInfo.setPublicIP(publicIP);
		maInfo.setActorPath(actorPath);
		return maInfo;
	}
	
	public RemoteActorInfo getInfoFromFile(String filePath)
	{
		try {
			String jsonString = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
			return new RemoteActorInfo().FromJson(jsonString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		RemoteActorInfo maInfo = new RemoteActorInfo().setInfo("127.0.0.1:2552", "akka.tcp://MasterNode@127.0.0.1:2552/user/masterActor");
		
		String maInfoString = maInfo.ToJson(maInfo).toString();
		
		System.out.println(maInfoString);
		System.out.println(maInfo.FromJson(maInfoString).getPublicIP());
		System.out.println(maInfo.FromJson(maInfoString).getActorPath());
		System.out.println(new RemoteActorInfo().getInfoFromFile("masterInfo").getPublicIP());
		
	}

}
