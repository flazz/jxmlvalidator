package edu.fcla.da.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author franco
 * 
 */
public class WebCache {

	/**
	 * The System Property that determines the expiration of the webcache in milliseconds, the default is set to 1 hour.
	 */
	public static final String EXPIRATION_PROPERTY = "edu.fcla.webcache.expiration";
	
	private static final long EXPIRATION_DEFAULT = 1000 * 60 * 60 * 24 * 7;
		
	private File root;

	private static long expiration() {
	    Long expr;
	    if (System.getProperty(EXPIRATION_PROPERTY) != null) {
	        String property = System.getProperty(EXPIRATION_PROPERTY);
	        expr = Long.parseLong(property);
	    } else {
	        expr = EXPIRATION_DEFAULT;
	    }
	    
	    return expr;
	}
	
	/**
	 * Constructs a WebCache rooted at the directory specified by the daitss
	 * property WEBCACHE_DIR
	 * 
	 * @param root	the root of the webcache.
	 * @throws IOException 
	 * @throws FatalException
	 */
	public WebCache(File root) throws IOException {
		
		if(!root.exists()) {
			throw new FileNotFoundException(root.toString());
		}
		
		if(!root.isDirectory()) {
			throw new IOException(root + " is not a directory");
		}
		
		this.root = root;
	}

	/**
	 * @param url
	 * @return an input stream to the cached copy of the data located at the
	 *         url.
	 * @throws IOException
	 */
	public InputStream get(URL url) throws IOException {
		File f = convertUrlToFile(url); 
		boolean expired = f.lastModified() + expiration() < System.currentTimeMillis();;
		boolean missing = !f.exists();
		synchronized (f) {
			if (missing || expired) {
				downloadUrl(url, f);
			}			
		}

		FileInputStream inputStream = new FileInputStream(f);
		return inputStream;
	}

	/**
	 * @param urlString a String representation of a URL.
	 * @return an input stream to the cached copy of the data located at the url.
	 * @throws IOException
	 */
	public InputStream get(String urlString) throws IOException {
		URL url = new URL(urlString);
		return get(url);
	}

	private void downloadUrl(URL url, File file) throws IOException {

		// make the directory tree for this path
		file.getParentFile().mkdirs();

		// download the file
		BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

		int b;
		while ((b = inputStream.read()) != -1) {
			outputStream.write(b);
		}

		inputStream.close();
		outputStream.close();
	}

	private File convertUrlToFile(URL url) {
		String path = url.getAuthority() + url.getPath();
		return new File(root, path);
	}
	
	

}
