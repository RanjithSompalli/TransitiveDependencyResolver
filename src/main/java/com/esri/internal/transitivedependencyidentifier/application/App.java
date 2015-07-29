package com.esri.internal.transitivedependencyidentifier.application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


//import com.esri.internal.transitivedependencyidentifier.util.;













import java.io.IOException;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InvalidRemoteException, TransportException, GitAPIException, XmlPullParserException, MavenInvocationException
    {
    	
    	/*BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL("https://raw.githubusercontent.com/RanjithSompalli/TransitiveDependencyResolver/branch-1/pom.xml").openStream());
            File tempFileName = File.createTempFile("tempPOMFile", ".xml");
            System.out.println("Temp file path ="+tempFileName.getAbsolutePath());
            fout = new FileOutputStream(tempFileName.getAbsolutePath());

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }*/
    	
    	File gitWorkDir = new File("C:\\Users\\ranj8168\\AppData\\Local\\Temp\\1\\tempCloneFolder185923185622015762");
        Git git = Git.open(gitWorkDir);
       
        Repository repo = git.getRepository();
     
        ObjectId lastCommitId = repo.resolve(Constants.HEAD);
     
        RevWalk revWalk = new RevWalk(repo);
        RevCommit commit = revWalk.parseCommit(lastCommitId);
     
        RevTree tree = commit.getTree();
     
        TreeWalk treeWalk = new TreeWalk(repo);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create("file1.txt"));
        if (!treeWalk.next()) 
        {
          System.out.println("Nothing found!");
          return;
        }
     
        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = repo.open(objectId);
     
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        loader.copyTo(out);
        System.out.println("file1.txt:\n" + out.toString());
        

		/* Collection<Ref> references = result.lsRemote().setCredentialsProvider(credentialsProvider).setHeads(true).call();
		 System.out.println("Lists of builds available:::");
		 for(Ref reference : references) 
		 {
			if(reference.getName().contains("refs/heads/builds"))
			{
				String refName = reference.getName();
				String[] splittedBranch = refName.split("/");
				System.out.println(splittedBranch[splittedBranch.length-1]);	
			}
		}	*/
    	 // prepare a new folder
    	/*Repository repository = createNewRepository();
    	 Collection<Ref>  refs = new Git(repository).lsRemote().setHeads(true).call();
         for (Ref ref : refs) {
             System.out.println("Head: " + ref);
         }
        // find the HEAD
        ObjectId lastCommitId = repository.resolve(Constants.HEAD);

        // a RevWalk allows to walk over commits based on some filtering that is defined
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(lastCommitId);
        // and using commit's tree find the path
        RevTree tree = commit.getTree();
        System.out.println("Having tree: " + tree);

        // now try to find a specific file
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create("pom.xml"));
        if (!treeWalk.next()) {
            throw new IllegalStateException("Did not find expected file 'README.md'");
        }

        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = repository.open(objectId);

        // and then one can the loader to read the file
        loader.copyTo(System.out);

        revWalk.dispose();

        repository.close();*/

    	
                    
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
    	
    	/*InvocationRequest request = new DefaultInvocationRequest();
    	request.setPomFile( new File( "C:\\WorkingDirectory\\Java-Workspace\\arcgis-for-server-master" ) );
    	request.setGoals( Arrays.asList( "clean","site") );

    	Invoker invoker = new DefaultInvoker();
    	invoker.setMavenHome(new File("C:\\Users\\ranj8168\\Downloads\\apache-maven-3.3.3"));
    	invoker.execute( request );*/
    	
    	
    
    		/*FileOutputStream fosOutHtml =null;
    		FileWriter fwOutXml =null;
    		FileReader frInHtml=null;
    		 BufferedWriter bwOutXml =null;
    		 BufferedReader brInHtml=null;
    	try {
    	     
    	     frInHtml = new FileReader("C:\\WorkingDirectory\\Java-Workspace\\arcgis-for-server-master\\framework\\persistence\\target\\site\\dependencies.html");
    	     brInHtml = new BufferedReader(frInHtml);
    	     SAXBuilder saxBuilder = new SAXBuilder();
    	     org.jdom.Document jdomDocument = saxBuilder.build(brInHtml);
    	     XMLOutputter outputter = new XMLOutputter();
    	     try {
    	          outputter.output(jdomDocument, System.out);
    	          fwOutXml = new FileWriter("C:\\WorkingDirectory\\Java-Workspace\\dependencies.xml");
    	          bwOutXml = new BufferedWriter(fwOutXml);
    	          outputter.output(jdomDocument, bwOutXml);
    	          System.out.flush();
    	      }
    	      catch (IOException e)  {  }
    	            
    	}
    	catch (IOException e) {  }  
    	finally {
    	     System.out.flush();
    	     try{
    	     fosOutHtml.close();
    	     fwOutXml.flush();
    	     fwOutXml.close();
    	     bwOutXml.close();
    	     }
    	     catch(Exception w)
    	     {
    	    	 
    	     }
    	}
    	}*/  	
    	
    }  
    
    public static Repository openJGitCookbookRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        return repository;
    }
    
    public static Repository createNewRepository() throws IOException {
        // prepare a new folder
        File localPath = File.createTempFile("TestGitRepository", "");
        localPath.delete();

        // create the directory
        Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
        repository.create();

        return repository;
    }
}


