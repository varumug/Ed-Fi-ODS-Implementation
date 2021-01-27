package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object OdsApi_OdsApiInitDevIntegrationTest : BuildType({
    templates(_Self.buildTypes.OdsApiInitDevUnitTestPackageTemplate)
    name = "ODS/API: InitDev, Integration Test"

    params {
        param("odsapi.build.runPester", "false")
        param("odsapi.build.runDotnetTest", "false")
        param("odsapi.build.script", """Ed-Fi-ODS-Implementation\build.teamcity.ps1""")
    }
    
    disableSettings("RUNNER_392", "RUNNER_417", "RUNNER_438", "RUNNER_456", "RUNNER_89", "RUNNER_90", "RUNNER_93")
})
