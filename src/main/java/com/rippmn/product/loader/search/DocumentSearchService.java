package com.rippmn.product.loader.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;
import com.rippmn.product.ParsedProduct;

@Service
public class DocumentSearchService {

	public String indexDocs(List<ParsedProduct> products) {

		IndexSpec indexSpec = IndexSpec.newBuilder().setName("testIndex").build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

		for (ParsedProduct prod : products) {
			Document document = Document.newBuilder().setId(prod.getSku())
					.addField(Field.newBuilder().setName("name").setText(prod.getName())).build();

			try {
				final int maxRetry = 3;
				int attempts = 0;
				int delay = 2;
				while (true) {

					try {
						index.put(document);
					} catch (PutException e) {
						if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
								&& ++attempts < maxRetry) { // retrying
							Thread.sleep(delay * 1000);
							delay *= 2; // easy exponential backoff
							continue;
						} else {
							throw e; // otherwise throw
						}
					}
					break;
				}
			} catch (Exception e) {
				return "error";
			}
		}

		return "done";

	}
	
	


}
