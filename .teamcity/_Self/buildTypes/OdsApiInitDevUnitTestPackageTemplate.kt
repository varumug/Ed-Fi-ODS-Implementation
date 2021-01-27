package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.freeDiskSpace
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApiInitDevUnitTestPackageTemplate : Template({
    name = "ODS/API: InitDev, Unit Test, Package Template"

    buildNumberPattern = "%version%"

    params {
        param("odsapi.build.runPester", "")
        param("odsapi.build.package.webApi.version", "%version%")
        param("odsapi.build.package.sandboxAdmin.version", "%version%")
        param("odsapi.build.noRebuild", "")
        param("odsapi.package.smokeTest", "EdFi%odsapi.package.suffix%.SmokeTest.Console")
        param("odsapi.package.sdk", "EdFi%odsapi.package.suffix%.OdsApi.Sdk")
        param("odsapi.build.package.webApi.id", "EdFi%odsapi.package.suffix%.Ods.WebApi")
        param("odsapi.build.runDotnetTest", "")
        param("odsapi.build.odsTokens", "")
        checkbox("odsapi.build.usePlugins", "true",
                  checked = "true", unchecked = "false")
        param("odsapi.build.package.sandboxAdmin.id", "EdFi%odsapi.package.suffix%.Ods.SandboxAdmin")
        param("odsapi.build.package.databases.version", "%version%")
        param("odsapi.build.package.swaggerUI.version", "%version%")
        param("odsapi.build.runPostman", "")
        param("odsapi.build.script", """Ed-Fi-ODS-Implementation\build.teamcity.ps1""")
        param("odsapi.build.package.databases.id", "EdFi%odsapi.package.suffix%.RestApi.Databases")
        param("odsapi.build.runSmokeTest", "")
        param("odsapi.build.package.swaggerUI.id", "EdFi%odsapi.package.suffix%.Ods.SwaggerUI")
        param("odsapi.build.noDeploy", "")
        param("odsapi.build.engine", "")
        param("odsapi.build.installType", "")
        param("odsapi.build.noCodeGen", "")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOdsImplementation, "=> Ed-Fi-ODS-Implementation")
        root(_Self.vcsRoots.EdFiOds, "=> Ed-Fi-ODS")

        cleanCheckout = true
    }

    steps {
        powerShell {
            name = "build.teamcity.ps1"
            id = "RUNNER_355"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    & %odsapi.build.script%
                """.trimIndent()
            }
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_CUSTOM
            quietPeriod = 120
            triggerRules = "+:**"
            branchFilter = """
                +:*
                -:*-v2
            """.trimIndent()
        }
    }

    failureConditions {
        failOnMetricChange {
            id = "BUILD_EXT_69"
            metric = BuildFailureOnMetric.MetricType.TEST_FAILED_COUNT
            threshold = 0
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = value()
        }
    }

    features {
        freeDiskSpace {
            id = "jetbrains.agent.free.space"
            requiredSpace = "%build.feature.freeDiskSpace%"
            failBuild = true
        }
        commitStatusPublisher {
            id = "BUILD_EXT_45"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%github.accessToken%"
                }
            }
        }
        replaceContent {
            id = "BUILD_EXT_51"
            fileRules = "**/Directory.Build.props"
            pattern = """(<(AssemblyVersion)\s*>).*(<\/\s*\2\s*>)"""
            replacement = "${'$'}1%version.core%.%build.counter%${'$'}3"
        }
        replaceContent {
            id = "BUILD_EXT_52"
            fileRules = "**/Directory.Build.props"
            pattern = """(<(FileVersion)\s*>).*(<\/\s*\2\s*>)"""
            replacement = "${'$'}1%version.core%.%build.counter%${'$'}3"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
        replaceContent {
            id = "BUILD_EXT_53"
            fileRules = "**/Directory.Build.props"
            pattern = """(<(InformationalVersion)\s*>).*(<\/\s*\2\s*>)"""
            replacement = "${'$'}1%version.informational%${'$'}3"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
        replaceContent {
            id = "BUILD_EXT_60"
            fileRules = "**/*.nuspec"
            pattern = """(?<=<id>)(.*?)(EdFi)(?=\b.*</id>)"""
            caseSensitivePattern = false
            replacement = "%odsapi.package.suitenumber%"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
        feature {
            id = "BUILD_EXT_68"
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "trx")
            param("xmlReportParsing.reportDirs", "**/reports/*.*")
        }
    }

    dependencies {
        artifacts(OdsApi_Packages.buildTypes.OdsApi_Packages_EdFiOdsApiSdk) {
            id = "ARTIFACT_DEPENDENCY_41"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%odsapi.package.sdk%.*.nupkg!** => %odsapi.package.sdk%"
        }
        artifacts(RelativeId("OdsImplementationKotlin_EdFiLoadTools_BranchBuild")) {
            id = "ARTIFACT_DEPENDENCY_39"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%odsapi.package.smokeTest%.*.nupkg!** => %odsapi.package.smokeTest%"
        }
    }
})
