package net.teufel.services.db;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DbService {

    @GET
    @Path("/sayHello")
    public Response sayHello() {
        return Response.ok("Hello World JRebel!").build();
    }

}
