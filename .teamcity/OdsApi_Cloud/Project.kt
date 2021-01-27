package OdsApi_Cloud

import OdsApi_Cloud.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("OdsApi_Cloud")
    name = "Cloud"

    buildType(OdsApi_Cloud_CloudOdsApiBuildPackage)
})
