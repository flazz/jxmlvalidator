/**
 * 
 */
package edu.fcla.da.xml;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * @author franco
 *
 */
public class WebCacheResolverTest {

	private File sandbox;
	private WebCacheResolver resolver;
	
	@Before
	public void makeSandbox() throws Exception {
		File tempFile = File.createTempFile(getClass().getName(), "testdir");
		if (!tempFile.delete() || !tempFile.mkdir() )
			throw new Exception("Cannot create sandbox at " + tempFile.getAbsolutePath());
			
		sandbox = tempFile;
		Map<String, String> map = new Hashtable<String, String>();
		resolver = new WebCacheResolver(sandbox, map);
	}

	@After
	public void removeSandbox() throws Exception {
		new Runnable() {
			private void rmr(File f) {
				if (f.isDirectory())
					for(File child : f.listFiles())
						rmr(child);
				f.delete();
			}
	
			public void run() {
				rmr(sandbox);
			}
		}.run();
	}


	@Test
	public void publicAndSystem() throws Exception {
		
		String publicId = "http://www.fcla.edu/dls/md/daitss/";
		String systemId = "http://www.fcla.edu/dls/md/daitss/daitss.xsd";
		
		InputSource source = resolver.resolveEntity(publicId, systemId);
		
		assertEquals("public id is wrong", publicId, source.getPublicId());
		assertEquals("system id is wrong", systemId, source.getSystemId());
	}

	@Test
	public void noPublic() throws Exception {
		String systemId = "http://www.fcla.edu/dls/md/daitss/daitss.xsd";
		
		InputSource source = resolver.resolveEntity(null, systemId);		
		assertEquals("public id is wrong", null, source.getPublicId());
		assertEquals("system id is wrong", systemId, source.getSystemId());		
	}
	
	@Test(expected=IOException.class)
	public void noSystem() throws Exception {
		resolver.resolveEntity("http://www.fcla.edu/dls/md/daitss/", null);
	}
	
	@Test
	public void overrideSystemId() throws Exception {				
		String publicId = "http://www.fcla.edu/dls/md/daitss/";
		String systemId = "http://www.fcla.edu/dls/md/daitss/daitss.xsd";
		
		String newSystemId = "http://www.fcla.edu/dls/md/daitss/dev/daitss.xsd";
		resolver.getSystemLocations().put(publicId, newSystemId);
		
		InputSource source = resolver.resolveEntity(publicId, systemId);
		assertEquals("system id is not being mapped", newSystemId, source.getSystemId());

	}
}
