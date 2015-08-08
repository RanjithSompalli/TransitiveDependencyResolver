# TransitiveDependencyResolver
This project lists out all the dependencies of a given artifact and identifies the transitive dependencies and the duplicate dependencies across the artifact by parsing the sub modules.

## Execution Steps:
In order to execute the jar file, few command line arguments needs to be passed as explained below: 
All the options are mandatory!!!
(1) -local/-remote (mandatory argument): indicates the mode of the repository. -local indicates that the repository is in local directory, -remote indicates to clone the repository from a remote repository");
(2) -configFilePath=path\\to\\configFile (mandatory argument) : the path to the properties file. This file should contain the path for local or remote repositories, maven installation folder, devtopia username and password
	Sample configFile format:
	remoteRepositoryPath=https://devtopia.esri.com/WebGIS/arcgis-for-server.git
	localRepositoryPath=C:\\WorkingDirectory\\Java-Workspace\\TransitiveDependencyResolver
	MAVEN_HOME=C:\\Users\\ranj8168\\Downloads\\apache-maven-3.3.3
	login=<devtopia_username>
	password=<devtopia_password>
(3) -outputFilePath=path\\to\\write\\outputFileDirectory (mandatory argument): path to which the output Json file is written.
(4) -duplicateOnly=true/false : specifies whether to retrieve only duplicate dependencies or all the dependencies.
	
output file is written to the given output directory with the  name as dependencyList_<product_name>_<currentTimeStamp>.html file

Example : java -jar transitiveDependencyIdentifier-1.0.0-SNAPSHOT.jar -local -configFilePath=C:\\WorkingDirectory\\projectConfigurations.properties -outputFilePath=C:\\WorkingDirectory\\output --duplicateOnly=true

output file will be saved in C:\WorkingDirectory\output\

This application parses the maven project from localRepositoryPath retrieved from the config file passed through command line arguments and generates a Html file as provided in the outputFilePath.

		
