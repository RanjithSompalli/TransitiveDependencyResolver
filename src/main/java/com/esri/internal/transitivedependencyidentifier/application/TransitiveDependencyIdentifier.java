package com.esri.internal.transitivedependencyidentifier.application;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;
import com.esri.internal.transitivedependencyidentifier.util.GitHubUtility;
import com.esri.internal.transitivedependencyidentifier.util.MavenUtility;

public class TransitiveDependencyIdentifier 
{
	
	public static void main(String[] args) 
	{
		/** Read the list of repositories available for the product selected*/
		List<String> repositories = readRepositoriesFromPropertiesFile();
		//Check if repositories returned are not null or not empty
		if(repositories!=null && repositories.size()>0)
		{	
			//for each repository: invoke the git hub utility
			for(String repository : repositories)
			{
				String clonedFileName = GitHubUtility.cloneRepositoryBasedOnBranch(repository);
				//String clonedFileName = "C:\\Users\\ranj8168\\AppData\\Local\\Temp\\1\\tempCloneFolder2672907876374116410"; 
				System.out.println("Cloned File name::"+clonedFileName);
				Map<String,List<DependencyBean>> artifactDependenciesMap = new LinkedHashMap<String,List<DependencyBean>>();
				MavenUtility.processPomFiles(clonedFileName,artifactDependenciesMap);
				for(Map.Entry<String, List<DependencyBean>> artifactDependencyPair : artifactDependenciesMap.entrySet())
				{
					System.out.println("Artifact Name:::"+artifactDependencyPair.getKey());
					List<DependencyBean> dependenciesRetrieved = artifactDependencyPair.getValue();
					System.out.println("groupID"+"\t"+"artifactID"+"\t"+"version"+"\t"+"isTransitive"+"\t"+"isDuplicate");
					for(DependencyBean dependency : dependenciesRetrieved)
					{
						System.out.println(dependency.getGroupId()+"\t"+dependency.getArtifactId()+"\t"+dependency.getVersion()+"\t"+dependency.isTransitiveDependency()+"\t"+dependency.isDuplicate());
					}
				}
				break;
			}
		}
		else
		{
			System.err.println("Failed to retrive available products!!!!");
		}
	}

	/**
	 * Utility method to read all the available products from the properties file and returns the list of git repositories 
	 * for a selected product.
	 * */
	private static List<String> readRepositoriesFromPropertiesFile() 
	{
		List<String> repositories = new ArrayList<String>();
		Properties props = new Properties();
		FileInputStream in = null;
		Scanner reader = new Scanner(System.in);
		try 
		{
			in = new FileInputStream(TransitiveDependencyProjectConstants.PRODUCTREPOSITORYFILEPATH);
			props.load(in);
			System.out.println("Please select a product from the list:");
			for(Object product:props.keySet())
			{
				System.out.println(product);
			}
			String selectedProduct = reader.next();
			if(props.containsKey(selectedProduct))
				repositories.addAll(Arrays.asList(props.getProperty(selectedProduct).split(",")));
			else
				System.err.println("Selected product is not valid!!!");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				in.close();
				reader.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return repositories;
	}
}

