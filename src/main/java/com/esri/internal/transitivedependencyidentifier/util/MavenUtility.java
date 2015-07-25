package com.esri.internal.transitivedependencyidentifier.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;


public class MavenUtility 
{

	/**
	 * This method will first parse the pom file present in the clonedFolder passed as an param.
	 * If the packaging type of the pom file is pom, then it gets its sub modules and recursively 
	 * parse the pom files of sub modules.
	 * If the packaging type is war or jar, it calls the getDependencyList() method to get the list of 
	 * transitive dependencies for that artifact.
	 * 
	 * @param clonedFolder -- name of the folder in which the repository is cloned to.
	 * @return -- A map that has the all the atifact Ids as keys and their corresponding transitive dependencies as values
	 */
	public static void processPomFiles(String clonedFolder,Map<String,List<DependencyBean>> artifactDependenciesMap) 
	{
		try 
		{
			MavenXpp3Reader mavenreader = new MavenXpp3Reader();
			File pomfile = new File(clonedFolder+"\\pom.xml");
			Model model= mavenreader.read(new FileReader(pomfile));
			
			String artifactId = model.getArtifactId();
			String modelPackagingType = model.getPackaging();
			if(modelPackagingType.equalsIgnoreCase("pom"))
			{	
				List<String> modules = model.getModules();
				if(modules!=null && modules.size()>0)
				{
					for(String module : modules)
					{
						System.out.println("Module name::"+module);
						String filePath = clonedFolder+"\\"+module;
						processPomFiles(filePath,artifactDependenciesMap);
					}
				}
				//If there are no sub modules and packaging type is pom, then it is a set up pom.
				else
				{
					List<DependencyBean> dependencies = generateDependencyList(artifactId,clonedFolder);
					List<Dependency> directDependencies = model.getDependencies();
					deriveTransitiveDependencies(directDependencies,dependencies);
					artifactDependenciesMap.put(artifactId,dependencies);
				}
			}
			else
			{
				//If the pom packaging is of type war or jar, retrieve the depencies
				List<DependencyBean> dependencies = generateDependencyList(artifactId,clonedFolder);
				List<Dependency> directDependencies = model.getDependencies();
				deriveTransitiveDependencies(directDependencies,dependencies);
				artifactDependenciesMap.put(artifactId,dependencies);
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method gets the folder path in which the pom file exists, it then generates the list of all the transitive dependencies as 
	 * List of DependencyBeans. Each Dependency bean holds information like groupId, artifactId, version, isTransitiveDependency, isDuplicateDependency
	 *  
	 * @param clonedFolder -- folder that has the pom file
	 * @return List<DependencyBean> -- a list of DependencyBean objects that holds the information of dependencies of this artifact
	 */
	public static List<DependencyBean> generateDependencyList(String artifactId,String clonedFolder) 
	{
		List<DependencyBean> dependencyList = null;
		InvocationRequest request = new DefaultInvocationRequest();
    	request.setPomFile( new File(clonedFolder));
    	String outputFileName = "C:\\WorkingDirectory\\DependencyLists\\DependencyList_"+artifactId+".txt";
    	request.setGoals( Arrays.asList( "dependency:list -DoutputFile="+outputFileName));

    	Invoker invoker = new DefaultInvoker();
    	invoker.setMavenHome(new File(TransitiveDependencyProjectConstants.MAVENHOME));
    	try 
    	{
			InvocationResult result =invoker.execute( request );
			if(result.getExitCode()!=0)
				return dependencyList;
			dependencyList = parseDependenciesFile(outputFileName);
		} 
    	catch (MavenInvocationException e) 
    	{
			e.printStackTrace();
		}
    	return dependencyList;
	}
	
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
	            			bean.setGroupId(splittedLine[0]);
	            			bean.setArtifactId(splittedLine[1]);
	            			bean.setVersion(splittedLine[3]);
	            			bean.setTransitiveDependency(true);
	            			bean.setDuplicate(false);
	            			dependencies.add(bean);
	            		}
	            	}	
	            }  
	        }
	        //Close the input stream
	        in.close();
	    }
		catch (Exception e)
		{//Catch exception if any
	        e.printStackTrace();
	    }
		return dependencies;
	}
	
	public static void deriveTransitiveDependencies(List<Dependency> directDependencies, List<DependencyBean> dependencies)
	{
		for(DependencyBean dependency : dependencies)
		{
			for(Dependency directDependency : directDependencies)
			{
				if(directDependency.getArtifactId().equalsIgnoreCase(dependency.getArtifactId()))
					dependency.setTransitiveDependency(false);
			}
		}	
	}
}
