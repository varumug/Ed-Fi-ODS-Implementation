package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_Packages_EdFiOdsExtensionsSample : BuildType({
    templates(OdsApi_Packages_EdFiExtensionsTemplate)
    name = "EdFi.Ods.Extensions.Sample"

    params {
        param("extension.project", "EdFi.Ods.Extensions.Sample")
        param("script.initdev.parameters", "-NoCredentials -NoDeploy")
    }

    steps {
        powerShell {
            name = "Run CodeGen"
            id = "RUNNER_458"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    . "%teamcity.build.checkoutDir%\%script.initdev%"
                    Invoke-CodeGen -Engine SQLServer -ExtensionPaths  %teamcity.build.checkoutDir%\Ed-Fi-Extensions\Extensions\%extension.project%
                """.trimIndent()
            }
        }
        stepsOrder = arrayListOf("RUNNER_395", "RUNNER_398", "RUNNER_196", "RUNNER_197", "RUNNER_404", "RUNNER_199")
    }
})
