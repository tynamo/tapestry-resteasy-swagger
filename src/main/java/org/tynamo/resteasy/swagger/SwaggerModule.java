package org.tynamo.resteasy.swagger;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.BaseURLSource;
import org.tynamo.resteasy.ResteasyModule;
import org.tynamo.resteasy.ResteasySymbols;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

//import io.swagger.jaxrs.listing.AcceptHeaderApiListingResource;
import io.swagger.v3.jaxrs2.SwaggerSerializers;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@ImportModule(ResteasyModule.class)
public class SwaggerModule
{

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void provideSymbols(MappedConfiguration<String, Object> configuration)
	{
		configuration.add(ResteasySymbols.CORS_ENABLED, true);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons, ApplicationGlobals globals)
	{
		singletons.addInstance(SwaggerSerializers.class);
		singletons.addInstance(OpenApiResource.class);
		singletons.addInstance(AcceptHeaderOpenApiResource.class);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void jacksonJsonProviderSetup(Configuration<Object> singletons)
	{

		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		/**
		 * "publishedDate": 1384267338786, vs "publishedDate": "2013-11-12T14:42:18.786+0000",
		 */
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JacksonJaxbJsonProvider jacksonJsonProvider = new JacksonJaxbJsonProvider();
		jacksonJsonProvider.setMapper(mapper);

		singletons.add(jacksonJsonProvider);
	}

	@SuppressWarnings("rawtypes")
	@Startup
	public static void swagger(javax.ws.rs.core.Application application, BaseURLSource baseURLSource,
		ApplicationGlobals applicationGlobals,
		@Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) String basePackage,
		@Symbol(ResteasySymbols.MAPPING_PREFIX) String restPath,
		@Symbol(SymbolConstants.APPLICATION_VERSION) String version) throws OpenApiConfigurationException
	{
		application.getSingletons(); // EAGER LOADING!!

		OpenAPI openAPI = new OpenAPI();
		Info info = new Info().title(applicationGlobals.getServletContext().getServletContextName());
		openAPI.info(info);
		Server server = new Server();
		server.setUrl(restPath);
		openAPI.servers(Stream.of(server).collect(Collectors.toList()));
		SwaggerConfiguration oasConfig = new SwaggerConfiguration().openAPI(openAPI).prettyPrint(true)
			.resourcePackages(Stream.of(basePackage).collect(Collectors.toSet()));

		// note the last read() is meant to read a configuration file if any exist, see
		// https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Integration-and-configuration#jax-rs-application
		new JaxrsOpenApiContextBuilder().application(application).openApiConfiguration(oasConfig).buildContext(true).read();
	}

	// @Contribute(ClasspathAssetAliasManager.class)
	// public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
	// configuration.add("swagger-ui", "swagger/ui");
	// }

}
