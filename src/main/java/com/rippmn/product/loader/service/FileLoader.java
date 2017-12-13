package com.rippmn.product.loader.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.rippmn.product.ParsedProduct;
import com.rippmn.product.ProductCategory;
import com.rippmn.product.loader.persistence.ProductSaver;

@Service
public class FileLoader {

	private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
			.initialRetryDelayMillis(10).retryMaxAttempts(10).totalRetryPeriodMillis(15000).build());

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;

	@Autowired
	private ProductSaver prodRepo;
	
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

	public String fileRead() throws IOException {
		GcsFilename fileName = new GcsFilename("product-rippmn", "products-batteries.json");
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		InputStream in = Channels.newInputStream(readChannel);

		StringBuffer sb = new StringBuffer();

		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = in.read(buffer);

		while (bytesRead != -1) {
			sb.append(new String(buffer));
			bytesRead = in.read(buffer);
		}

		in.close();

		return sb.toString();
	}


	public String loadProducts(String loadFileName) throws IOException {
		GcsFilename fileName = new GcsFilename("product-rippmn", loadFileName);
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		InputStream in = Channels.newInputStream(readChannel);

		int prds = parseProducts(in);
		return "Loaded " +prds+ " products";
	
	}
	
	public String loadProducts() throws IOException {
		return loadProducts("products-batteries.json");
	}

	

	private int parseProducts(InputStream in) throws JsonParseException, IOException{
	
	JsonFactory factory = new JsonFactory();

	JsonParser parser = factory.createParser(in);
	
	JsonToken token;
	int products = 0;

	ArrayList<ParsedProduct> prodSaveList = new ArrayList<ParsedProduct>();

	while(!parser.isClosed())
	{
		token = parser.nextToken();

		// if its the last token then we are done
		if (token == null)
			break;

		if (JsonToken.START_ARRAY.equals(token)) {
			// for now we are skipping nested object arrays
			continue;
		} else if (JsonToken.START_OBJECT.equals(token)) {
			// create the product
			products++;
			// System.out.println(createProduct(parser));
			prodSaveList.add(createProduct(parser));
			if (products % 10 == 0) {
				//System.out.println("save " + prodSaveList.size());
				//TestGCDS.persistProducts(prodSaveList);
				prodRepo.persistProducts(prodSaveList);
				prodSaveList = new ArrayList<ParsedProduct>();
			}
		}
	}

	if(prodSaveList.size()>0)
	{
		//System.out.println("save rest - " + prodSaveList.size());
		prodRepo.persistProducts(prodSaveList);
	}

	return products;

}

private ParsedProduct createProduct(JsonParser parser) throws IOException {

	JsonToken token = parser.nextToken();

	ParsedProduct parsedProduct = new ParsedProduct();

	// now loop thru tokens to find product info
	while (!JsonToken.END_OBJECT.equals(token)) {

		if (JsonToken.START_ARRAY.equals(token) && parser.getCurrentName().equals("category") ) {
			ProductCategory category = null;
			while (!JsonToken.END_ARRAY.equals(token)) {
				
				if(JsonToken.START_OBJECT.equals(token)) {
					category = new ProductCategory();
					parsedProduct.getCategories().add(category);
				}
				
				if (token.name().startsWith("VALUE")) {
					String text = parser.getText();
					switch (parser.getCurrentName()) {
					case "id":
						category.setId(text);
						break;
					case "name":
						category.setName(text);
					default:
						break;
					}
				}
				token = parser.nextToken();
			}
		} else if (token.name().startsWith("VALUE")) {
			String text = parser.getText();
			
			switch (parser.getCurrentName()) {
			case "sku":
				parsedProduct.setSku(text);
				break;
			case "name":
				parsedProduct.setName(text);
				break;
			case "price":
				parsedProduct.setPrice(parser.getDoubleValue());
				break;
			case "shipping":
				if(text != null && text.length() >0)
					parsedProduct.setShipping(parser.getDoubleValue());
				break;
			case "upc":
				parsedProduct.setUpc(text);
				break;
			case "description":
				parsedProduct.setDescription(text);
				break;
			case "manufacturer":
				parsedProduct.setManufacturer(text);
				break;
			case "model":
				parsedProduct.setModel(text);
				break;
			case "url":
				parsedProduct.setUrl(text);
				break;
			case "image":
				parsedProduct.setImage(text);
				break;
			case "type":
				parsedProduct.setType(text);
				break;
			default:
				break;
			}
			
		}

		token = parser.nextToken();
	}
			
	return parsedProduct;
}

}
