package com.rippmn.product.loader.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

@Service
public class FileLoader {

	private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
			.initialRetryDelayMillis(10).retryMaxAttempts(10).totalRetryPeriodMillis(15000).build());

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;

	public void saveFile() throws IOException {

		OutputStream out = null;
		try {
			GcsFileOptions instance = GcsFileOptions.getDefaultInstance();

			GcsFilename fileName = new GcsFilename("rippmn-test", "lastRun");
			GcsOutputChannel outputChannel;
			outputChannel = gcsService.createOrReplace(fileName, instance);
			out = Channels.newOutputStream(outputChannel);
			out.write(new Date().toString().getBytes());
			out.flush();
		} finally {
			out.close();
		}

	}

	public String fileInfo() throws IOException{
		GcsFilename fileName = new GcsFilename("rippmn-test", "products-batteries.json");
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		InputStream in = Channels.newInputStream(readChannel);

		StringBuffer sb = new StringBuffer();
		
		byte[] buffer = new byte[BUFFER_SIZE];
	    int bytesRead = in.read(buffer);
	    
	    
	    
	    while(bytesRead != -1){
	    	sb.append(new String(buffer));
	    	bytesRead = in.read(buffer);
	    }
	    
	    in.close();

		return sb.toString();
	}
}
