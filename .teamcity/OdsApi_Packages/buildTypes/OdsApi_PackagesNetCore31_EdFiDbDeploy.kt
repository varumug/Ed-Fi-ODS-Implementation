package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPack

object OdsApi_PackagesNetCore31_EdFiDbDeploy : BuildType({
    templates(OdsApi_PackagesNetCore31_NetCore31Packages)
    name = "EdFi.Db.Deploy"

    artifactRules = """%nuget.pack.output%\%PackageId%.%version.core%*.nupkg"""

    params {
        param("project.file.csproj", "%project.directory%/%project.name%/%project.name%.csproj")
        param("pathToSolutionFile", "src/EdFi.Db.Deploy.sln")
        param("msbuild.exe", "")
        param("PackageId", "EdFi%odsapi.package.suffix%.Db.Deploy")
        param("project.name", "EdFi.LoadTools")
        param("pathToTestFile", "tests/**/%msbuild.buildConfiguration%/EdFi.Db.Deploy.Tests.dll")
        param("version.major", "2")
        param("project.directory", "Ed-Fi-ODS/Utilities/DataLoading")
        param("dotnet.pack.parameters", "-p:NoWarn=NU5123 -p:PackageId=%PackageId%")
    }

    vcs {
        root(_Self.vcsRoots.EdFiDatabases)
    }

    steps {
        dotnetPack {
            name = "Pack Prerelease version - Library"
            id = "RUNNER_400"
            enabled = false
            projects = "%project.file.csproj%"
            configuration = "%msbuild.configuration%"
            outputDir = "%teamcity.build.checkoutDir%"
            skipBuild = true
            args = """-p:VersionPrefix=%version.core% --version-suffix "%version.prerelease%" %dotnet.pack.args%"""
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        stepsOrder = arrayListOf("RUNNER_191", "RUNNER_189", "RUNNER_319", "RUNNER_400", "RUNNER_193", "RUNNER_194", "RUNNER_195")
    }
})
