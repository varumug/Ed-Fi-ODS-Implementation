package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object OdsApi_Packages_EdFiOdsMinimalTemplate : BuildType({
    templates(OdsApi_Packages_CreateDatabaseTemplate)
    name = "EdFi.Ods.Minimal.Template"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""

    params {
        param("script.initdev.parameters", "-NoCredentials -NoDeploy")
        param("script.create.template.parameters", """-samplePath "./Ed-Fi-Standard/Descriptors/"  -noExtensions""")
        param("nuget.pack.files", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Database\EdFi.Ods.Minimal.Template.nuspec""")
        param("git.branch.default", "main")
        param("nuget.package.name", "EdFi.Ods.Minimal.Template")
        param("script.create.template", """Ed-Fi-ODS-Implementation\DatabaseTemplate\Modules\create-minimal-template.psm1""")
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Minimal.Template")
        param("nuget.pack.properties", """
            id=%PackageId%
            title=%PackageId%
            description=%nuget.package.description%
        """.trimIndent())
        param("nuget.package.description", "EdFi Ods Minimal Template Database")
        param("version.major", "5")
    }
})
