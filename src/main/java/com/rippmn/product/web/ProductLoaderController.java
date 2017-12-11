package com.rippmn.product.web;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductLoaderController {

	private static Logger log = LoggerFactory.getLogger(ProductLoaderController.class);
	
	@RequestMapping("/loadProducts")
	public String loadProducts() {
		log.info("running daily product load");
		return "completed at:" + new Date().toString();
	
	}

}
