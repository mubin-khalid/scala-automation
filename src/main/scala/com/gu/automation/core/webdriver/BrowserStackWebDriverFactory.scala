package com.gu.automation.core.webdriver

import java.net.URL
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import com.gu.automation.support.Config
import com.gu.automation.core.ParentWebDriverFactory

object BrowserStackWebDriverFactory extends ParentWebDriverFactory {

  val webDriverRemoteUrl: String = Config().getWebDriverRemoteUrl()
  val browserStackOS: Option[String] = Config().getPlatform()
  val browserStackOSVersion: Option[String] = Config().getPlatformVersion()
  val browserVersion: Option[String] = Config().getBrowserVersion()
  val resolution: Option[String] = Config().getResolution()
  val browserStackVisualLog: Option[String] = Config().getBrowserStackVisualLog()

  override def createDriver(testCaseName: String, capabilities: DesiredCapabilities): WebDriver = {
    augmentCapabilities(testCaseName, capabilities)
    new RemoteWebDriver(new URL(webDriverRemoteUrl), capabilities)
  }

  def augmentCapabilities(testCaseName: String, capabilities: DesiredCapabilities): DesiredCapabilities = {
    browserStackOS.foreach(capabilities.setCapability("os", _))
    browserStackOSVersion.foreach(capabilities.setCapability("os_version", _))
    browserVersion.foreach(capabilities.setCapability("browser_version", _))
    resolution.foreach(capabilities.setCapability("resolution", _))
    browserStackVisualLog.foreach(capabilities.setCapability("browserstack.debug", _))
    capabilities
  }
}