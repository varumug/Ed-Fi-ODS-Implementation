package OdsApi

import OdsApi.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.buildReportTab

object Project : Project({
    id("OdsApi")
    name = "ODS/API"
    description = "Primary ODS/API builds"

    buildType(OdsApi_Deploy_And_Smoke_Test_YearSpecific)
    buildType(OdsApi_OdsApiPublishToAzure)
    buildType(OdsApi_OdsApiSmokeTestStaging)
    buildType(OdsApi_OdsApiInitDevUnitTestPackage)
    buildType(OdsApi_OdsApiDeployToStaging)
    buildType(OdsApi_Deploy_And_SmokeTest_SharedInstance)
    buildType(OdsApi_OdsApiInitDevIntegrationTest)
    buildType(OdsApi_OdsApiDeployLandingPageToStaging)

    template(OdsApi_OdsApi_Deploy_And_SmokeTest_SharedInstanceOrYearSpecific)

    params {
        param("script.initdev.parameters", "-UsePlugins")
        param("odsapi.package.webApi", "EdFi.Ods.WebApi")
        param("nuget.pack.properties.copyright", "Copyright ©Ed-Fi Alliance, LLC. 2020")
        param("octopus.release.channel", "v%version.core%")
        param("odsapi.smokeTestExecutable", "EdFi.SmokeTest.Console")
        param("nuget.pack.properties.owners", "Ed-Fi Alliance")
        param("nuget.pack.properties.authors", "Ed-Fi Alliance")
        param("odsapi.package.sandboxAdmin", "EdFi.Ods.SandboxAdmin")
        param("odsapi.package.swaggerUI", "EdFi.Ods.SwaggerUI")
        param("odsapi.package.smokeTest", "EdFi%odsapi.package.suffix%.SmokeTest.Console")
        param("script.initdev", """Ed-Fi-ODS-Implementation\Initialize-PowershellForDevelopment.ps1""")
        param("odsapi.package.sdk", "EdFi%odsapi.package.suffix%.OdsApi.Sdk")
        param("version.prerelease.prefix", "b")
        param("nuget.pack.output", "NugetPackages")
        param("nuget.pack.properties.default", """
            configuration=%msbuild.buildConfiguration%
            authors=%nuget.pack.properties.authors%
            owners=%nuget.pack.properties.owners%
            copyright=%nuget.pack.properties.copyright%
        """.trimIndent())
        param("version.patch", "0")
        param("nuget.pack.parameters", "-NoPackageAnalysis -NoDefaultExcludes")
        param("version.major", "5")
        param("odsapi.package.databases", "EdFi.RestApi.Databases")
        param("version.minor", "2")
        param("script.build.management", """Ed-Fi-ODS-Implementation\logistics\scripts\modules\build-management.psm1""")
        param("version.suite", "3")
        param("datastandard.version", "v3.2")
        param("odsapi.package.suffix", ".Suite%version.suite%")
    }

    features {
        buildReportTab {
            id = "PROJECT_EXT_18"
            title = "NUnit"
            startPage = "TestResult.xml"
        }
    }
    buildTypesOrder = arrayListOf(OdsApi_OdsApiInitDevUnitTestPackage, OdsApi_OdsApiInitDevIntegrationTest, OdsApi_OdsApiDeployToStaging, OdsApi_OdsApiSmokeTestStaging, OdsApi_Deploy_And_Smoke_Test_YearSpecific, OdsApi_Deploy_And_SmokeTest_SharedInstance, OdsApi_OdsApiDeployLandingPageToStaging, OdsApi_OdsApiPublishToAzure)
    subProjectsOrder = arrayListOf(RelativeId("OdsApi_PostgreSQL"), RelativeId("OdsApi_Cloud"), RelativeId("OdsApi_Packages"), RelativeId("OdsApi_MetaEdIntegration"))

    subProject(OdsApi_MetaEdIntegration.Project)
    subProject(OdsApi_Cloud.Project)
    subProject(OdsApi_Packages.Project)
    subProject(OdsApi_Utilities.Project)
    subProject(OdsApi_PostgreSQL.Project)
})
