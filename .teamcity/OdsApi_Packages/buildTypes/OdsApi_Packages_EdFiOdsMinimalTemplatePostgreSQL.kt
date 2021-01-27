package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object OdsApi_Packages_EdFiOdsMinimalTemplatePostgreSQL : BuildType({
    templates(OdsApi_Packages_CreateDatabaseTemplate)
    name = "EdFi.Ods.Minimal.Template.PostgreSQL"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""

    params {
        param("script.initdev.parameters", "-NoCredentials -NoDeploy -Engine PostgreSQL")
        param("script.create.template.parameters", """-samplePath "./Ed-Fi-Standard/Descriptors/"  -noExtensions -Engine PostgreSQL""")
        param("nuget.pack.files", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Database\EdFi.Ods.Minimal.Template.PostgreSQL.nuspec""")
        param("git.branch.default", "main")
        param("nuget.package.name", "EdFi.Ods.Minimal.Template.PostgreSQL")
        param("script.create.template", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Modules\create-minimal-template.psm1""")
        param("nuget.pack.properties", """
            id=%PackageId%
            title=%PackageId%
            description=%nuget.package.description%
        """.trimIndent())
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Minimal.Template.PostgreSQL")
        param("nuget.package.description", "EdFi Ods Minimal Template Database for PostgreSQL")
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
                    Initialize-MinimalTemplate %script.create.template.parameters%
                """.trimIndent()
            }
            param("jetbrains_powershell_script_file", "Ed-Fi-Common/logistics/scripts/activities/build/create-populated/create-populated.ps1")
        }
        powerShell {
            name = "Remove-EdFiDatabases"
            id = "RUNNER_395"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    ${'$'}ErrorActionPreference = 'Stop'
                    
                    Import-Module "%teamcity.build.checkoutDir%\%script.build.management%"
                    
                    Remove-EdFiDatabases -Force -Engine PostgreSQL
                """.trimIndent()
            }
        }
    }
})
