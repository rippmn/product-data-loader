package com.rippmn.product.loader.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.stereotype.Repository;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.KeyFactory;
import com.rippmn.product.ParsedProduct;

@Repository
public class ProductSaver {

	public void persistProducts(List<ParsedProduct> products) {
		
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	    
		String kind = "productNameSearch";
	    
		KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		for(ParsedProduct prod: products) {
			
			Entity.Builder builder = Entity.newBuilder(keyFactory.newKey(prod.getSku()));
			
		    builder.set("name", prod.getName());
		    
		    //parse the product name into two word phrases
		    addSearchPhrases(builder, prod.getName());
		            
		    entities.add(builder.build());
			
		}
		
		
		datastore.put(entities.toArray(new Entity[entities.size()]));
		
	}
	
	private void addSearchPhrases(Builder builder, String productName) {
		
		StringTokenizer st = new StringTokenizer(productName.toLowerCase());
		
		ArrayList<String> phrases = new ArrayList<String>();
		
		String lastToken = null;
		String middleToken = null;
		String phrase = null;
		while(st.hasMoreTokens()) {
			
			
			if(phrase == null) {
				phrase = st.nextToken();
				middleToken = getNextString(st);
				lastToken = getNextString(st);
			}
			
			phrases.add(phrase.concat(" ").concat(middleToken).concat(" ").concat(lastToken));
			phrase = middleToken;
			middleToken = lastToken;
			lastToken = getNextString(st);
			
			
		}
		
		if(!phrase.contains(lastToken)) {
			phrases.add(phrase.concat(" ").concat(middleToken).concat(" ").concat(lastToken));
			phrases.add(middleToken.concat(" ").concat(lastToken));
			phrases.add(lastToken);
		}else if (phrase.equals(lastToken)) {
			phrases.add(lastToken);
		}
		
		System.out.println(phrases);
		
		
		if(phrases.size() == 1) {
			builder.set("tag", phrases.get(0));
		}else if(phrases.size() == 2) {
			builder.set("tag", phrases.get(0), phrases.get(1));
		}else if(phrases.size() > 2){
			builder.set("tag", phrases.get(0), phrases.get(1), phrases.subList(2, phrases.size()).toArray(new String[phrases.size()-2]));
		}
		
	}
	
	private String getNextString(StringTokenizer st) {
		
		String theString = "";
		
		if(st.hasMoreTokens()) {
			theString = st.nextToken();
		}
		
		while(theString.length() == 1) {
			if(!Character.isLetterOrDigit(theString.charAt(0))) {
				theString = st.nextToken();
			} else {
				break;
			}
			
		}
		return theString;
		
	}

}
