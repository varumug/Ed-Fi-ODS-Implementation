package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange

object OdsApi_OdsApiInitDevUnitTestPackage : BuildType({
    templates(_Self.buildTypes.OdsApiInitDevUnitTestPackageTemplate)
    name = "ODS/API: InitDev, Unit Test, Package"

    artifactRules = """
        %nuget.pack.output%/** => .
        *.log => .
    """.trimIndent()
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        param("odsapi.build.runSmokeTest", "false")
        param("odsapi.build.runPostman", "false")
    }

    vcs {
        branchFilter = "+:*"
    }

    steps {
        powerShell {
            name = "Create EdF.Ods.WebApi.Prerelease for Staging"
            id = "RUNNER_485"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    if (Test-Path 'Ed-Fi-ODS-Implementation/build.teamcity.ps1') {
                        . "%teamcity.build.checkoutDir%/%script.initdev%"
                    
                        ${'$'}params = @{
                            ProjectPath           = 'Ed-Fi-ODS-Implementation/Application/EdFi.Ods.WebApi'
                            PackageDefinitionFile = 'Ed-Fi-ODS-Implementation/Application/EdFi.Ods.WebApi/bin/**/**/publish/EdFi.Ods.WebApi.Prerelease.nuspec'
                            PackageId             = 'EdFi%odsapi.package.suffix%.Ods.WebApi.Prerelease'
                            Version               = '%version%'
                            Properties            = (Get-DefaultNuGetProperties)
                            OutputDirectory       = 'Ed-Fi-ODS-Implementation/packages'
                        }
                        New-WebPackage @params
                    
                        Write-Host "##teamcity[publishArtifacts 'Ed-Fi-ODS-Implementation/packages']"
                    }
                """.trimIndent()
            }
        }
        stepsOrder = arrayListOf("RUNNER_41", "RUNNER_355", "RUNNER_438", "RUNNER_392", "RUNNER_421", "RUNNER_403", "RUNNER_362", "RUNNER_89", "RUNNER_456", "RUNNER_90", "RUNNER_485")
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
        replaceContent {
            id = "BUILD_EXT_60"
            enabled = false
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
    
    disableSettings("ARTIFACT_DEPENDENCY_39", "ARTIFACT_DEPENDENCY_41", "RUNNER_154", "RUNNER_362", "RUNNER_403", "RUNNER_417", "RUNNER_421", "RUNNER_93")
})
