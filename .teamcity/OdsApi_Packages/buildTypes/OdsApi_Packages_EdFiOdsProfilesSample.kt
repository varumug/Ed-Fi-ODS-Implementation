package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_Packages_EdFiOdsProfilesSample : BuildType({
    templates(OdsApi_Packages_EdFiExtensionsTemplate)
    name = "EdFi.Ods.Profiles.Sample"

    params {
        param("script.initdev.parameters", "-NoCredentials -NoDeploy")
        param("nuget.pack.files", """Ed-Fi-Extensions\Extensions\EdFi.Ods.Profiles.Sample\EdFi.Ods.Profiles.Sample.nuspec""")
        param("extension.project", "EdFi.Ods.Profiles.Sample")
        param("nuget.package.name", "EdFi.Ods.Profiles.Sample")
        param("script.create.template", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Modules\create-minimal-template.psm1""")
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Profiles.Sample")
        param("nuget.package.description", "EdFi.Ods.Profiles.Sample")
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
        stepsOrder = arrayListOf("RUNNER_447", "RUNNER_458", "RUNNER_398", "RUNNER_197", "RUNNER_404", "RUNNER_199")
    }

    features {
        replaceContent {
            id = "BUILD_EXT_58"
            fileRules = "Ed-Fi-Extensions/Extensions/%extension.project%/%extension.project%.nuspec"
            pattern = """(?<=<id>)(.*?)(EdFi)(?=\b.*</id>)"""
            replacement = "%odsapi.package.suitenumber%"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
    }
})
