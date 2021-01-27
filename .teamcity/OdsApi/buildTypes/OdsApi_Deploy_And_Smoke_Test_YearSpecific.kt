package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger

object OdsApi_Deploy_And_Smoke_Test_YearSpecific : BuildType({
    templates(OdsApi_OdsApi_Deploy_And_SmokeTest_SharedInstanceOrYearSpecific)
    name = "ODS/API: Deploy and Smoke Test Year-Specific"
    description = "Deploys the ODS/API in Year Specific mode on the Staging server and runs the Smoke Test utility"

    buildNumberPattern = "${OdsApi_OdsApiInitDevUnitTestPackage.depParamRefs.buildNumber}"

    params {
        param("odsapi.package.sdk", "EdFi%odsapi.package.suffix%.OdsApi.Sdk")
        param("odsapi.package.webApi", "EdFi.Ods.WebApi")
        param("odsapi.smokeTestExecutable", "EdFi.SmokeTest.Console")
        param("git.branch.default", "main")
        param("environment.webApi.odsYear", "2020")
        param("octopus.project.name", "Ed-Fi ODS Year-Specific Instance (SQL Server)")
        param("odsapi.package.databases", "EdFi%odsapi.package.suffix%.RestApi.Databases")
        param("environment.webApi.startupType", "YearSpecific")
        param("odsapi.package.smokeTest", "EdFi%odsapi.package.suffix%.SmokeTest.Console")
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_40"
            buildType = "${OdsApi_OdsApiDeployToStaging.id}"
            successfulOnly = true
            branchFilter = ""
        }
    }

    dependencies {
        snapshot(OdsApi_OdsApiDeployToStaging) {
            reuseBuilds = ReuseBuilds.NO
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }
})
