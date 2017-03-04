/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package tech.sirwellington.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author SirWellington
 */
public class DenvyComHtmlUnit 
{
    private final static Logger LOG = LoggerFactory.getLogger(DenvyComHtmlUnit.class);

    public static void main(String[] args) throws IOException 
    {
        WebClient web = new WebClient(BrowserVersion.CHROME);
        
        HtmlPage page = web.getPage("http://denvycom.com/blog/");
        
        String title = page.getTitleText();
        LOG.info("Page title is  {}", title);
        
        DomElement searchBar = page.getElementById("s");
        DomElement searchButton = page.getElementById("searchsubmit");
        
        searchBar.setAttribute("value", "research");
        HtmlPage newPage = searchButton.click();
        
        LOG.info("New page title is {}", newPage.getTitleText());
        
        DomNodeList<DomNode> titles = newPage.querySelectorAll(".page-header");
        
        titles.forEach((element) -> 
        {
            String text = element.asText();
            LOG.info("Result Title: {}", text);
        });
    }

}
