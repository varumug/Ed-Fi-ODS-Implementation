package OdsApi_PostgreSQL.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object OdsApi_PostgreSQL_OdsApiInitDevIntegrationTest : BuildType({
    templates(_Self.buildTypes.OdsApiInitDevUnitTestPackageTemplate)
    name = "ODS/API: InitDev, Integration Test"

    params {
        param("odsapi.package.sdk", "EdFi%odsapi.package.suffix%.OdsApi.Sdk")
        param("odsapi.smokeTestExecutable", "EdFi.SmokeTest.Console")
        param("odsapi.build.runDotnetTest", "false")
        param("odsapi.build.engine", "PostgreSQL")
        param("odsapi.package.smokeTest", "EdFi%odsapi.package.suffix%.SmokeTest.Console")
    }
    
    disableSettings("RUNNER_332", "RUNNER_392", "RUNNER_417", "RUNNER_421", "RUNNER_438", "RUNNER_456", "RUNNER_89", "RUNNER_90", "RUNNER_93")
})
