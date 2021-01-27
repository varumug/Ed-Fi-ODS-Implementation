package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_OdsApiSmokeTestStagingTemplate : Template({
    name = "ODS/API: Smoke Test Staging Template"

    maxRunningBuilds = 1

    params {
        param("environment.staging.metadataUrl", "https://api-stage.ed-fi.org/%octopus.release.channel%/api/metadata")
        param("script.module.loadtools", """Ed-Fi-ODS-Implementation\logistics\scripts\modules\loadtools.psm1""")
        param("script.run.smoketests", """Ed-Fi-ODS-Implementation\logistics\scripts\run-smoke-tests.ps1""")
    }

    vcs {
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        powerShell {
            name = "Invoke-SmokeTest"
            id = "RUNNER_406"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    Import-Module -Force "%teamcity.build.checkoutDir%\%script.module.loadtools%"
                     
                    ${'$'}params = @{
                        apiUrlBase = "https://api-stage.ed-fi.org/%octopus.release.channel%/api/"
                        apiKey = "smoke"
                        apiSecret = "smokeSecret"
                        apiNamespaceUri = "http://edfi.org"
                        smokeTestDll = ".\%odsapi.package.sdk%\lib\netstandard1.3\%odsapi.dllname.sdk%.dll"
                        smokeTestExecutable = ".\%odsapi.package.smokeTest%\tools\netcoreapp3.1\any\%odsapi.smokeTestExecutable%.dll"
                        testSets = @("NonDestructiveApi", "NonDestructiveSdk")
                    }
                     
                    Write-Host ${'$'}params
                     
                    Invoke-SmokeTestClient -config ${'$'}params
                """.trimIndent()
            }
        }
    }

    dependencies {
        artifacts(AbsoluteId("EdFiBuilds_EdFi20_OdsCi_V3_NuGetPackages_EdFiOdsApiTestSdkV3TestSdkWithSampleExtensionsForSmokeTest")) {
            id = "ARTIFACT_DEPENDENCY_38"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%odsapi.package.sdk%.*.nupkg!** => %odsapi.package.sdk%"
        }
        artifacts(OdsApi.buildTypes.OdsApi_OdsApiInitDevUnitTestPackage) {
            id = "ARTIFACT_DEPENDENCY_14"
            artifactRules = "%odsapi.package.databases%.*.nupkg!** => ."
        }
        artifacts(RelativeId("OdsImplementationKotlin_EdFiLoadTools_BranchBuild")) {
            id = "ARTIFACT_DEPENDENCY_37"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%odsapi.package.smokeTest%.*.nupkg!** => %odsapi.package.smokeTest%"
        }
    }
})
