package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger

object OdsApi_OdsApiSmokeTestStaging : BuildType({
    templates(_Self.buildTypes.OdsApi_OdsApiSmokeTestStagingTemplate)
    name = "ODS/API: Smoke Test Staging"

    buildNumberPattern = "${OdsApi_OdsApiDeployToStaging.depParamRefs.buildNumber}"

    params {
        param("odsapi.dllname.sdk", "EdFi.OdsApi.Sdk")
        param("odsapi.package.sdk", "EdFi%odsapi.package.suffix%.OdsApi.TestSdk")
        param("odsapi.smokeTestExecutable", "EdFi.SmokeTest.Console")
        param("git.branch.default", "main")
        param("odsapi.package.databases", "EdFi%odsapi.package.suffix%.RestApi.Databases")
        param("odsapi.package.smokeTest", "EdFi%odsapi.package.suffix%.SmokeTest.Console")
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_7"
            buildType = "${OdsApi_OdsApiDeployToStaging.id}"
            successfulOnly = true
            branchFilter = "+:*"
        }
    }

    dependencies {
        snapshot(OdsApi_OdsApiDeployToStaging) {
        }
        snapshot(OdsApi_OdsApiInitDevUnitTestPackage) {
        }
        dependency(OdsApi_Packages.buildTypes.OdsApi_Packages_EdFiOdsApiTestSdk) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_38"
                buildRule = lastSuccessful()
                cleanDestination = true
                artifactRules = "%odsapi.package.sdk%.*.nupkg!** => %odsapi.package.sdk%"
            }
        }
    }
})
