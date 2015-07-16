package com.esri.internal.transitivedependencyidentifier.application;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.esri.internal.transitivedependencyidentifier.beans.ProductRepository;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;
import com.esri.internal.transitivedependencyidentifier.util.GitHubUtility;
import com.esri.internal.transitivedependencyidentifier.util.MavenUtility;
import com.esri.internal.transitivedependencyidentifier.util.XMLUtility;

public class TransitiveDependencyIdentifier 
{
	public static void main(String[] args) 
	{
		List<ProductRepository> availableProductRepositories = XMLUtility.getAvailableProductsFromXML();
		List<String> repositoryLinks = new ArrayList<String>();
		Scanner reader = new Scanner(System.in);
		if(availableProductRepositories!=null && availableProductRepositories.size()>0)
		{
			System.out.println("###### Available Products List #####");
			for(ProductRepository productRepository : availableProductRepositories)
			{
				System.out.println(productRepository.getProductName());
			}
			System.out.println("Please enter a product:");
			String selectedProduct = reader.next();
			for(ProductRepository productRepository : availableProductRepositories)
			{
				if(productRepository.getProductName().equalsIgnoreCase(selectedProduct))
					repositoryLinks.addAll(productRepository.getRepositories());
			}
			System.out.println("Enter a build number:");
			String buildNum = reader.next();
			for(String repositoryLink : repositoryLinks)
			{
				String clonedFolder = GitHubUtility.cloneRepositoryBasedOnBranch(repositoryLink,buildNum);
				MavenUtility.buildProjectSite(clonedFolder);
				Map<String,List<String>> dependencies = MavenUtility.processPomFile(clonedFolder);
				for(Map.Entry<String, List<String>> dependency : dependencies.entrySet())
				{
					System.out.println("Artifact Name:::"+dependency.getKey());
					System.out.println("Site to be traversed:::"+dependency.getValue().get(0));
				}
			}
		}
		else
		{
			System.err.println("Failed to retrive available products!!!!");
		}
		
		reader.close();
	}

}

