/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

 
package tech.sirwellington.selenium;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author SirWellington
 */
public final class DenvyCom 
{
    private final static Logger LOG = LoggerFactory.getLogger(DenvyCom.class);

    public static void main(String[] args)
    {
        WebDriver driver = new HtmlUnitDriver();
        
        driver.get("http://denvycom.com/blog/");
        
        WebElement searchBar = driver.findElement(By.id("s"));
        
        if (searchBar == null)
        {
            return;
        }
        
        searchBar.sendKeys("research");
        searchBar.submit();
        
        String pageTitle = driver.getTitle();
        LOG.info("Page Title: {}", pageTitle);
        
        List<WebElement> titles = driver.findElements(By.cssSelector("h2.page-header"));
        List<WebElement> dates = driver.findElements(By.cssSelector("span.entry-date"));
        
        LOG.info("Found {} titles and {} dates", titles.size(), dates.size());
        
        titles.forEach(title -> LOG.info("Title: {}", title.getText()));
    }
}
