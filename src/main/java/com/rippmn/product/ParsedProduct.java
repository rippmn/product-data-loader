package com.rippmn.product;

import java.util.ArrayList;
import java.util.List;

public class ParsedProduct {
	private String sku;
	private String name;
	private String type;
	private Double price;
	private String upc;
	private String description;
	private Double shipping;
	private String manufacturer;
	private String model;
	private String url;
	private String image;
	private List<ProductCategory> categories = new ArrayList<ProductCategory>();
	
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getShipping() {
		return shipping;
	}
	public void setShipping(Double shipping) {
		this.shipping = shipping;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public List<ProductCategory> getCategories() {
		return categories;
	}
	public void setCategories(List<ProductCategory> categories) {
		this.categories = categories;
	}
	@Override
	public String toString() {
		return "Product [sku=" + sku + ", name=" + name + ", type=" + type + ", price=" + price + ", upc=" + upc
				+ ", description=" + description + ", shipping=" + shipping + ", manufacturer=" + manufacturer
				+ ", model=" + model + ", url=" + url + ", image=" + image + ", categories=" + categories + "]";
	}
	
}
