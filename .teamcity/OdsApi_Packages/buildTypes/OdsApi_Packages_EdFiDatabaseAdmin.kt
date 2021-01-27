package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish

object OdsApi_Packages_EdFiDatabaseAdmin : BuildType({
    templates(OdsApi_Packages_CreateDatabasePackageTemplate)
    name = "EdFi.Database.Admin"

    params {
        select("script.create.database.package.database.type", "Admin",
                options = listOf("Admin", "Security"))
    }

    steps {
        nuGetPublish {
            name = "Publish Prerelease Version"
            id = "RUNNER_467"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.pack.output%\*.%version%.nupkg"""
            serverUrl = "%azureArtifacts.feed.nuget%"
            apiKey = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }
})
