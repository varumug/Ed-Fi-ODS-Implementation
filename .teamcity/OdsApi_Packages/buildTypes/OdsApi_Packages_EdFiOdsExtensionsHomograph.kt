package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_Packages_EdFiOdsExtensionsHomograph : BuildType({
    templates(OdsApi_Packages_EdFiExtensionsTemplate)
    name = "EdFi.Ods.Extensions.Homograph"

    params {
        param("script.initdev.parameters", "-NoDeploy")
        param("extension.project", "EdFi.Ods.Extensions.Homograph")
        param("nuget.pack.files", """Ed-Fi-Extensions\Extensions\EdFi.Ods.Extensions.Homograph\EdFi.Ods.Extensions.Homograph.nuspec""")
        param("git.branch.default", "main")
        param("nuget.package.name", "EdFi.Ods.Extensions.Homograph")
        param("script.create.template", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Modules\create-minimal-template.psm1""")
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Extensions.Homograph")
        param("nuget.package.description", "EdFi.Ods.Extensions.Homograph")
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

    features {
        replaceContent {
            id = "BUILD_EXT_58"
            fileRules = "Ed-Fi-Extensions/Extensions/EdFi.Ods.Extensions.Homograph/*.nuspec"
            pattern = """(?<=<id>)(.*?)(EdFi)(?=\b.*</id>)"""
            replacement = "%odsapi.package.suitenumber%"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
    }
})
