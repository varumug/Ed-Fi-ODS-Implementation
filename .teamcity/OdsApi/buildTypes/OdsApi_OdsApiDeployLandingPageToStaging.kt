package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_OdsApiDeployLandingPageToStaging : BuildType({
    name = "ODS/API: Deploy Landing Page to Staging"

    artifactRules = "%nuget.pack.output%/** => ."
    buildNumberPattern = "%version%"

    params {
        param("nuget.pack.files", """Ed-Fi-ODS-Implementation\LandingPage\EdFi.LandingPage.nuspec""")
        param("octopus.release.channel", "")
        param("git.branch.default", "main")
        param("octopus.deploy.arguments", "--deploymenttimeout=%octopus.deploy.timeout% --packageversion=%version%")
        param("octopus.nuget.feed", "%octopus.server%/nuget/packages")
        param("nuget.package.description", "%nuget.package.name%")
        param("octopus.release.project", "Landing Page")
        param("octopus.release.version", "%version%")
        param("nuget.package.name", "EdFi.LandingPage")
        param("octopus.deploy.timeout", "00:45:00")
        param("nuget.pack.properties", """
            id=%nuget.package.name%
            title=%nuget.package.name%
            description=%nuget.package.description%
        """.trimIndent())
        param("octopus.deploy.environment", "Staging")
        param("nuget.packages", "NugetPackages")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOdsImplementation, "=> Ed-Fi-ODS-Implementation")

        cleanCheckout = true
    }

    steps {
        nuGetPack {
            name = "Pack Prerelease version"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%nuget.pack.files%"
            version = "%version%"
            outputDir = "%nuget.pack.output%"
            cleanOutputDir = true
            publishPackages = true
            properties = """
                %nuget.pack.properties.default%
                %nuget.pack.properties%
            """.trimIndent()
            args = "%nuget.pack.parameters%"
        }
        nuGetPublish {
            name = "Force Publishing NuGet Packages to Octopus Feed"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = """%nuget.packages%\*.%version%.nupkg"""
            serverUrl = "%octopus.nuget.feed%"
            apiKey = "%OctopusAPIKey%"
        }
        step {
            name = "Create Octopus Release and Deploy It to Staging"
            type = "octopus.create.release"
            param("octopus_additionalcommandlinearguments", "%octopus.deploy.arguments%")
            param("octopus_waitfordeployments", "true")
            param("octopus_channel_name", "%octopus.release.channel%")
            param("octopus_version", "3.0+")
            param("octopus_host", "%octopus.server%")
            param("octopus_project_name", "%octopus.release.project%")
            param("octopus_deployto", "%octopus.deploy.environment%")
            param("secure:octopus_apikey", "%OctopusAPIKey%")
            param("octopus_releasenumber", "%octopus.release.version%")
        }
    }

    triggers {
        vcs {
            triggerRules = """+:Ed-Fi-ODS-Implementation\LandingPage\**"""
            branchFilter = "+:<default>"
        }
    }

    features {
        swabra {
        }
        replaceContent {
            fileRules = "**/*.nuspec"
            pattern = """(<(id)\s*>)(.*)(<\/\s*\2\s*>)"""
            caseSensitivePattern = false
            replacement = "${'$'}1${'$'}3%odsapi.package.suffix%${'$'}4"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
    }
})
