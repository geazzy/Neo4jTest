package com.geazzy;

import java.net.URI;
import java.net.URISyntaxException;

public class App {

	
	final static String SERVER_ROOT_URI = "http://geazzy.com:7474";
	
	public static void main(String[] args) {
		
		Server server = new Server();
		
		server.getStatus(SERVER_ROOT_URI);
		//System.out.println(server.createNode(SERVER_ROOT_URI));
		
		URI firstNode = server.createNode(SERVER_ROOT_URI);
		URI secondNode = server.createNode(SERVER_ROOT_URI);
		@SuppressWarnings("unused")
		URI relationshipUri;
		
		try {
			server.addProperty( firstNode, "name", "Joe Strummer" );
			server.addProperty( secondNode, "band", "The Clash" );
			relationshipUri = server.addRelationship( firstNode, secondNode, "singer",
			        "{ \"from\" : \"1976\", \"until\" : \"1986\" }" );
			server.addMetadataToProperty( relationshipUri, "stars", "5" );
			server.query(firstNode);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	

		
	}



}
