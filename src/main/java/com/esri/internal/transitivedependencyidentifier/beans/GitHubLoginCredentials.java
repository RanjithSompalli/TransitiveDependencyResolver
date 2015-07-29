package com.esri.internal.transitivedependencyidentifier.beans;

/**
 *@author ranj8168
 *
 *This is a java bean class that holds the user name and password for authenticating the github
 *
 */
public class GitHubLoginCredentials 
{
	private String login;
	private String password;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
