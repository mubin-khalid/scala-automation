package com.gu.automation.support

import java.io.{File, FileReader, InputStreamReader, Reader}

import com.typesafe.config.ConfigFactory

class Config(localFile: Option[Reader], projectFile: Option[Reader], frameworkFile: Option[Reader]) {

  private val config: com.typesafe.config.Config = {

    def inFileFallback(environment: String)(conf: com.typesafe.config.Config) = {
      if (conf.hasPath(environment)) conf.getObject(environment).withFallback(conf).toConfig()
      else conf
    }

    def crossFileFallback(localConf: com.typesafe.config.Config, projectConf: com.typesafe.config.Config, frameworkConfig: com.typesafe.config.Config) = {
      localConf.withFallback(projectConf.withFallback(frameworkConfig))
    }

    val frameworkConfig = frameworkFile.map(ConfigFactory.parseReader(_)).getOrElse(ConfigFactory.empty())
    val projectConf = projectFile.map(ConfigFactory.parseReader(_)).getOrElse(ConfigFactory.empty())
    val localConf = localFile.map(ConfigFactory.parseReader(_)).getOrElse(ConfigFactory.empty())

    val environment = crossFileFallback(localConf, projectConf, frameworkConfig).getString("environment")

    val specificEnvFallback = inFileFallback(environment)_

    crossFileFallback(specificEnvFallback(localConf), specificEnvFallback(projectConf), specificEnvFallback(frameworkConfig))

  }

  protected def getConfigValue(key: String) = {
    config.getString(key)
  }

  def getBrowser(): String = {
    getConfigValue("browser")
  }

  def getWebDriverRemoteUrl(): String = {
    getConfigValue("webDriverRemoteUrl")
  }

  def getTestBaseUrl(): String = {
    getConfigValue("testBaseUrl")
  }

  def getUserValue(key: String): String = {
    config.getConfig("user").getString(key)
  }

}

object Config {

  def apply() = defaultLoader

  private lazy val defaultLoader: Config = {
    val readers = getDefaultInject
    new Config(readers._1, readers._2, readers._3)
  }

  def getDefaultInject = {
    val local = new File("local.conf")
    val localOption =
      if (local.exists) Some(new FileReader(local))
      else None
    (localOption, getReader("project.conf"), getReader("framework.conf"))
  }

  private def getReader(leafName: String) = {
    val resource = this.getClass.getClassLoader.getResourceAsStream(leafName)
    if (resource == null) None
    else Some(new InputStreamReader(resource))
  }

}