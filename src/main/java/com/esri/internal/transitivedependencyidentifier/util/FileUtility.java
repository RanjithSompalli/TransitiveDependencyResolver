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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.esri.internal.transitivedependencyidentifier.application.TransitiveDependencyIdentifier;
import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * @author ranj8168
 * Utility class to perform all the file related operations
 */
public class FileUtility 
{	
	
	
	private static List<String> existingArtifactIds = new ArrayList<String>();
	
	
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
					//writer.write(dependency.getGroupId()+"\t\t"+dependency.getArtifactId()+"\t\t"+dependency.getVersion()+"\t\t"+dependency.isTransitiveDependency()+"\t\t"+dependency.isDuplicate());
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
	public static void parseDependenciesFile(String dependencyFilePath,List<DependencyBean> artifactDependencies, String subModuleName)
	{
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
	            			String artifactId = splittedLine[1];
	            			String versionNumber = splittedLine[3];
	            			if(existingArtifactIds.contains(artifactId))
	            			{
	            				for(DependencyBean bean : artifactDependencies)
	            				{
	            					if(bean.getArtifactId().equals(artifactId))
	            					{
	            						Map<String,List<String>> setVersionToArtifactMappings = bean.getVersionToArtifactMappings();
	            						if(setVersionToArtifactMappings.containsKey(versionNumber))
	            						{
	            							setVersionToArtifactMappings.get(versionNumber).add(subModuleName);
	            						}
	            						else
	            						{
	            							List<String> mappedModuleNames = new ArrayList<String>();
	        		            			mappedModuleNames.add(subModuleName);
	            							setVersionToArtifactMappings.put(versionNumber, mappedModuleNames);
	            							bean.setDuplicate(true);
	            						}
	            					}
	            				}
	            			}
	            			else
	            			{
	            				DependencyBean bean = new DependencyBean();
		            			bean.setGroupId(splittedLine[0].trim());
		            			bean.setArtifactId(artifactId);
		            			List<String> mappedModuleNames = new ArrayList<String>();
		            			mappedModuleNames.add(subModuleName);
		            			Map<String,List<String>> versionArtifactMappings = new LinkedHashMap<String,List<String>>();
		            			versionArtifactMappings.put(versionNumber, mappedModuleNames);
		            			bean.setVersionToArtifactMappings(versionArtifactMappings);
		            			bean.setTransitiveDependency(true);
		            			bean.setDuplicate(false);
		            			artifactDependencies.add(bean);
		            			existingArtifactIds.add(artifactId);
	            			}
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

	
	/**
	 * This method will writes the identified dependencies in the JSON object to an output file
	 * @param JSONObject dependencies in the JSON object format
	 * 
	 */
	public static void writeResultToOutputFile(JSONObject resultInJSONFormat,String outputFilePath)
	{
		FileWriter writer = null;
		try 
		{
			System.out.println("OutputFIlePath:::"+outputFilePath);
			File file = new File(outputFilePath);
			if(!file.exists())
				file.mkdirs();
			File outputFile = new File(outputFilePath+"\\dependencyList_"+MavenUtility.productName+"_"+System.currentTimeMillis()+".json");
			writer = new FileWriter(outputFile);
			//in pretty printable format
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.writeValue(writer, resultInJSONFormat);
			System.out.println("Successfully Copied JSON Object to File...");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();

		} 
		finally 
		{
			try {writer.close();} catch (IOException e) {e.printStackTrace();}
		}	
	}
	
	/**
	 * 
	 * This method writes the output to a HTML file and places it in the given output folder.
	 * 
	 * @param artifactDependencies
	 * @param outputFilePath
	 */
	public static void writeResultToHTMLFile(List<DependencyBean> artifactDependencies, String outputFilePath)
	{
		BufferedWriter writer = null;
		
		try 
		{
			File file = new File(outputFilePath);
			if(!file.exists())
				file.mkdirs();
			File outputFile = new File(outputFilePath+"\\dependencyList_"+MavenUtility.productName+"_"+System.currentTimeMillis()+".html");
			writer = new BufferedWriter(new FileWriter(outputFile)); 
			writer.write("<html><head><title>Duplicate Dependencies</title></head><body><h1>"+MavenUtility.productName+"</h1>");
			writer.write("<table width='100%' border='3'>");
			writer.write("<tr><th>Group ID</th><th>Artifact ID</th><th>Is a Transitive Dependency</th><th>Is a Duplicate Dependency</th><th>Versions</th><th>Sub Modules Used</th></tr>");
			List<DependencyBean> finalListToOutput = new ArrayList<DependencyBean>();
			if(TransitiveDependencyIdentifier.duplicateOnly.equals("true"))
			{
				for(DependencyBean dependency : artifactDependencies)
				{
					if(dependency.isDuplicate())
						finalListToOutput.add(dependency);
				}
			}
			else
				finalListToOutput.addAll(artifactDependencies);
			for(DependencyBean dependency : finalListToOutput)
			{
				Map<String,List<String>> versionArtifactMappings = dependency.getVersionToArtifactMappings();
				writer.write("<tr><td rowspan ='"+versionArtifactMappings.size()+"'>"+dependency.getGroupId()+"</td><td rowspan ='"+versionArtifactMappings.size()+"'>"+dependency.getArtifactId()+"</td><td rowspan ='"+versionArtifactMappings.size()+"'>"+dependency.isTransitiveDependency()+"</td><td rowspan ='"+versionArtifactMappings.size()+"'>"+dependency.isDuplicate()+"</td>");
				for(Map.Entry<String,List<String>> versionArtifactMap : versionArtifactMappings.entrySet())
				{
					writer.write("<td>"+versionArtifactMap.getKey());
					writer.write("<td>");
					for(String subModuleName : versionArtifactMap.getValue())
						writer.write(subModuleName+" , ");
					writer.write("</td></tr><tr>");	
				}
			}
			writer.write("</table>");
			writer.write("</body></html>");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();

		} 
		finally 
		{
			try {writer.close();} catch (IOException e) {e.printStackTrace();}
		}
	}
}
