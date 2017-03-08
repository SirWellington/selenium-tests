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
import org.openqa.selenium.lift.find.TableCellFinder
import org.openqa.selenium.support.ui.Select
import org.slf4j.LoggerFactory
import tech.sirwellington.alchemy.arguments.FailedAssertionException
import java.util.*

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

    LOG.info("Food Balance [$foodBalance], Cash Balance [$cashBalance]")

    printTransactions(web)
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


private data class Transaction(val date: String,
                               val retailer: String,
                               val address: String,
                               val type: String,
                               val transactionType: String,
                               val amount: Double)

private fun printTransactions(web: WebDriver)
{

    val transactionsURL = "https://www.ebt.ca.gov/caebtclient/main.recip?transaction=true"
    web.get(transactionsURL)
    web.printStatus()

    val transactions = getTransactions(web = web, month = 2, year = 2017)
    web.printStatus()

    transactions.forEach { LOG.info("$it") }
}

private fun getTransactions(web: WebDriver, month: Int, year: Int) : Array<Transaction>
{
    selectYear(web, year)
    selectMonth(web, month)

    fun clickSearch()
    {
        val path = """//*[@id="middle_column"]/div/form/table[1]/tbody/tr[2]/td[4]/p/input"""
        val element = web.tryToFindField(By.xpath(path)) ?: return
        element.click()
    }

    clickSearch()

    web.printStatus()

    return loadTransactions(web)
}

private fun loadTransactions(web: WebDriver): Array<Transaction>
{
    var transactions = arrayOf<Transaction>()

    val source = web.pageSource
    val tablePath = """//*[@id="middle_column"]/div/table[2]/tbody[2]"""
    val tableSelector = "#middle_column > div > table:nth-child(7) > tbody:nth-child(2)"
    val table = web.tryToFindField(By.cssSelector(tableSelector)) ?: return transactions

    //Will Wagner
    return table.tryToFindElements(By.cssSelector("tr"))
            .map(::rowToTransaction)
            .filterNotNull()
            .toTypedArray()

}

private fun rowToTransaction(element: WebElement) : Transaction?
{

    val cells = element.tryToFindElements(By.cssSelector("td"))

    if (cells.isEmpty()) return null

    val date = cells[0].text ?: ""
    val retailer = cells[1].text ?: ""
    val location = cells[2].text ?: ""
    val type = cells[4].text ?: ""
    val transactionType = cells[5].text ?: ""
    val amount = cells.last().text.removePrefix("-$").toDoubleOrNull() ?: 0.0

    return Transaction(date, retailer, location, type, transactionType, amount)
}

private fun selectYear(web: WebDriver, year: Int)
{
    val elementId = "selectyear"
    val element = web.findElement(By.id(elementId))
    element.click()
    val select = Select(element)
    select.selectByVisibleText("$year")
}

private fun selectMonth(web: WebDriver, month: Int)
{
    val elementId = "selectmonth"
    val element = web.findElement(By.id(elementId))
    element.click()

    val select = Select(element)
    select.selectByIndex(month - 1)
}

private val Int.monthToText: String
    get() {
        return when(this)
        {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

//MARK: Extensions

private fun WebDriver.tryToFindField(by: By): WebElement?
{
    try
    {
        return this.findElement(by)
    }
    catch(ex: NoSuchElementException)
    {
        LOG.warn("Failed to load Element: $by. $ex")
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

private fun WebElement.tryToFindElement(by: By) : WebElement?
{
    try
    {
        return this.findElement(by)
    }
    catch(ex: NoSuchElementException)
    {
        LOG.warn("Failed to load Element: $by. $ex")
        return null
    }
}

private fun WebElement.tryToFindElements(by: By) : Array<WebElement>
{

    try
    {
        return this.findElements(by)?.filterNotNull()?.toTypedArray() ?: emptyArray()
    }
    catch(ex: NoSuchElementException)
    {
        return emptyArray()
    }

}