package AppliedIntegrations.API;

import AppliedIntegrations.API.Parts.AIPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
/**
 * @Author Azazell
 */
public abstract class AIApi {
	protected static AIApi api = null;

	@Nullable
	public static AIApi instance()
	{
		// Have we already retrieved the api?
		if( AIApi.api == null )
		{
			try
			{
				// Attempt to locate the API implementation
				Class clazz = Class.forName("AppliedIntegrations.AppliedIntegrations.AppliedIntegrations");

				// Get the instance method
				Method instanceAccessor = clazz.getMethod( "instance" );

				// Attempt to get the API instance
				AIApi.api = (AIApi)instanceAccessor.invoke( null );
			}
			catch( Exception e )
			{
				// Unable to locate the API, return null
				return null;
			}
		}

		return AIApi.api;
	}




	/**
	 * Cable Parts
	 */
	@Nonnull
	public abstract AIPart parts();


}
