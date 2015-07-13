package com.esri.internal.transitivedependencyidentifier.beans;

import java.util.List;

public class ProductRepository 
{
	private String productName;
	private List<String> repositories;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<String> getRepositories() {
		return repositories;
	}
	public void setRepositories(List<String> repositories) {
		this.repositories = repositories;
	}

}
