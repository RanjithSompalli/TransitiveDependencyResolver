package com.esri.internal.transitivedependencyidentifier.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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


/**
 * @author ranj8168
 *
 */
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
		try 
		{
			InvocationRequest request = new DefaultInvocationRequest();
			request.setPomFile( new File(clonedFolder));
			
			File dependencyListFile = File.createTempFile("DependencyList_"+artifactId,".txt");
			String outputFileName = dependencyListFile.getAbsolutePath();
			request.setGoals( Arrays.asList( "dependency:list -DoutputFile="+outputFileName));

			Invoker invoker = new DefaultInvoker();
			invoker.setMavenHome(new File(TransitiveDependencyProjectConstants.MAVENHOME));

			InvocationResult result =invoker.execute( request );
			if(result.getExitCode()!=0)
				return dependencyList;
			dependencyList = FileUtility.parseDependenciesFile(outputFileName);
		}
		
    	catch (MavenInvocationException e) 
    	{
			e.printStackTrace();
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
    	return dependencyList;
	}
	
	/**
	 * This method will compare the dependencies retrieved from the pom using mvn:list command and the direct dependencies retrieved from maven model API and
	 * determines the transitive and direct dependencies
	 * @param directDependencies -- direct dependencies retrieved using maven model API
	 * @param dependencies -- all the list of dependencies retrieved using mvn -dependency:list command
	 */
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
	
	/**
	 * This method will identify the duplicate artifact ids among all the modules of a given repository.
	 * It loops through each and every dependency of each and every sub module and a dependency is identified as duplicate if there exists different versions of same artifact in 
	 * different modules.
	 * 
	 * @param artifactDependenciesMap -- Map containing the modules artifact Id as the key and its corresponding dependencies as the value
	 */
	public static void identifyDuplicateDependencies(Map<String,List<DependencyBean>> artifactDependenciesMap)
	{
		Map<String,String> uniqueArtifactIds = new HashMap<String,String>();
		for(Map.Entry<String,List<DependencyBean>> artifactDependencies : artifactDependenciesMap.entrySet())
		{
			List<DependencyBean> dependencies = artifactDependencies.getValue();
			for(DependencyBean dependency : dependencies)
			{
				String artifactId = dependency.getArtifactId();
				String version = dependency.getVersion();
				if(uniqueArtifactIds.size()==0)
					uniqueArtifactIds.put(artifactId, version);
				if(uniqueArtifactIds.get(artifactId)!=null)
				{
					if(!uniqueArtifactIds.get(artifactId).equalsIgnoreCase(version))
						dependency.setDuplicate(true);
				}
				else
					uniqueArtifactIds.put(artifactId, version);		
			}
		}		
	}
}
