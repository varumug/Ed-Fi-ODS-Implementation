package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_OdsApi_Deploy_And_SmokeTest_SharedInstanceOrYearSpecific : Template({
    name = "ODS/API: Deploy and Smoke Test Shared Instance or Year-Specific"
    description = "Runs deploy and smoke test for either a Shared Instance or Year-Specific install"

    params {
        param("odsapi.dllname.sdk", "EdFi.OdsApi.Sdk")
        param("environment.webApi.apiUrl", "%environment.webApi.baseUrl%/%environment.webApi.startupType%_%octopus.release.channel%/api")
        param("smokeTest.apiUrl", "%environment.webApi.apiUrl%/data/v3")
        param("smokeTest.credential.key", "populatedSandbox")
        param("smokeTest.credential.secret", "populatedSandboxSecret")
        param("smokeTest.oauthUrl", "%environment.webApi.apiUrl%/")
        param("script.run.smoketests", "Ed-Fi-ODS-Implementation/logistics/scripts/run-smoke-tests.ps1")
        param("octopus.release.version", "${OdsApi_OdsApiInitDevUnitTestPackage.depParamRefs["version"]}")
        param("script.module.loadtools", "Ed-Fi-ODS-Implementation/logistics/scripts/modules/LoadTools.psm1")
        param("environment.webApi.baseUrl", "https://api-stage.ed-fi.org")
        param("octopus.deploy.timeout", "00:06:00")
        param("environment.webApi.metadataUrl", "%environment.webApi.apiUrl%/metadata")
        param("octopus.deploy.environment", "Staging")
        param("environment.webApi.odsYear", "")
        param("script.octopusDeploy.management", "Ed-Fi-ODS-Implementation/logistics/scripts/modules/octopus-deploy-management.psm1")
        param("odsapi.package.databases", "EdFi.RestApi.Databases")
    }

    vcs {
        showDependenciesChanges = true
    }

    steps {
        powerShell {
            name = "Ensure Octopus Channel Exists"
            id = "RUNNER_381"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    Import-Module "%teamcity.build.checkoutDir%\%script.octopusDeploy.management%"
                    
                    ${'$'}params = @{
                        serverBaseUrl = "%octopus.server%"
                        apiKey = "%octopus.apiKey%"
                        project = "%octopus.project.name%"
                        channel = "%octopus.release.channel%"
                    }
                    Invoke-OctopusCreateChannel @params
                """.trimIndent()
            }
        }
        powerShell {
            name = "Create Octopus Release"
            id = "RUNNER_383"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    Import-Module "%teamcity.build.checkoutDir%\%script.octopusDeploy.management%"
                    
                    ${'$'}params = @{
                        serverBaseUrl = "%octopus.server%"
                        apiKey = "%octopus.apiKey%"
                        project = "%octopus.project.name%"
                        channel = "%octopus.release.channel%"
                        packageVersion = "%octopus.release.version%"
                        version = "%octopus.release.version%"
                    }
                    Invoke-OctopusCreateRelease @params
                """.trimIndent()
            }
        }
        powerShell {
            name = "Deploy Octopus Release"
            id = "RUNNER_384"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    Import-Module "%teamcity.build.checkoutDir%\%script.octopusDeploy.management%"
                    
                    ${'$'}params = @{
                        serverBaseUrl = "%octopus.server%"
                        apiKey = "%octopus.apiKey%"
                        project = "%octopus.project.name%"
                        channel = "%octopus.release.channel%"
                        version = "%octopus.release.version%"
                        environment = "%octopus.deploy.environment%"
                        deploymentTimeout = "%octopus.deploy.timeout%"
                    }
                    Invoke-OctopusDeployRelease @params
                """.trimIndent()
            }
        }
        powerShell {
            name = "Run Smoketest"
            id = "RUNNER_389"
            scriptMode = script {
                content = """
                    Import-Module -Force "%teamcity.build.checkoutDir%\%script.module.loadtools%"
                     
                    ${'$'}params = @{
                        apiUrlBase = "%environment.webApi.apiUrl%"
                        apiKey = "%smokeTest.credential.key%"
                        apiSecret = "%smokeTest.credential.secret%"
                        apiYear = "%environment.webApi.odsYear%"
                        namespaceUri = "http://edfi.org"
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
        dependency(OdsApi_OdsApiInitDevUnitTestPackage) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_52"
                artifactRules = "%odsapi.package.databases%.*.nupkg!**=>."
            }
        }
        artifacts(OdsApi_Packages.buildTypes.OdsApi_Packages_EdFiOdsApiSdk) {
            id = "ARTIFACT_DEPENDENCY_51"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%odsapi.package.sdk%.*.nupkg!** => %odsapi.package.sdk%"
        }
        artifacts(RelativeId("OdsImplementationKotlin_EdFiLoadTools_BranchBuild")) {
            id = "ARTIFACT_DEPENDENCY_50"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%odsapi.package.smokeTest%.*.nupkg!** => %odsapi.package.smokeTest%"
        }
    }
})
