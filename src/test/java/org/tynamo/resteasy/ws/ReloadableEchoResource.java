package org.tynamo.resteasy.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;

@Path("/echo")
public interface ReloadableEchoResource
{
	@GET
	@Path("/{message}")
	@Produces("application/json")
	@Operation(description = "echoes a message")
	Response echo(@PathParam("message") String message);
}
