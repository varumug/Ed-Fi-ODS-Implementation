package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger

object OdsApi_OdsApiPublishToAzure : BuildType({
    name = "ODS/API: Publish to Azure"

    buildNumberPattern = "${OdsApi_OdsApiDeployToStaging.depParamRefs.buildNumber}"

    params {
        param("version", "${OdsApi_OdsApiInitDevUnitTestPackage.depParamRefs["version"]}")
    }

    vcs {
        showDependenciesChanges = true
    }

    steps {
        nuGetPublish {
            name = "Publish to Azure"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = "*.nupkg"
            serverUrl = "%azureArtifacts.feed.nuget%"
            apiKey = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${OdsApi_OdsApiSmokeTestStaging.id}"
            successfulOnly = true
            branchFilter = """
                +:<default>
                +:master-v3
            """.trimIndent()
        }
    }

    features {
        nuGetFeedCredentials {
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    dependencies {
        snapshot(OdsApi_Deploy_And_SmokeTest_SharedInstance) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
        snapshot(OdsApi_Deploy_And_Smoke_Test_YearSpecific) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
        snapshot(OdsApi_OdsApiDeployToStaging) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
        snapshot(OdsApi_OdsApiInitDevIntegrationTest) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
        dependency(OdsApi_OdsApiInitDevUnitTestPackage) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                cleanDestination = true
                artifactRules = "*.nupkg => ."
            }
        }
        snapshot(OdsApi_OdsApiSmokeTestStaging) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }
})
