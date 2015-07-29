package com.esri.internal.transitivedependencyidentifier.application;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;
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
	
	
	/***
	 * This is the starting method of the application. This method will check for the command line arguments. 
	 * If an argument -L is passed, application will run on the provided local repository.
	 * If an argument -R is passed, application will display the available products in the remote repository and clones the repository based on the user selection.
	 * 
	 *  @param args
	 * 
	 */
	public static void main(String[] args) 
	{
		//Check for the command line argument and proceed accordingly. local repository mode is considered as the default mode, if no arguments are passed
		String repositoryMode = "remote";
		if(args.length>0 && args[0].equalsIgnoreCase("-L"))
			repositoryMode = "local";
		
		//perform the local repository specific tasks
		if(repositoryMode.equalsIgnoreCase("local"))
		{
			retrieveTransitiveDependenciesBasedOnLocalRepository();
		}
		//perform the remote repository specific tasks
		else
		{
			retrieveTrasitiveDependenciesBasedOnRemoteRepository();
		}
	}
	
	/**
	 * 
	 * This method will ask the user for local git repository folder and the build number and retrieve the dependencies accordingly.
	 * In order to use local repository mode the repository should already be cloned into a local folder.
	 */
	private static void retrieveTransitiveDependenciesBasedOnLocalRepository()
	{
		System.out.println("Please provide the Full path of Local repositoy:");
		Scanner scanner = new Scanner(System.in);
		String localRepositoryPath = scanner.next(); 
		System.out.println("Please provide the build number(Eg:5225):");
		String buildNum = scanner.next();
		//Checkout to the corresponding build in the local repository
		GitHubUtility.checkOutToBuildNumber(localRepositoryPath,buildNum);
		
		//perform the maven specific operations : Identifies the dependencies for each artifact, determine the transitive and direct dependencies
		performMavenOperations(localRepositoryPath);
		scanner.close();	
	}
	
	/**
	 * 
	 * This method will retrieve the available products from the productProperties.properties file and asks for user selection and build number.
	 * It then calls the git hub utility to clone the remote repository to local folder and perform the maven operations.
	 *
	 */
	private static void retrieveTrasitiveDependenciesBasedOnRemoteRepository()
	{
		List<String> repositories = FileUtility.readRepositoriesFromPropertiesFile();
		Scanner reader = new Scanner(System.in);
		System.out.println("Please provide the build number(Eg:5225):");
		String buildNum = reader.next();
		//Check if repositories returned are not null or not empty
		if(repositories!=null && repositories.size()>0)
		{	
			//for each repository: invoke the git hub utility
			for(String repository : repositories)
			{
				String clonedFilePath = GitHubUtility.cloneRepositoryBasedOnBranch(repository,buildNum);
				System.out.println("Cloned File Path::"+clonedFilePath);
				performMavenOperations(clonedFilePath);
			}
		}
		else
		{
			System.err.println("Failed to retrive available products!!!!");
		}
		reader.close();
	}

	private static void performMavenOperations(String mavenProjectFilePath)
	{
		Map<String,List<DependencyBean>> artifactDependenciesMap = new LinkedHashMap<String,List<DependencyBean>>();
		MavenUtility.processPomFiles(mavenProjectFilePath,artifactDependenciesMap);
		//Identify the duplicate dependencies
		MavenUtility.identifyDuplicateDependencies(artifactDependenciesMap);
		//write the final list of dependencies to output file
		String outputFilePath = FileUtility.writeOutputToFile(artifactDependenciesMap);	
		System.out.println("\n\n Final list of dependencies written to:"+outputFilePath);
	}	
}

