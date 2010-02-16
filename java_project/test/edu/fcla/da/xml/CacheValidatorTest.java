package edu.fcla.da.xml;

import java.io.File;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;


public class CacheValidatorTest extends ValidatorTest {

	private File sandbox;

	@Before
	public void makeSandboxAndValidator() throws Exception {
		File tempFile = File.createTempFile(getClass().getName(), "testdir");
		if (!tempFile.delete() || !tempFile.mkdir() )
			throw new Exception("Cannot create sandbox at " + tempFile.getAbsolutePath());
			
		sandbox = tempFile;
		
		WebCacheResolver resolver = new WebCacheResolver(sandbox, new Hashtable<String, String>());
		validator = new Validator(resolver);
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
	
}
