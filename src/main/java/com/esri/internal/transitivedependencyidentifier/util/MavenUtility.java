package com.esri.internal.transitivedependencyidentifier.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

	public static Map<String,List<String>> processPomFile(String clonedFolder) 
	{
		Map<String,List<String>> dependencies= new LinkedHashMap<String,List<String>>();
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
						processPomFile(filePath);
					}
				}
			}
			else
			{
				String sitePath=clonedFolder+"\\target\\site\\dependencies.html";
				System.out.println("Dependency file to be parsed for"+artifactId+" ::"+sitePath);
				dependencies.put(artifactId,new ArrayList<String>());
				dependencies.get(artifactId).add(sitePath);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return dependencies;
	}

	public static boolean buildProjectSite(String clonedFolder) 
	{
		InvocationRequest request = new DefaultInvocationRequest();
    	request.setPomFile( new File(clonedFolder));
    	request.setGoals( Arrays.asList( "clean","site") );

    	Invoker invoker = new DefaultInvoker();
    	invoker.setMavenHome(new File(TransitiveDependencyProjectConstants.MAVENHOME));
    	try 
    	{
			InvocationResult result =invoker.execute( request );
			if(result.getExitCode()!=0)
				return false;			
		} 
    	catch (MavenInvocationException e) 
    	{
			e.printStackTrace();
		}
    	return true;
	}
}
