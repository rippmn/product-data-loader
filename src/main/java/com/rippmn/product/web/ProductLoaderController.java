package com.rippmn.product.web;


import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rippmn.product.loader.service.FileLoader;

@RestController
public class ProductLoaderController {

	private static Logger log = LoggerFactory.getLogger(ProductLoaderController.class);
	
	@Autowired
	private FileLoader loader;
	
	@RequestMapping("/loadProducts")
	public String loadProducts() throws IOException {
		log.info("running daily product load");
		loader.loadProducts();
		return "completed at:" + new Date().toString();
	
	}
	
	@RequestMapping("/readProds")
	public String readProducts() throws IOException {
		log.info("reading daily product load file");
		
		return loader.fileRead();
	
	}
	
	@RequestMapping("/loadFromNamedFile")
	public String loadProducts(@RequestParam("fileName")String fileName) throws IOException {
		log.info("reading product load file from");
		
		String saveMsg = loader.loadProducts(fileName);
		return saveMsg + " completed at:" + new Date().toString();
	
	}
	
}
