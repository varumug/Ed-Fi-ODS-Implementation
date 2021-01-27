package _Self

import _Self.buildTypes.*
import _Self.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object ODSPlatform : Project({
    description = "Projects Owned by the ODS Platform Team"

    vcsRoot(EdFiOds)
    vcsRoot(EdFiOdsImplementation)
    vcsRoot(EdFiDatabases)
    vcsRoot(Packages_EdFiStandard)
    vcsRoot(EdFiExtensions)
    vcsRoot(EdFiTpdmExtension)
    vcsRoot(TeamCityConfigsOdsImplementation)
    vcsRoot(OdsApi_PackagesNetCore31_EdFiMigrationUtility)

    template(OdsApiInitDevUnitTestPackageTemplate)
    template(OdsApiDeployToStagingTemplate)
    template(OdsApi_OdsApiSmokeTestStagingTemplate)

    params {
        param("build.feature.freeDiskSpace", "8gb")
        param("env.msbuild_buildConfiguration", "%msbuild.buildConfiguration%")
        param("env.msbuild_exe", "%msbuild.exe%")
        param("git.branch.default", "main")
        param("git.branch.release", "master")
        param("git.branch.specification", """
            refs/heads/(*)
            refs/tags/(*)
        """.trimIndent())
        param("git.repo.implementation", "Ed-Fi-ODS-Implementation")
        param("git.repo.ods", "Ed-Fi-ODS")
        param("github.organization.teamCity", "Ed-Fi-Alliance")
        param("github.organization", "Ed-Fi-Alliance-OSS")
        param("msbuild.buildConfiguration", "release")
        param("msbuild.exe", """%MSBuildTools16.0_x64_Path%\MSBuild.exe""")
        param("node.version", "12.4.0")
        param("odsapi.package.suffix", ".Suite%version.suite%")
        param("odsapi.package.suitenumber", "EdFi%odsapi.package.suffix%")
        param("version.core", "%version.major%.%version.minor%.%version.patch%")
        param("version.informational", "%version%")
        param("version.major", "1")
        param("version.minor", "0")
        param("version.patch", "0")
        param("version.prerelease.prefix", "dev")
        param("version.prerelease.suffix", "%build.counter%")
        param("version.prerelease", "%version.prerelease.prefix%%version.prerelease.suffix%")
        param("version.suite", "3")
        param("version", "%version.core%-%version.prerelease%")
    }

    cleanup {
        baseRule {
            artifacts(builds = 2, artifactPatterns = "+:**/*")
        }
    }

    subProject(OdsApi.Project)
})
