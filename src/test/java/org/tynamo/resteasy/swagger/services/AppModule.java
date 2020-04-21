package org.tynamo.resteasy.swagger.services;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.tynamo.resteasy.ResteasyModule;
import org.tynamo.resteasy.ResteasyPackageManager;
import org.tynamo.resteasy.ResteasySymbols;
import org.tynamo.resteasy.swagger.SwaggerModule;

@ImportModule({ ResteasyModule.class, SwaggerModule.class })
public class AppModule
{

	public static void bind(ServiceBinder binder)
	{
	}


	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy singleton services here.
	 * <p/>
	 *
	 */
	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons
	// ,ReloadableEchoResource reloadableEchoResource
	)
	{
		// singletons.add(reloadableEchoResource);
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void provideSymbols(MappedConfiguration<String, String> configuration)
	{
		// note that we want to
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/mycustomresteasyprefix");
		configuration.add(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "org.tynamo.resteasy");
		configuration.add(InternalSymbols.APP_PACKAGE_PATH, "org/tynamo/resteasy");
	}

	@Contribute(ResteasyPackageManager.class)
	public static void resteasyPackageManager(Configuration<String> configuration)
	{
		// configuration.add("org.tynamo.resteasy.ws.autobuild");
	}

}