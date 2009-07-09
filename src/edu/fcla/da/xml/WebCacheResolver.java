package edu.fcla.da.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolve http entities from the local WebCache
 * 
 * @author franco
 * 
 */
public class WebCacheResolver implements EntityResolver {

	private WebCache cache;
	
	private Map<String, String> systemLocations;


	/**
	 * @param webCacheRoot	the directory where the cache is stored.
	 * @param locations	a map of publicIds to systemIds that will override a specified systemId in a document for a respective publicId.
	 * @throws IOException 
	 */
	public WebCacheResolver(File webCacheRoot, Map<String, String> locations) throws IOException {
		cache = new WebCache(webCacheRoot);
		systemLocations = new Hashtable<String, String>();
		systemLocations.putAll(locations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
	 *      java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		
		// special cases for public Id		
		if ( publicId != null 
				&& systemLocations.containsKey(publicId) 
				&& !systemLocations.get(publicId).equals(systemId)) {
			systemId = systemLocations.get(publicId);
		}			

		if(systemId == null) throw new IOException("Cannot determine a resolution to public id " + publicId + " with system id " + systemId );
		
		try {
			if (isRemote(systemId)) {
				InputStream is = cache.get(systemId);

				// return the stream as an input source.
				InputSource source = new InputSource(is);
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				return source;
			}
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage());
		}

		// return null, and tell the document builder to use its own resolver.
		return null;
	}

	/**
	 * @param systemId
	 * @return true if the systemId references a http location.
	 * @throws URISyntaxException 
	 * @throws URISyntaxException
	 */
	private boolean isRemote(String systemId) throws URISyntaxException {
		return new URI(systemId).getScheme().equals("http");	
	}

	public Map<String, String> getSystemLocations() {
		return systemLocations;
	}

	public void setSystemLocations(Map<String, String> systemLocations) {
		this.systemLocations = systemLocations;
	}

}
