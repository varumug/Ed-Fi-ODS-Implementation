package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.DotnetPackStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPack

object OdsApi_Packages_EdFiOdsCodeGen : BuildType({
    templates(OdsApi_PackagesNetCore31_NetCore31Packages)
    name = "EdFi.Ods.CodeGen"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""

    params {
        param("pathToUtilityProjectFile", """Utilities\CodeGeneration\EdFi.Ods.CodeGen.Console\EdFi.Ods.CodeGen.Console.csproj""")
        param("pathToSolutionFile", """Utilities\CodeGeneration\EdFi.Ods.CodeGen\EdFi.Ods.CodeGen.csproj""")
        param("msbuild.exe", "")
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.CodeGen")
        param("pathToTestFile", """Utilities\CodeGeneration\**\bin\%msbuild.buildConfiguration%\**\*Tests.dll""")
        param("version.major", "5")
        param("dotnet.pack.parameters", "-p:NoWarn=NU5123 -p:PackageId=%PackageId%")
    }

    vcs {
        root(_Self.vcsRoots.EdFiOds)
    }

    steps {
        dotnetPack {
            name = "Pack Prerelease version"
            id = "RUNNER_193"
            projects = "%pathToSolutionFile%"
            configuration = "%msbuild.buildConfiguration%"
            outputDir = "%nuget.pack.output%"
            skipBuild = true
            args = "-p:PackageVersion=%version% %dotnet.pack.parameters%"
            logging = DotnetPackStep.Verbosity.Normal
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPack {
            name = "Pack Release version"
            id = "RUNNER_194"
            projects = "%pathToSolutionFile%"
            configuration = "%msbuild.buildConfiguration%"
            outputDir = "%nuget.pack.output%"
            skipBuild = true
            args = "-p:PackageVersion=%version.core% %dotnet.pack.parameters%"
            logging = DotnetPackStep.Verbosity.Normal
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }
})
