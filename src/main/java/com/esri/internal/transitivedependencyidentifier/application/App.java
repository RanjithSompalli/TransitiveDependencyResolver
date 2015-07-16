package com.esri.internal.transitivedependencyidentifier.application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
    public static void main( String[] args ) throws IOException, InvalidRemoteException, TransportException, GitAPIException, XmlPullParserException, MavenInvocationException
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
    	
    	
    	
    	
    	/*try {
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
    	}*/
    	
    	
    	/*MavenXpp3Reader mavenreader = new MavenXpp3Reader();

    	File pomfile = new File("C:\\WorkingDirectory\\tempCloneFolder\\pom.xml");
    	Model model = mavenreader.read(new FileReader(pomfile));
    	System.out.println("Model Packaging Type::"+model.getPackaging());
    	for(String module :model.getModules())
    	{
    		System.out.println("Module Name:"+module);
    	}
    	
    	List<Dependency> deps = model.getDependencies();

    	for (Dependency d: deps) {          
    	    System.out.print(d.getArtifactId());
    	} */
    	
    	InvocationRequest request = new DefaultInvocationRequest();
    	request.setPomFile( new File( "C:\\WorkingDirectory\\Java-Workspace\\arcgis-for-server-master" ) );
    	request.setGoals( Arrays.asList( "clean","site") );

    	Invoker invoker = new DefaultInvoker();
    	invoker.setMavenHome(new File("C:\\Users\\ranj8168\\Downloads\\apache-maven-3.3.3"));
    	invoker.execute( request );
    	
    }
}
