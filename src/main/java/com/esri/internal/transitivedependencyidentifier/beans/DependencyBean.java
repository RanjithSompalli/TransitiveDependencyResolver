package com.esri.internal.transitivedependencyidentifier.beans;

import java.util.List;
import java.util.Map;

/***
 * @author ranj8168
 *This is a java bean class that represents a pom file's Dependency
 *
 *
 */
public class DependencyBean 
{
	private String groupId;
	private String artifactId;
	private Map<String,List<String>> versionToArtifactMappings;
	private boolean isTransitiveDependency;
	private boolean isDuplicate;
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public Map<String, List<String>> getVersionToArtifactMappings() {
		return versionToArtifactMappings;
	}
	public void setVersionToArtifactMappings(
			Map<String, List<String>> versionToArtifactMappings) {
		this.versionToArtifactMappings = versionToArtifactMappings;
	}
	public boolean isTransitiveDependency() {
		return isTransitiveDependency;
	}
	public void setTransitiveDependency(boolean isTransitiveDependency) {
		this.isTransitiveDependency = isTransitiveDependency;
	}
	public boolean isDuplicate() {
		return isDuplicate;
	}
	public void setDuplicate(boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}
}
