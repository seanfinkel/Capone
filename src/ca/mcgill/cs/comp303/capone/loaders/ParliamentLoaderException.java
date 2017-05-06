package ca.mcgill.cs.comp303.capone.loaders;

/**
 * Denotes a problem with the building of an model object. CSOFF:
 */
@SuppressWarnings("serial")
public class ParliamentLoaderException extends RuntimeException
{
	public ParliamentLoaderException() {}
	public ParliamentLoaderException(String pMessage) { super(pMessage); }
	public ParliamentLoaderException(Throwable pThorwable) { super(pThorwable); }
	public ParliamentLoaderException(String pMessage, Throwable pThrowable) { super(pMessage, pThrowable); }
}
