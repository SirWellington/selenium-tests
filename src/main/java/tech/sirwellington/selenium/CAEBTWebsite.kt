/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.sirwellington.selenium

import com.gargoylesoftware.htmlunit.WebClient
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.slf4j.LoggerFactory

/*

  @author sirwellington
  Created on Mar 7, 2017
*/

private const val username = ""
private const val password = ""

class CAEBTWebsite
private val LOG = LoggerFactory.getLogger(CAEBTWebsite::class.java)

fun main(args: Array<String>)
{

    val loginURL = "https://www.ebt.ca.gov/caebtclient/login.jsp"

    val web = HtmlUnitDriver(true)
    web.get(loginURL)
    web.printStatus()

    login(web)
    web.printStatus()

    val foodBalance = getFoodBalance(web)
    val cashBalance = getCashBalance(web)

    LOG.info("Food Balance [${foodBalance}], Cash Balance [${cashBalance}]")
}

private fun login(web: WebDriver)
{
    val idForUserId = "login"
    val idForPassword = "password"

    val loginField = web.findElement(By.id(idForUserId)) ?: return
    loginField.sendKeys(username)

    val passwordField = web.findElement(By.id(idForPassword)) ?: return
    passwordField.sendKeys(password)

    val submitField = web.tryToFindField(By.cssSelector("input.submit_container")) ?: return
    submitField.click()

}

private fun WebDriver.tryToFindField(by: By): WebElement?
{
    try
    {
        return this.findElement(by)
    }
    catch(ex: NoSuchElementException)
    {
        LOG.warn("Failed to load Element: ${by}. ${ex}")
        return null
    }
}

private fun WebDriver.printStatus()
{
    val status =
            """
            The current title is '${this.title}'
            and the current url is '${this.currentUrl}'
            """

    LOG.info(status)
}

private fun getFoodBalance(web: WebDriver) : Double?
{
    val selector = "#middle_column > div > table > tbody > tr:nth-child(2) > td.light_yellow_no > table > tbody > tr:nth-child(1) > td:nth-child(2) > font > b"
    val element = web.tryToFindField(By.cssSelector(selector)) ?: return null

    var text = element.text ?: return null
    text = text.removePrefix("$")
    return text.toDoubleOrNull()
}

private fun getCashBalance(web: WebDriver) : Double?
{
    val xPath = """//*[@id="middle_column"]/div/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td[2]/font/b"""
    val element = web.tryToFindField(By.xpath(xPath)) ?: return null

    return element.text.removePrefix("$").toDoubleOrNull()
}