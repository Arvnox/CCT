package com.cct.resources;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


@Path("/requerimiento")
public class Requerimiento {
	@Context
	HttpServletRequest context;
	
	private static final Map<String, String> jwtMap = new HashMap<>();
	private static final Map<String, Long> jwtMapMillis = new HashMap<>();
	private static final long MAX_SESSION = 60 * 1000l; 
	
	private static String createJWT(String id, String issuer, String subject, long ttlMillis) throws UnsupportedEncodingException {
		 
		   //The JWT signature algorithm we will be using to sign the token
		   SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		 
		   long nowMillis = System.currentTimeMillis();
		   Date now = new Date(nowMillis);
		 
		  
		   //Let's set the JWT Claims
		   JwtBuilder builder = Jwts.builder().setId(id)
		                               .setIssuedAt(now)
		                               .setSubject(subject)
		                               .setIssuer(issuer)
		                               .claim("name", "nombre")
		                    .claim("scope", "self groups/find")
		                               .signWith(signatureAlgorithm, "secret".getBytes("UTF-8"));
		 
		   //if it has been specified, let's add the expiration
		   if (ttlMillis >= 0) {
		   long expMillis = nowMillis + ttlMillis;
		       Date exp = new Date(expMillis);
		       builder.setExpiration(exp);
		   }
		 
		   //Builds the JWT and serializes it to a compact, URL-safe string
		   String ret = builder.compact();
		   return ret;
	}
	
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String pingTest() {
        return "PING exitoso!";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String consultarRequerimientos(
    		@DefaultValue("") @QueryParam("fecha-vencimiento-inicio") String fechaVencimientoInicio,
            @DefaultValue("") @QueryParam("fecha-vencimiento-fin") String fechaVencimientoFin,
            @DefaultValue("") @QueryParam("id") String identificacion) {
    	int total = 0;
    	StringBuilder sb = new StringBuilder();
    	sb.append("{");
    	
    	if(fechaVencimientoInicio != null && fechaVencimientoInicio.length() != 0) {
    		total++;
    		sb.append("\"fechaVencimientoInicio\":" + fechaVencimientoInicio + "T05:00:01.000Z");
    	}
    	if(fechaVencimientoFin != null && fechaVencimientoFin.length() != 0) {
    		if(total >= 1) {
    			sb.append(',');
    		}
    		
    		total++;
    		sb.append("\"fechaVencimientoFin\":" + fechaVencimientoFin + "T05:00:01.000Z");
    	}
    	if(identificacion != null && identificacion.length() != 0) {
    		if(total >= 1) {
    			sb.append(',');
    		}
    		
    		total++;
    		sb.append("\"identificacion\":" + identificacion);
    	}
    	
    	sb.append('}');
    	
    	if(total == 0) {
    		return pingTest();
    	}
    	
    	System.out.println(sb.toString());
        return consultarRequerimientos(sb.toString());
    }
    
    @POST
    @Consumes(MediaType.TEXT_PLAIN + "; charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public String consultarRequerimientos(String entrada) {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	CredentialsProvider credsProvider = new BasicCredentialsProvider();
    	Credentials defaultcreds = new UsernamePasswordCredentials("cualquiercosa", "secret");
    	String ip = "ip-172-31-9-19.us-west-2.compute.internal";
    	//String ip = "192.168.0.4";
    	credsProvider.setCredentials(new AuthScope(ip, 9000), defaultcreds);
    	String responseBody = "";
    	System.out.println(context.getRemoteAddr());
    	System.out.println(context.getRequestURL());
    	System.out.println(context.getLocalAddr());
        try {
            HttpPost httppost = new HttpPost("http://" + ip + ":9000/Requerimientos/RequerimientoFind");
            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credsProvider);
            StringEntity entity = new StringEntity(entrada);
            httppost.setEntity(entity);
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("token", createJWT("cualquiercosa", " ", "soyyo", 15 * 1000l));
            //httppost.addHeader("token", "AEFBCCBBFFB030491839048101B300D4BD24CB02C484D8CBD230");
            
            System.out.println("Executing request " + httppost.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + response.getStatusLine());
                    }
                }

            };
            //responseBody = httpclient.execute(httppost, responseHandler);
            responseBody = httpclient.execute(httppost, responseHandler, context);
            System.out.println("----------------------------------------");
        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return responseBody;
    }
}