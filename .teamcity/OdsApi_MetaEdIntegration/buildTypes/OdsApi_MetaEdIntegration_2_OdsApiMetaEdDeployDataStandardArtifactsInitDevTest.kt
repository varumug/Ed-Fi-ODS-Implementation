package OdsApi_MetaEdIntegration.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

object OdsApi_MetaEdIntegration_2_OdsApiMetaEdDeployDataStandardArtifactsInitDevTest : BuildType({
    templates(_Self.buildTypes.OdsApiInitDevUnitTestPackageTemplate)
    name = "ODS/API+MetaEd: Deploy Data Standard Artifacts, InitDev, Test"

    artifactRules = """
        %nuget.pack.output%/** => .
        %metaed.deploy.output%
    """.trimIndent()

    params {
        param("metaed.deploy", """run metaed:deploy -- --source %system.teamcity.build.checkoutDir%\%datastandard.source% --target %system.teamcity.build.checkoutDir% --core --defaultPluginTechVersion %metaed.technologyVersion%""")
        param("metaed.deploy.output", "Ed-Fi-ODS/Application/EdFi.Ods.Standard/Artifacts/** => DataStandardMetaEdDeploy.zip!Ed-Fi-ODS/Application/EdFi.Ods.Standard/Artifacts/")
        param("version.major", "5")
        param("datastandard.source", "MetaEdSource")
    }

    steps {
        powerShell {
            name = "Select-NodeVersion"
            id = "RUNNER_408"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    Import-Module "%teamcity.build.checkoutDir%\%script.build.management%"
                    
                    ${'$'}params = @{
                        version= "%node.version%"
                    }
                    Select-NodeVersion @params
                """.trimIndent()
            }
        }
        step {
            name = "run metaed:deploy"
            id = "RUNNER_409"
            type = "jonnyzzz.npm"
            param("teamcity.build.workingDir", "%metaed.source%")
            param("npm_commands", "%metaed.deploy%")
        }
        stepsOrder = arrayListOf("RUNNER_408", "RUNNER_409", "RUNNER_41", "RUNNER_355", "RUNNER_93", "RUNNER_417", "RUNNER_421", "RUNNER_403", "RUNNER_362", "RUNNER_89", "RUNNER_90")
    }

    triggers {
        schedule {
            id = "TRIGGER_31"
            schedulingPolicy = daily {
                hour = 5
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            enforceCleanCheckout = true
            param("revisionRuleBuildBranch", "<default>")
        }
    }

    dependencies {
        artifacts(AbsoluteId("MetaEd_DataStandard_MetaEdJsDataStandardStaging32")) {
            id = "ARTIFACT_DEPENDENCY_47"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "%datastandard.source%.zip!** => %datastandard.source%"
        }
        artifacts(AbsoluteId("MetaEd_MetaEdJsCi")) {
            id = "ARTIFACT_DEPENDENCY_46"
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = """
                %metaed.source%.zip!** => %metaed.source%
            """.trimIndent()
        }
    }
    
    disableSettings("vcsTrigger")
})
