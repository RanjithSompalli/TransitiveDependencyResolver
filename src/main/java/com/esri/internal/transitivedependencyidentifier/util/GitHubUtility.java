package com.esri.internal.transitivedependencyidentifier.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.esri.internal.transitivedependencyidentifier.beans.GitHubLoginCredentials;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;

/**
 * @author ranj8168
 * This class performs all git specific operations : like authenticating the remote repository, cloning the remote repository,
 * Switching between the builds etc.,
 *
 */
public class GitHubUtility 
{
	/***
	 * This method will clones the repository from repoURL and checks out the specific branch
	 * @param repoURL -- URL of the remote repository
	 * @param buildNum -- he build number for which the dependencies are to be resolved
	 * @return
	 */
	public static String cloneRepositoryBasedOnBranch(String repoURL,String buildNum)
	{
		Git result = null;
		File tempCloneFolder = null;
		try 
		{
			Path path = Files.createTempDirectory("tempCloneFolder");
			
			tempCloneFolder = new File(path.toString());
    		GitHubLoginCredentials loginCredentials = getLoginCredentials();
    		
    		UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(loginCredentials.getLogin(),loginCredentials.getPassword());
			result = Git.cloneRepository().setDirectory(tempCloneFolder)
					.setURI(repoURL)
					.setCredentialsProvider(credentialsProvider)
					.setNoCheckout(true)
					.setProgressMonitor(new TextProgressMonitor())
					.call();
			result.checkout().setName("origin/builds/10.4.0."+buildNum).call();
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
	
	/**
	 * This method checks if remote repository exists and then switched to corresponding branch provided as build number
	 * @param localRepositoryPath -- local git repository path
	 * @param buildNum -- the build number for which the dependencies are to be resolved
	 */
	public static void checkOutToBuildNumber(String localRepositoryPath,String buildNum) 
	{
		try 
		{
			File gitWorkDir = new File(localRepositoryPath);
			if(gitWorkDir.exists())
			{
				Git git = Git.open(gitWorkDir);
				git.checkout().setName("origin/builds/10.4.0."+buildNum).call();
			}
			else
				throw new Exception("No Git repository exists in Given Path");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}   
	}

	
	/**
	 * Retrieves the git hub login credentials from the properties file.
	 * 
	 * @return GitHubLoginCredentials -- Object that holds the login and password of github
	 */
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
        {try {in.close();} catch (IOException e) {e.printStackTrace();}
        }
        return credentials;
    }	
}
