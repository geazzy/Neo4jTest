package com.geazzy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Server {


	public void getStatus(String SERVER_ROOT_URI) {
		WebResource resource = Client.create()
		        .resource( SERVER_ROOT_URI );
		ClientResponse response = resource.get( ClientResponse.class );
		 
		System.out.println( String.format( "GET on [%s], status code [%d]",
		        SERVER_ROOT_URI, response.getStatus() ) );
		response.close();
	}
	
	public URI createNode(String SERVER_ROOT_URI){
		String nodeEntryPointUri = SERVER_ROOT_URI + "/db/data/node";
		// http://localhost:7474/db/data/node
		 
		WebResource resource = Client.create()
		        .resource( nodeEntryPointUri );
		// POST {} to the node entry point URI
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
		        .type( MediaType.APPLICATION_JSON )
		        .entity( "{}" )
		        .post( ClientResponse.class );
		 
		URI location = response.getLocation();
		System.out.println( String.format(
		        "POST to [%s], status code [%d], location header [%s]",
		        nodeEntryPointUri, response.getStatus(), location.toString() ) );
		response.close();
		 
		return location;	
	}

	public void addProperty(URI nodeUri, String propertyName, String propertyValue) throws URISyntaxException {
		
		String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
		// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}
		 
		WebResource resource = Client.create()
		        .resource( propertyUri );
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
		        .type( MediaType.APPLICATION_JSON )
		        .entity( "\"" + propertyValue + "\"" )
		        .put( ClientResponse.class );
		 
		System.out.println( String.format( "PUT to [%s], status code [%d]",
		        propertyUri, response.getStatus() ) );
		response.close();
		
	}

	public URI addRelationship(URI startNode, URI endNode, String relationshipType,
			String jsonAttributes) throws URISyntaxException {
		
		URI fromUri = new URI( startNode.toString() + "/relationships" );
	    String relationshipJson = generateJsonRelationship( endNode,
	            relationshipType, jsonAttributes );
	 
	    WebResource resource = Client.create()
	            .resource( fromUri );
	    // POST JSON to the relationships URI
	    ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
	            .type( MediaType.APPLICATION_JSON )
	            .entity( relationshipJson )
	            .post( ClientResponse.class );
	 
	    URI location = response.getLocation();
	    System.out.println( String.format(
	            "POST to [%s], status code [%d], location header [%s]",
	            fromUri, response.getStatus(), location.toString() ) );
	 
	    response.close();
	    return location;
	}
	
	 private String generateJsonRelationship( URI endNode,
	            String relationshipType, String... jsonAttributes )
	    {
	        StringBuilder sb = new StringBuilder();
	        sb.append( "{ \"to\" : \"" );
	        sb.append( endNode.toString() );
	        sb.append( "\", " );

	        sb.append( "\"type\" : \"" );
	        sb.append( relationshipType );
	        if ( jsonAttributes == null || jsonAttributes.length < 1 )
	        {
	            sb.append( "\"" );
	        }
	        else
	        {
	            sb.append( "\", \"data\" : " );
	            for ( int i = 0; i < jsonAttributes.length; i++ )
	            {
	                sb.append( jsonAttributes[i] );
	                if ( i < jsonAttributes.length - 1 )
	                { // Miss off the final comma
	                    sb.append( ", " );
	                }
	            }
	        }

	        sb.append( " }" );
	        return sb.toString();
	    }

	 public void addMetadataToProperty( URI relationshipUri,
		        String name, String value ) throws URISyntaxException
		{
		    URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
		    String entity = toJsonNameValuePairCollection( name, value );
		    WebResource resource = Client.create()
		            .resource( propertyUri );
		    ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
		            .type( MediaType.APPLICATION_JSON )
		            .entity( entity )
		            .put( ClientResponse.class );
		 
		    System.out.println( String.format(
		            "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
		            response.getStatus() ) );
		    response.close();
		}
	 
	  private String toJsonNameValuePairCollection( String name,
	            String value )
	    {
	        return String.format( "{ \"%s\" : \"%s\" }", name, value );
	    }

	public void query(URI startNode) throws URISyntaxException {
		
			TraversalDefinition t = new TraversalDefinition();
			t.setOrder( TraversalDefinition.DEPTH_FIRST );
			t.setUniqueness( TraversalDefinition.NODE );
			t.setMaxDepth( 10 );
			t.setReturnFilter( TraversalDefinition.ALL );
			t.setRelationships( new Relation( "singer", Relation.OUT ) );
			
			URI traverserUri = new URI( startNode.toString() + "/traverse/node" );
			WebResource resource = Client.create()
			        .resource( traverserUri );
			String jsonTraverserPayload = t.toJson();
			ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
			        .type( MediaType.APPLICATION_JSON )
			        .entity( jsonTraverserPayload )
			        .post( ClientResponse.class );
			 
			System.out.println( String.format(
			        "POST [%s] to [%s], status code [%d], returned data: "
			                + System.getProperty( "line.separator" ) + "%s",
			        jsonTraverserPayload, traverserUri, response.getStatus(),
			        response.getEntity( String.class ) ) );
			response.close();
		
		
	}

}
