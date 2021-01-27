package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger

object OdsApi_Deploy_And_SmokeTest_SharedInstance : BuildType({
    templates(OdsApi_OdsApi_Deploy_And_SmokeTest_SharedInstanceOrYearSpecific)
    name = "ODS/API: Deploy and Smoke Test Shared Instance"
    description = "Deploys the ODS/API in Shared Instance mode on the Staging server and runs the Smoke Test utility"

    buildNumberPattern = "${OdsApi_OdsApiInitDevUnitTestPackage.depParamRefs.buildNumber}"

    params {
        param("odsapi.package.sdk", "EdFi%odsapi.package.suffix%.OdsApi.Sdk")
        param("odsapi.package.webApi", "EdFi.Ods.WebApi")
        param("odsapi.smokeTestExecutable", "EdFi.SmokeTest.Console")
        param("octopus.project.name", "Ed-Fi ODS Shared Instance (SQL Server)")
        param("odsapi.package.databases", "EdFi%odsapi.package.suffix%.RestApi.Databases")
        param("environment.webApi.startupType", "SharedInstance")
        param("odsapi.package.smokeTest", "EdFi%odsapi.package.suffix%.SmokeTest.Console")
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_41"
            buildType = "${OdsApi_Deploy_And_Smoke_Test_YearSpecific.id}"
            successfulOnly = true
            branchFilter = "+:*"
        }
    }

    dependencies {
        snapshot(OdsApi_Deploy_And_Smoke_Test_YearSpecific) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }
})
