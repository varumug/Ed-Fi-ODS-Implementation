package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_Packages_EdFiOdsPopulatedTemplate : BuildType({
    templates(OdsApi_Packages_CreateDatabaseTemplate)
    name = "EdFi.Ods.Populated.Template"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""

    params {
        param("script.initdev.parameters", "-NoCredentials -NoDeploy")
        param("nuget.pack.files", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Database\EdFi.Ods.Populated.Template.nuspec""")
        param("nuget.package.name", "EdFi.Ods.Populated.Template")
        param("script.create.template", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Modules\create-populated-template.psm1""")
        param("nuget.pack.properties", """
            id=%PackageId%
            title=%PackageId%
            description=%nuget.package.description%
        """.trimIndent())
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Populated.Template")
        param("nuget.package.description", "EdFi Ods Populated Template Database")
        param("version.major", "5")
    }

    steps {
        powerShell {
            name = "Create Database Template (no extensions)"
            id = "RUNNER_196"
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    Import-Module -Force -Scope Global "%teamcity.build.checkoutDir%\%script.create.template%"
                    Initialize-PopulatedTemplate %script.create.template.parameters%
                """.trimIndent()
            }
            param("jetbrains_powershell_script_file", "Ed-Fi-Common/logistics/scripts/activities/build/create-populated/create-populated.ps1")
        }
    }
})
