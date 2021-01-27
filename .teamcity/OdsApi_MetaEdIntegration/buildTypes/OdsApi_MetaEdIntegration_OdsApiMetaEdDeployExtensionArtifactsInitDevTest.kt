package OdsApi_MetaEdIntegration.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

object OdsApi_MetaEdIntegration_OdsApiMetaEdDeployExtensionArtifactsInitDevTest : BuildType({
    templates(_Self.buildTypes.OdsApiInitDevUnitTestPackageTemplate)
    name = "ODS/API+MetaEd: Deploy Extension Artifacts, InitDev, Test"

    artifactRules = """
        %nuget.pack.output%/** => .
        %metaed.deploy.output%
    """.trimIndent()

    params {
        param("metaed.deploy", """run metaed:deploy -- --source %system.teamcity.build.checkoutDir%\%datastandard.source% %extension.source% --target %system.teamcity.build.checkoutDir% --defaultPluginTechVersion %metaed.technologyVersion% --suppressDelete""")
        param("odsapi.package.webApi", "EdFi.Ods.WebApi")
        param("metaed.deploy.output", """
            Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Sample\Artifacts\** => ExtensionMetaEdDeploy.zip!Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Sample\Artifacts\
            Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Homograph\Artifacts\** => ExtensionMetaEdDeploy.zip!Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Homograph\Artifacts\
            Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.TPDM\Artifacts\** => ExtensionMetaEdDeploy.zip!Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.TPDM\Artifacts\
        """.trimIndent())
        param("extension.project", "EdFi.Ods.Extensions.TPDM")
        param("version.major", "5")
        param("extension.source", """%system.teamcity.build.checkoutDir%\Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Homograph %system.teamcity.build.checkoutDir%\Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Sample %system.teamcity.build.checkoutDir%\Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.TPDM""")
    }

    vcs {
        root(_Self.vcsRoots.EdFiExtensions, "=> Ed-Fi-Extensions")
    }

    steps {
        step {
            name = "run metaed:deploy"
            id = "RUNNER_412"
            type = "jonnyzzz.npm"
            param("teamcity.build.workingDir", "%metaed.source%")
            param("npm_commands", "%metaed.deploy%")
        }
        stepsOrder = arrayListOf("RUNNER_421", "RUNNER_412", "RUNNER_41", "RUNNER_355", "RUNNER_93", "RUNNER_417", "RUNNER_403", "RUNNER_362", "RUNNER_89", "RUNNER_90")
    }

    triggers {
        schedule {
            id = "TRIGGER_36"
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
        artifacts(AbsoluteId("MetaEd_MetaEdJsCi")) {
            id = "ARTIFACT_DEPENDENCY_48"
            buildRule = lastSuccessful()
            artifactRules = "%metaed.source%.zip!** => %metaed.source%"
        }
    }
    
    disableSettings("RUNNER_89", "RUNNER_90", "vcsTrigger")
})
