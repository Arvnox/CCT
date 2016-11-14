package com.cct.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/requerimiento")
public class Requerimiento {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String pingTest() {
        return "PING exitoso!";
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response consultarRequerimientos(Object entrada) {
    	Object result = new Object();
        return Response.ok(result).build();
    }
}