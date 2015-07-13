package com.esri.internal.transitivedependencyidentifier.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esri.internal.transitivedependencyidentifier.beans.ProductRepository;
import com.esri.internal.transitivedependencyidentifier.constants.TransitiveDependencyProjectConstants;

public class XMLUtility {

	public static List<ProductRepository> getAvailableProductsFromXML() 
	{
		List<ProductRepository> productRepositoriesList = new ArrayList<ProductRepository>();
		try 
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(TransitiveDependencyProjectConstants.XMLFILEPATH));
			NodeList productNodes = doc.getElementsByTagName(TransitiveDependencyProjectConstants.PRODUCTTAGNAME);
			if(productNodes!=null && productNodes.getLength()>0)
			{
				for(int productCount=0;productCount<productNodes.getLength();productCount++)
				{
					ProductRepository productRepository = new ProductRepository();
					Element productElement = (Element)productNodes.item(productCount);
					if(productElement.hasAttributes())
					{
						productRepository.setProductName(productElement.getAttribute(TransitiveDependencyProjectConstants.PRODUCTNAMEATTRIBUTE));
						NodeList repositories = productElement.getElementsByTagName(TransitiveDependencyProjectConstants.REPOSITORYTAGNAME);
						if(repositories!=null && repositories.getLength()>0)
						{
							List<String> repositoryLinks = new ArrayList<String>();
							for(int repositoryCount =0; repositoryCount<repositories.getLength();repositoryCount++)
							{
								repositoryLinks.add(repositories.item(repositoryCount).getTextContent());
							}
							productRepository.setRepositories(repositoryLinks);
						}
						
					}
					productRepositoriesList.add(productRepository);
				}	
			}
			else
				return null;
			
		} 
		
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		return productRepositoriesList;
	}
}
