package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object OdsApi_PackagesNetCore31_EdFiOdsUtilitiesMigration : BuildType({
    templates(OdsApi_PackagesNetCore31_NetCore31Packages)
    name = "EdFi.Ods.Utilities.Migration"

    params {
        param("pathToSolutionFile", """%teamcity.build.checkoutDir%\%MigrationUtilityRoot%\Migration.sln""")
        param("git.branch.default", "main")
        param("msbuild.exe", "")
        param("PackageId", "EdFi%odsapi.package.suffix%.Ods.Utilities.Migration")
        param("MigrationUtilityRoot", "Ed-Fi-MigrationUtility")
        param("pathToTestFile", """%MigrationUtilityRoot%\**\bin\%msbuild.buildConfiguration%\**\*Tests.dll""")
        param("version.major", "2")
        param("version.minor", "0")
        param("dotnet.pack.parameters", "-p:NoWarn=NU5123 -p:PackageId=%PackageId% -p:NoPackageAnalysis=true -p:NoDefaultExcludes=true")
    }

    vcs {
        root(_Self.vcsRoots.OdsApi_PackagesNetCore31_EdFiMigrationUtility, "=> %MigrationUtilityRoot%")
        root(_Self.vcsRoots.EdFiOds, "+:Application/EdFi.Ods.Standard/Artifacts => Ed-Fi-ODS/Application/EdFi.Ods.Standard/Artifacts")
    }
})
