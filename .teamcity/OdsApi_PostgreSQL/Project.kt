package OdsApi_PostgreSQL

import OdsApi_PostgreSQL.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("OdsApi_PostgreSQL")
    name = "PostgreSQL"

    buildType(OdsApi_PostgreSQL_OdsApiInitDevIntegrationTest)
})
