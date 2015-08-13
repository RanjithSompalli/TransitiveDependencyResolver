package com.esri.internal.transitivedependencyidentifier.application;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;
import com.esri.internal.transitivedependencyidentifier.util.FileUtility;
import com.esri.internal.transitivedependencyidentifier.util.GitHubUtility;
import com.esri.internal.transitivedependencyidentifier.util.MavenUtility;

/**
 * This is the class that holds the main method of the application. 
 * 
 * @author ranj8168
 *
 */
public class TransitiveDependencyIdentifier 
{
	
	public static String repositoryMode = null;
	public static String configFilePath = null;
	public static String outputFilePath = null;
	public static Properties configProperties = null;
	public static String duplicateOnly = null;
	/***
	 * This is the starting method of the application. This method will check for the command line arguments. 
	 * 
	 *  @param args
	 * 
	 */
	public static void main(String[] args) 
	{
		//Check for the command line argument and proceed accordingly. local repository mode is considered as the default mode, if no arguments are passed
		processCommandLineArguments(args);
		configProperties = loadConfigPropertiesFile(configFilePath);
		//perform the local repository specific tasks
		if(repositoryMode.equalsIgnoreCase("-local"))
		{
			retrieveTransitiveDependenciesBasedOnLocalRepository();
		}
		//perform the remote repository specific tasks
		else
		{
			retrieveTrasitiveDependenciesBasedOnRemoteRepository();
		}
	}
	
	/***
	 * This method will check for the command line arguments and load the global variables from the arguments. If the arguments are not valid, this displays the information to correct 
	 * 
	 * @param args -- Array of command line arguements
	 */
	private static void processCommandLineArguments(String[] args) 
	{
		if(args.length==4)
		{
			if(args[0].equalsIgnoreCase("-remote") || args[0].equalsIgnoreCase("-local"))
				repositoryMode = args[0];
			if(args[1].contains("-configFilePath"))
				configFilePath = (args[1].split("="))[1];
			if(args[2].contains("-outputFilePath"))
				outputFilePath = (args[2].split("="))[1];
			if(args[3].contains("-duplicateOnly"))
				duplicateOnly = (args[3].split("="))[1];
		}
		if(args.length!=4 || repositoryMode==null || configFilePath==null || outputFilePath == null || duplicateOnly == null)	
		{
			displayErrorMessage();
			System.exit(0);
		}
		
	}
	
	/**
	 * This method displays error message and useful info in case of invalid command line arguments
	 * 
	 */
	private static void displayErrorMessage()
	{
		System.err.println("Please check the command line arguements passed!!!!");
		System.out.println("List of command line arguments expected::::");
		System.out.println("(1) -local/-remote (mandatory argument): indicates the mode of the repository. -local indicates tha the repository is in local directory, -remote indicates to clone the repository from a remote repository");
		System.out.println("(2) -configFilePath=path\\to\\configFile (mandatory argument) : the path to the properties file. This file should contain the path for local or remote repositories, maven installation folder, devtopia username and password");
		System.out.println("For a sample config file read ReadMe.md file");
		System.out.println("(3) -outputFilePath=path\\to\\write\\outputFile (mandatory argument): path to which the output Json file is written." );
		System.out.println("(4) -duplicateOnly=true/false : specifies whether to retrieve only duplicate dependencies or all the dependencies.");
		System.out.println("Example : java -jar transitiveDependencyIdentifier-1.0.0-SNAPSHOT.jar -local -configFilePath=C:\\WorkingDirectory\\projectConfigurations.properties -outputFiklePath=C:\\WorkingDirectory\\output\\dependencyList.json");
		System.out.println("\n\n");
		System.out.println("For more information read ReadMe.md file");
	}
	
	/**
	 * This method loads the configuration properties from the config file path provided through command line arguments 
	 * @param configFilePath
	 * @return Properties object holding the project properties.
	 */
	private static Properties loadConfigPropertiesFile(String configFilePath) 
	{
		Properties props = null;
		FileInputStream in = null;
		if(configFilePath!=null)
		{
			try 
			{
				props = new Properties();
				in = new FileInputStream(configFilePath);
				props.load(in);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}	
		}
		return props;
	}

	/**
	 * 
	 * This method will ask the user for local git repository folder and the build number and retrieve the dependencies accordingly.
	 * In order to use local repository mode the repository should already be cloned into a local folder.
	 */
	private static void retrieveTransitiveDependenciesBasedOnLocalRepository()
	{	
		String localRepositoryPath = configProperties.getProperty(TransitiveDependencyProjectConstants.LOCALREPOSITORYPATHPROPERTY);
		if(localRepositoryPath!=null)
		{
			//perform the maven specific operations : Identifies the dependencies for each artifact, determine the transitive and direct dependencies
			performMavenOperations(localRepositoryPath);
		}
		else
			System.err.println("Unable to load local repository path from config file");
	}
	
	/**
	 * 
	 * This method will retrieve the available products from the productProperties.properties file and asks for user selection and build number.
	 * It then calls the git hub utility to clone the remote repository to local folder and perform the maven operations.
	 *
	 */
	private static void retrieveTrasitiveDependenciesBasedOnRemoteRepository()
	{
		
		String remoteRepositoryPath = configProperties.getProperty(TransitiveDependencyProjectConstants.REMOTEREPOSITORYPATHPROPERTY);
		if(remoteRepositoryPath!=null)
		{
			String clonedFilePath = GitHubUtility.cloneRepositoryBasedOnBranch(remoteRepositoryPath);
			performMavenOperations(clonedFilePath);
		}
		else
			System.err.println("Unable to load remote repository path from config file");
	}

	/**
	 * THis method will perform all the maven related operations by calling various methods in the MavenUtility class.
	 * 
	 * 
	 * @param mavenProjectFilePath
	 */
	private static void performMavenOperations(String mavenProjectFilePath)
	{
		List<DependencyBean> artifactDependencies = new ArrayList<DependencyBean>();
		MavenUtility.processPomFiles(mavenProjectFilePath,artifactDependencies);
		
		//Write the output to html file
		FileUtility.writeResultToHTMLFile(artifactDependencies,outputFilePath);
		
		//clear all the temp directories
		File tempDependencyListFile = new File(MavenUtility.tempDirectoryPath);
		FileUtility.deleteDir(tempDependencyListFile);
		tempDependencyListFile.delete();	
	}	
}

