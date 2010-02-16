/**
 * 
 */
package edu.fcla.da.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebCacheTest {

	private File sandbox;
	private File cacheDir;
	private WebCache cache;
	private URL fclaURL;
	
	@Before
	public void makeSandbox() throws Exception {
		sandbox = File.createTempFile(getClass().getName(), "testdir");
		if (!sandbox.delete() || !sandbox.mkdir() ) {
			throw new Exception("Cannot create sandbox at " + sandbox.getAbsolutePath());
		}			
		
		cacheDir = tempDirectory();
		cache = new WebCache(cacheDir);
		fclaURL = new URL("http://www.fcla.edu/index.html");
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
			
	@Test(expected=IOException.class)
	public void regularFileRoot() throws Exception {
		File root = tempFile();
		new WebCache(root);
	}
	
	@Test(expected=IOException.class)
	public void missingRoot() throws Exception {
		File root = tempMissing();
		new WebCache(root);
	}
	
	@Test
	public void directory() throws Exception {		
		File root = tempDirectory();		
		new WebCache(root);
	}

	
	private File tempFile() throws IOException {
		File f = File.createTempFile(getClass().getName(), "test-file", sandbox);		
		if (!f.isFile()) {
			throw new FileNotFoundException("cannot create temp file: " + f.getAbsolutePath());
		}
		return f;
	}	
	
	private File tempMissing() throws IOException {
		File f = tempFile();		
		f.delete();
		if (f.exists()) {
			throw new FileNotFoundException("cannot create missing file: " + f.getAbsolutePath());
		}
		return f;
	}
	
	private File tempDirectory() throws IOException {
		File f = tempFile();		
		f.delete();
		f.mkdir();
		if (!f.isDirectory()) {
			throw new FileNotFoundException("cannot create temp directory: " + f.getAbsolutePath());
		}
		return f;
	}

	@Test
	public void expired() throws Exception {

		putFileInCache(cacheDir, fclaURL);
		
		cache.get(fclaURL);
		
		// set the expiration, hit it after expiration
		long expiration = 1000;
		System.setProperty(WebCache.EXPIRATION_PROPERTY, Long.toString(expiration));
		Thread.sleep(expiration + 1);
		
		long preTime = cacheTime(cacheDir, fclaURL);
		cache.get(fclaURL);
		long postTime = cacheTime(cacheDir, fclaURL);
		
		assertTrue("last modified time of the cache did not change", preTime < postTime);
	}

	@Test
	public void notExpired() throws Exception {
		
		putFileInCache(cacheDir, fclaURL);
		
		cache.get(fclaURL);		

		// set the expiration, hit it before expiration 
		long expiration = 1000;
		System.setProperty(WebCache.EXPIRATION_PROPERTY, Long.toString(expiration));
		Thread.sleep(0);
		
		long preTime = cacheTime(cacheDir, fclaURL);
		cache.get(fclaURL);
		long postTime = cacheTime(cacheDir, fclaURL);
		
		assertEquals("last modified time of the cache changed", preTime, postTime);
	}	
	
	private long cacheTime(File cacheDir, URL url) {
		String path = url.getAuthority() + url.getPath();
		File f = new File(cacheDir, path);		
		return f.lastModified();
	}
	
	private void putFileInCache(File cacheDir, URL url) throws IOException {
		// make the file & directory from a url
		String path = url.getAuthority() + url.getPath();
		File f = new File(cacheDir, path);
		f.getParentFile().mkdirs();
		
		// streams
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));
		BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
		
		// trasnfer bytes
		int c;
		while( (c = inputStream.read()) > -1 ) {
			outputStream.write(c);
		}
		
		// cleanup
		outputStream.close();
		inputStream.close();
	}

	@Test
	public void compareData() throws Exception {
		
		InputStream expectedStream = new BufferedInputStream(fclaURL.openStream());
		InputStream actualStream = new BufferedInputStream(cache.get(fclaURL));
		int e;
		int a;
		do {
			e = expectedStream.read();
			a = actualStream.read();
			assertEquals("expected data does not match cahched data", e, a);
		} while (e != -1);
		expectedStream.close();
		actualStream.close();		
	}	
	
	@Test(expected=IOException.class)
	public void notFound() throws Exception {
		cache.get("http://www.fcla.edu/foo.bar.baz").close();		
	}

}
