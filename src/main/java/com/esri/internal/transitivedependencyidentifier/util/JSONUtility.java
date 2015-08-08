/**
 * 
 */
package com.esri.internal.transitivedependencyidentifier.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.esri.internal.transitivedependencyidentifier.beans.DependencyBean;

/**
 * @author ranj8168
 *	This class holds the utility methods that performs the JSON related operation
 */
public class JSONUtility 
{
	
	/**
	 * 
	 * This method converts the artifactDependenciesMap in the format of Map<String,List<DependencyBean>> to a JSon object
	 * Format of the JSON Object will be : 
	 * {
	 * 	"product-name":"productName",
	 * 	"artifactDependencies": [
	 * 								{
	 * 									"artifact-name" : "artifactName",
	 * 									"dependencies"  : [
	 * 														{
	 * 															"group-id" : "groupId",
	 * 															"artifact-id" : "artifactId",
	 * 															"version" : "version",
	 * 															"isTrasitiveDependency" : "True/False",
	 * 															"isDuplicateDependency" : "True/False"
	 * 														},
	 * 														{
	 * 														} ... for all the dependencies that the artifact has
	 * 													]
	 * 								},
	 * 								{
	 * 								} ... for all the artifacts that the product has
	 * 							]
	 * }
	 * 
	 * @param artifactDependenciesMap -- Map that holds the final dependencies list
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject writeDependecyMapToJsonObject(Map<String, List<DependencyBean>> artifactDependenciesMap) 
	{
		//Build a Linked Hash Map first and then add it to the JSON Object to maintain the insertion order
		Map<String,Object> jsonObjectMap = new LinkedHashMap<String,Object>();
		jsonObjectMap.put("product-name", MavenUtility.productName);
		
		JSONArray artifactDependenciesArray = new JSONArray();
		for(Map.Entry<String, List<DependencyBean>> artifactDependencyPair : artifactDependenciesMap.entrySet())
		{
			JSONObject artifactDependency = new JSONObject();
			artifactDependency.put("artifact-name", artifactDependencyPair.getKey());
			JSONArray dependencyList = new JSONArray();

			List<DependencyBean> dependenciesRetrieved = artifactDependencyPair.getValue();
			for(DependencyBean dependency : dependenciesRetrieved)
			{
				if(dependency.isDuplicate())
				{
					//Build a Linked Hash Map first and then add it to the JSON Object to maintain the insertion order
					Map<String,Object> dependencyJSONMap = new LinkedHashMap<String,Object>();

					dependencyJSONMap.put("groupId", dependency.getGroupId());
					dependencyJSONMap.put("artifactId", dependency.getArtifactId());
					//dependencyJSONMap.put("version", dependency.getVersion());
					dependencyJSONMap.put("isTransitiveDependency", dependency.isTransitiveDependency());
					dependencyJSONMap.put("isDuplicateDependency", dependency.isDuplicate());

					JSONObject dependencyJSON = new JSONObject(dependencyJSONMap);
					dependencyList.add(dependencyJSON);
				}
			}
			artifactDependency.put("dependencies", dependencyList);
			artifactDependenciesArray.add(artifactDependency);
		}
		
		jsonObjectMap.put("artifactDependencies",artifactDependenciesArray);
		JSONObject rootJSON = new JSONObject(jsonObjectMap);	
		
		return rootJSON;
	}

}
