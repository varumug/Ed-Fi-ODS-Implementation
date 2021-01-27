package OdsApi_Utilities

import OdsApi_Utilities.buildTypes.*
import OdsApi_Utilities.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("OdsApi_Utilities")
    name = "Utilities"

    vcsRoot(OdsApi_Utilities_EdFiOds_2)

    buildType(OdsApi_Utilities_EdFiSdkGen)
})
