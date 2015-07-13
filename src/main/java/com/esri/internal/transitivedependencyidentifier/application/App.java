package com.esri.internal.transitivedependencyidentifier.application;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InvalidRemoteException, TransportException, GitAPIException
    {
    	
                    
                    /*CloneCommand cloneCommand = Git.cloneRepository();
                    cloneCommand.setDirectory(new File("C:\\myfolder"));
                    cloneCommand.setNoCheckout(true);
                    cloneCommand.setRemote( "https://github.com/RanjithSompalli/IDP.git" );
                    cloneCommand.setCredentialsProvider( new UsernamePasswordCredentialsProvider( "RanjithSompalli", "Ranso@6290" ) );
                    cloneCommand.call();*/
                    
                    /*Git result = Git.cloneRepository()
                            .setURI("https://github.com/RanjithSompalli/IDP.git")
                            .setDirectory(new File("C:\\myfolder"))
                            .setBranchesToClone(Arrays.asList("tree/IDP_v0.9"))
                            .call();
                    System.out.println("Result:::"+result.toString());*/
    	
    	
    	
    	
    	try {
    		File tempCloneFile = new File("C://tempCloneFolder");
    		if(tempCloneFile.exists())
    		{
    			
    			tempCloneFile.delete();
    		}
			Git r = Git.cloneRepository().setDirectory(tempCloneFile)
					.setURI("https://github.com/RanjithSompalli/IDP.git")
					.setCredentialsProvider(new UsernamePasswordCredentialsProvider("RanjithSompalli","Ranso@6290"))
					.setNoCheckout(false)
					.setProgressMonitor(new TextProgressMonitor())
					.call();
			r.checkout().setName("origin/IDP_v0.9.5").call();
    	}
    	catch(Exception e)
    	{
    		System.out.println("Exception caught:::"+e.getMessage());
    	}
    }
}
