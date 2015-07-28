/**
 * 
 */
package com.esri.internal.transitivedependencyidentifier.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;

/**
 * @author ranj8168
 * Utility class to perform all the file related operations
 */
public class FileUtility 
{
	
	/**
	 * Utility method to read all the available products from the properties file and returns the list of git repositories 
	 * for a selected product.
	 * */
	public static List<String> readRepositoriesFromPropertiesFile() 
	{
		List<String> repositories = new ArrayList<String>();
		Properties props = new Properties();
		FileInputStream in = null;
		Scanner scanner = new Scanner(System.in);
		try 
		{
			in = new FileInputStream(TransitiveDependencyProjectConstants.PRODUCTREPOSITORYFILEPATH);
			props.load(in);
			System.out.println("Please select a product from the list:");
			for(Object product:props.keySet())
			{
				System.out.println(product);
			}
			System.out.println("\n\n");
			System.out.println("Select a Product::");
			String selectedProduct = scanner.next();
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
			try {in.close();} catch (IOException e) {e.printStackTrace();}
		}
		return repositories;
	}
	
	/**
	 * This method will writes the identified dependencies to a temporary output file
	 * @param artifactDependenciesMap -- Map that holds the final dependencies list
	 * @return String -- path of the output file
	 */
	public static String writeOutputToFile(Map<String,List<DependencyBean>> artifactDependenciesMap)
	{
		Writer writer = null;
		File file = null;
		try 
		{
			file = File.createTempFile("DependencyList_","");
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
		    for(Map.Entry<String, List<DependencyBean>> artifactDependencyPair : artifactDependenciesMap.entrySet())
			{
		    	writer.write("Artifact Name:::"+artifactDependencyPair.getKey());
		    	writer.write("\n\n");
				List<DependencyBean> dependenciesRetrieved = artifactDependencyPair.getValue();
				writer.write("groupID"+"\t\t"+"artifactID"+"\t\t"+"version"+"\t\t"+"isTransitive"+"\t\t"+"isDuplicate");
				writer.write("\n\n");
				for(DependencyBean dependency : dependenciesRetrieved)
				{
					writer.write(dependency.getGroupId()+"\t\t"+dependency.getArtifactId()+"\t\t"+dependency.getVersion()+"\t\t"+dependency.isTransitiveDependency()+"\t\t"+dependency.isDuplicate());
					writer.write("\n");
				}
				writer.write("\n\n");
			}
		} 
		catch (IOException e) 
		{
		  e.printStackTrace();
		} 
		finally 
		{
		   try {writer.close();} catch (Exception ex) {}
		}
		return file.getAbsolutePath();
	}
	
	/**
	 * This method parses the dependencies list file and populates the DependencyBean objects
	 * @param dependencyFilePath -- Path to the dependencyList file
	 * @return a List of DependencyBean objects
	 */
	public static List<DependencyBean> parseDependenciesFile(String dependencyFilePath)
	{
		List<DependencyBean> dependencies = new ArrayList<DependencyBean>();
		try
		{
	        FileInputStream fstream = new  FileInputStream(dependencyFilePath);
	        DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine;
	        //Read File Line By Line
	        while ((strLine = br.readLine()) != null)   
	        {
	            if(strLine.length()>0)
	            {
	            	String[] splittedLine = strLine.split(":");
	            	if(splittedLine.length>1)
	            	{
	            		String dependencyScope = splittedLine[4];
	            		if(!dependencyScope.equalsIgnoreCase("test"))
	            		{
	            			DependencyBean bean = new DependencyBean();
	            			bean.setGroupId(splittedLine[0].trim());
	            			bean.setArtifactId(splittedLine[1]);
	            			bean.setVersion(splittedLine[3]);
	            			bean.setTransitiveDependency(true);
	            			bean.setDuplicate(false);
	            			dependencies.add(bean);
	            		}
	            	}	
	            }  
	        }
	        in.close();
	    }
		catch (Exception e)
		{
	        e.printStackTrace();
	    }
		return dependencies;
	}
	/**
	 * This method recursively deletes the entire directory structure provided as the parameter 
	 * @param dir -- File reference of the file to be deleted.
	 */
	public static void deleteDir(File dir) 
	{
	    File[] files = dir.listFiles();
	    for (File myFile: files) 
	    {
	    	if (myFile.isDirectory()) 
	    	{  
	    		deleteDir(myFile);
	    	} 
	    	myFile.delete();
	    }
	}
}
