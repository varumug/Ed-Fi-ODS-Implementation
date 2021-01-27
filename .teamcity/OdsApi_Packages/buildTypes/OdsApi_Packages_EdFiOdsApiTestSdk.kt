package OdsApi_Packages.buildTypes

import OdsApi_Utilities.buildTypes.OdsApi_Utilities_EdFiSdkGen
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetFeedCredentials
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.VisualStudioStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetRestore
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.visualStudio
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object OdsApi_Packages_EdFiOdsApiTestSdk : BuildType({
    name = "EdFi.Ods.Api.TestSdk"
    description = "Test SDK with sample extensions for Smoke Test"

    buildNumberPattern = "%version%"

    params {
        param("OctopusDeployChannel", "v%MajorPackageVersion%.%MinorPackageVersion%.%PatchPackageVersion%%OctopusDeployChannelPrereleaseSuffix%")
        param("OctopusDeployChannelPrereleaseSuffix", "")
        param("MinorPackageVersion", "2")
        param("pathToSolutionFile", """netcoreapp3.1\csharp\EdFi.OdsApi.Sdk.sln""")
        param("MajorPackageVersion", "5")
        param("SdkCliVersion", "2.4.15")
        param("NuGetAuthors", "Ed-Fi Alliance")
        param("pathToNuspecFile", """netcoreapp3.1\EdFi.OdsApi.Sdk.nuspec""")
        param("OctopusDeployDefaultAPIMetadataUrl", "https://api-stage.ed-fi.org/%OctopusDeployChannel%/api/metadata")
        param("NuGetCopyright", "Copyright ©Ed-Fi Alliance, LLC. 2019")
        param("PackageVersion", "%MajorPackageVersion%.%MinorPackageVersion%.%PatchPackageVersion%")
        param("SdkGenerationApiMetadataUrl", "%OctopusDeployDefaultAPIMetadataUrl%?sdk=true")
        param("env.VSS_NUGET_EXTERNAL_FEED_ENDPOINTS", """{"endpointCredentials": [{"endpoint": "%azureArtifacts.feed.nuget%","username": "%azureArtifacts.edFiBuildAgent.userName%","password": "%azureArtifacts.edFiBuildAgent.accessToken%"}]}""")
        param("BuildConfiguration", "Release")
        param("PatchPackageVersion", "0")
        param("PrereleasePackageVersion", "%PackageVersion%-beta%build.number%")
        param("DefaultNugetProperties", """
            Configuration=%BuildConfiguration%
            authors=%NuGetAuthors%
            owners=%NuGetOwners%
            copyright=%NuGetCopyright%
        """.trimIndent())
        param("NuGetOwners", "Ed-Fi Alliance")
        param("NugetPackageId", "EdFi%odsapi.package.suffix%.OdsApi.TestSdk")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOds, "+:. => Ed-Fi-ODS")
    }

    steps {
        exec {
            name = "Run EdFi.SdkGen.Console.exe to generate C# SDK code"
            workingDir = """%teamcity.build.checkoutDir%\netcoreapp3.1"""
            path = "EdFi.SdkGen.Console.exe"
            arguments = """-m "%SdkGenerationApiMetadataUrl%" -v "%SdkCliVersion%" --include-profiles --include-composites --include-identity"""
        }
        dotnetRestore {
            name = "Restore NuGet packages"
            projects = "%pathToSolutionFile%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        visualStudio {
            name = "Build Solution"
            path = "%pathToSolutionFile%"
            version = VisualStudioStep.VisualStudioVersion.vs2019
            runPlatform = VisualStudioStep.Platform.x86
            msBuildVersion = VisualStudioStep.MSBuildVersion.V16_0
            msBuildToolsVersion = VisualStudioStep.MSBuildToolsVersion.V16_0
            configuration = "Release"
        }
        powerShell {
            name = "Replace PackageId & PackageTitle"
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    (Get-Content -path %pathToNuspecFile% -Raw) | ForEach-Object {
                        ${'$'}_.replace('<id>EdFi%odsapi.package.suffix%.OdsApi.Sdk</id>','<id>EdFi%odsapi.package.suffix%.OdsApi.TestSdk</id>').replace('<title>EdFi%odsapi.package.suffix%.OdsApi.Sdk</title>','<title>EdFi%odsapi.package.suffix%.OdsApi.TestSdk</title>')
                     } | Set-Content -Path %pathToNuspecFile%
                      Write-Host "Updating package id to EdFi"+%odsapi.package.suffix%+".OdsApi.TestSdk"
                """.trimIndent()
            }
            noProfile = false
        }
        nuGetPack {
            name = "Pack Prerelease version"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%pathToNuspecFile%"
            version = "%PrereleasePackageVersion%"
            outputDir = "nuget"
            cleanOutputDir = false
            publishPackages = true
            properties = "%DefaultNugetProperties%"
        }
        nuGetPack {
            name = "Pack Release version"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "%pathToNuspecFile%"
            version = "%PackageVersion%"
            outputDir = "nuget"
            cleanOutputDir = false
            publishPackages = true
            properties = "%DefaultNugetProperties%"
        }
        powerShell {
            name = "Publish Prerelease Version to Azure Artifacts"
            workingDir = "nuget"
            scriptMode = script {
                content = """
                    ${'$'}src = "https://pkgs.dev.azure.com/ed-fi-alliance/Ed-Fi-Alliance-OSS/_packaging/EdFi/nuget/v3/index.json"
                    nuget push -source ${'$'}src -apikey az %NugetPackageId%.%PrereleasePackageVersion%.nupkg
                """.trimIndent()
            }
        }
    }

    triggers {
        vcs {
            triggerRules = """+:Ed-Fi-ODS\Utilities\SdkGen\**"""
        }
        finishBuildTrigger {
            buildType = "${OdsApi_Utilities_EdFiSdkGen.id}"
        }
    }

    features {
        feature {
            type = "JetBrains.AssemblyInfo"
            param("assembly-format", "%MajorPackageVersion%.%MinorPackageVersion%.%PatchPackageVersion%.%system.build.number%")
        }
        replaceContent {
            fileRules = "**/*.nuspec"
            pattern = """(?<=<id>)(.*?)(EdFi)(?=\b.*</id>)"""
            replacement = "%odsapi.package.suitenumber%"
            encoding = FileContentReplacer.FileEncoding.UTF_8
            customEncodingName = "UTF-8"
        }
        nuGetFeedCredentials {
            feedUrl = "%azureArtifacts.feed.nuget%"
            username = "%azureArtifacts.edFiBuildAgent.userName%"
            password = "%azureArtifacts.edFiBuildAgent.accessToken%"
        }
    }

    dependencies {
        artifacts(OdsApi_Utilities.buildTypes.OdsApi_Utilities_EdFiSdkGen) {
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "EdFi.SdkGen.zip!/EdFi.SdkGen.Console"
        }
    }
})
