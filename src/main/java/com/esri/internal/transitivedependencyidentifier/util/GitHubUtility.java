package com.esri.internal.transitivedependencyidentifier.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.esri.internal.transitivedependencyidentifier.beans.GitHubLoginCredentials;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;

public class GitHubUtility 
{
	public static String cloneRepositoryBasedOnBranch(String repoURL, String buildNum)
	{
		Git result = null;
		File tempCloneFolder = null;
		try 
		{
			tempCloneFolder = new File(TransitiveDependencyProjectConstants.TEMPCLONEFOLDER);
    		boolean isDirectoryCreated = tempCloneFolder.mkdir();
    		if (!isDirectoryCreated) 
    		{
    			deleteDir(tempCloneFolder);
    		} 
    		
    		GitHubLoginCredentials loginCredentials = getLoginCredentials();
    		
			result = Git.cloneRepository().setDirectory(tempCloneFolder)
					.setURI(repoURL)
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider(loginCredentials.getLogin(),loginCredentials.getPassword()))
					.setNoCheckout(true)
					.setProgressMonitor(new TextProgressMonitor())
					.call();
			result.checkout().setName("origin/builds/10.4.0."+buildNum).call();
			System.out.println("Having repository: " + result.getRepository().getDirectory());
    	}
		
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		finally
		{
			result.close();
		}
		return tempCloneFolder.getName();
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
