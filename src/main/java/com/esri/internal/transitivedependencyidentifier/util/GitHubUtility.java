package com.esri.internal.transitivedependencyidentifier.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.esri.internal.transitivedependencyidentifier.beans.GitHubLoginCredentials;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;

public class GitHubUtility 
{
	public static String cloneRepositoryBasedOnBranch(String repoURL)
	{
		Git result = null;
		File tempCloneFolder = null;
		Scanner scanner = null;
		try 
		{
			Path path = Files.createTempDirectory("tempCloneFolder");
			System.out.println("Path of temp folder:::"+path.toString());
			tempCloneFolder = new File(path.toString());
    		GitHubLoginCredentials loginCredentials = getLoginCredentials();
    		
    		UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(loginCredentials.getLogin(),loginCredentials.getPassword());
			result = Git.cloneRepository().setDirectory(tempCloneFolder)
					.setURI(repoURL)
					.setCredentialsProvider(credentialsProvider)
					.setNoCheckout(true)
					.setProgressMonitor(new TextProgressMonitor())
					.call();
			
			 Collection<Ref> references = result.lsRemote().setCredentialsProvider(credentialsProvider).setHeads(true).call();
			 System.out.println("Lists of builds available:::");
			 for(Ref reference : references) 
			 {
				if(reference.getName().contains("refs/heads/builds"))
				{
					String refName = reference.getName();
					String[] splittedBranch = refName.split("/");
					System.out.println(splittedBranch[splittedBranch.length-1]);	
				}
			}	
			String buildNum = "10.4.0.5225";
			result.checkout().setName("origin/builds/"+buildNum).call();
    	}
		
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		finally
		{
			result.close();
		}
		return tempCloneFolder.getAbsolutePath();
	}

	
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
	
	public static GitHubLoginCredentials getLoginCredentials()
	{
		GitHubLoginCredentials credentials = new GitHubLoginCredentials();
		Properties props = new Properties();
        FileInputStream in = null;
        try 
        {
            in = new FileInputStream(TransitiveDependencyProjectConstants.CREDENTIALSFILE);
            props.load(in);
            credentials.setLogin(props.getProperty(TransitiveDependencyProjectConstants.LOGIN));
            credentials.setPassword(props.getProperty(TransitiveDependencyProjectConstants.PASSWORD));
        } 
        catch (IOException e)
        {
			e.printStackTrace();
		} 
        finally 
        {
            try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return credentials;
    }

	
}
