package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_Packages_CreateDatabasePackageTemplate : Template({
    name = "Create Database Package Template"

    artifactRules = "%nuget.pack.output%/*.nupkg"
    buildNumberPattern = "%version%"
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        param("script.create.database.package.parameters", "-Output %nuget.pack.output% -DatabaseType %script.create.database.package.database.type%")
        param("nuget.pack.properties", "")
        param("PackageId", "")
        param("script.create.database.package", "Ed-Fi-ODS-Implementation/logistics/scripts/activities/build/create-database-package.ps1")
        param("nuget.pack.files", """
            %nuget.pack.output%/EdFi.Database.%script.create.database.package.database.type%.nuspec
            %nuget.pack.output%/EdFi.Database.%script.create.database.package.database.type%.BACPAC.nuspec
            %nuget.pack.output%/EdFi.Database.%script.create.database.package.database.type%.PostgreSQL.nuspec
        """.trimIndent())
    }

    vcs {
        root(_Self.vcsRoots.EdFiOds, ". => Ed-Fi-ODS")
        root(_Self.vcsRoots.EdFiOdsImplementation, ". => Ed-Fi-ODS-Implementation")

        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        powerShell {
            id = "RUNNER_464"
            formatStderrAsError = true
            scriptMode = script {
                content = ". %teamcity.build.checkoutDir%/%script.create.database.package% %script.create.database.package.parameters%"
            }
        }
        nuGetPack {
            name = "Pack Prerelease version"
            id = "RUNNER_468"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%nuget.pack.files%"
            version = "%version%"
            outputDir = "%nuget.pack.output%"
            cleanOutputDir = false
            publishPackages = true
            properties = """
                %nuget.pack.properties.default%
                %nuget.pack.properties%
            """.trimIndent()
            args = "%nuget.pack.parameters%"
        }
        nuGetPack {
            name = "Pack Release version"
            id = "RUNNER_469"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%nuget.pack.files%"
            version = "%version.core%"
            outputDir = "%nuget.pack.output%"
            cleanOutputDir = false
            publishPackages = true
            properties = """
                %nuget.pack.properties.default%
                %nuget.pack.properties%
            """.trimIndent()
            args = "%nuget.pack.parameters%"
        }
        nuGetPublish {
            name = "Publish Prerelease Version"
            id = "RUNNER_467"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.pack.output%\*.%version%.nupkg"""
            serverUrl = "%azureArtifacts.feed.nuget%"
            apiKey = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            triggerRules = "+:**.sql"
        }
    }

    features {
        nuGetFeedCredentials {
            id = "BUILD_EXT_70"
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
        commitStatusPublisher {
            id = "BUILD_EXT_72"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%EdFiBuildAgent-GitHubKeyPasshrase%"
                }
            }
        }
    }
})
