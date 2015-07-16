package com.esri.internal.transitivedependencyidentifier.beans;

public class DependencyBean 
{
	String groupId;
	String artifactId;
	String version;
	String licencse;
	boolean isTransitiveDependency;
	boolean isDuplicate;
	
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getLicencse() {
		return licencse;
	}
	public void setLicencse(String licencse) {
		this.licencse = licencse;
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
