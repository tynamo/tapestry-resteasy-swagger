package org.tynamo.resteasy.swagger;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.services.ApplicationGlobals;

import io.swagger.jaxrs.listing.BaseApiListingResource;

/*
 * Implement a replacement service for swagger's ApiListingResource because it tries to 
 * inject ServletConfig that is not available in T5 (which is filter-based) 
 * https://github.com/swagger-api/swagger-core/issues/2239
 */
@javax.ws.rs.Path("/swagger.{type:json|yaml}")
public class FilterApiListingResource extends BaseApiListingResource {
	private ServletContext context;

	public FilterApiListingResource(ApplicationGlobals globals) {
		this.context = globals.getServletContext();
	}

	@GET
	@Produces({ "application/json", "application/yaml" })
	// @ApiOperation(value = "The swagger definition in either JSON or YAML", hidden = true)
	public Response getListing(@Context Application app, @Context HttpHeaders headers, @Context UriInfo uriInfo,
		@PathParam("type") String type) {
		return StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")
			? this.getListingYamlResponse(app, context, null, headers, uriInfo)
			: this.getListingJsonResponse(app, context, null, headers, uriInfo);
	}
}

//public class FilterApiListingResource {
//
//}