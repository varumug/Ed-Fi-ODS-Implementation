package OdsApi_Utilities.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.VisualStudioStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.visualStudio

object OdsApi_Utilities_EdFiSdkGen : BuildType({
    name = "EdFi.SdkGen"

    artifactRules = """EdFi.SdkGen.Console\bin\%BuildConfiguration%\**=>EdFi.SdkGen.zip!/EdFi.SdkGen.Console"""

    params {
        param("BuildConfiguration", "Release")
        param("NuGetFeeds", """
            https://www.myget.org/F/ed-fi/
            https://www.nuget.org/api/v2/
        """.trimIndent())
        param("pathToSolutionFile", "EdFi.SdkGen.sln")
    }

    vcs {
        root(OdsApi_Utilities.vcsRoots.OdsApi_Utilities_EdFiOds_2, "+:Utilities/SdkGen => .")
    }

    steps {
        nuGetInstaller {
            name = "Restore NuGet packages"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "%pathToSolutionFile%"
            sources = "%NuGetFeeds%"
            param("nuget.updatePackages.include.prerelease", "true")
        }
        visualStudio {
            name = "Build Visual Studio Project"
            path = "%pathToSolutionFile%"
            version = VisualStudioStep.VisualStudioVersion.vs2019
            runPlatform = VisualStudioStep.Platform.x86
            msBuildVersion = VisualStudioStep.MSBuildVersion.V16_0
            msBuildToolsVersion = VisualStudioStep.MSBuildToolsVersion.V16_0
            targets = "Clean;Build"
            configuration = "%BuildConfiguration%"
        }
    }
})
