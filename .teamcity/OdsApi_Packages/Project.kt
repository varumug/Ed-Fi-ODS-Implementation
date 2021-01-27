package OdsApi_Packages

import OdsApi_Packages.buildTypes.*
import OdsApi_Packages.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("OdsApi_Packages")
    name = "Packages"

    vcsRoot(OdsApi_Packages_EdFiStandard)
    vcsRoot(OdsApi_Packages_EdFiAllianceEdFiStandard)
    vcsRoot(OdsApi_Packages_EdFiMigrationUtility)

    buildType(OdsApi_Packages_EdFiOdsApiTestSdk)
    buildType(OdsApi_Packages_EdFiOdsCodeGen)
    buildType(OdsApi_Packages_EdFiOdsExtensionsTpdm)
    buildType(OdsApi_Packages_EdFiOdsPopulatedTemplateTpdmPostgreSQL)
    buildType(OdsApi_PackagesNetCore31_EdFiDbDeploy)
    buildType(OdsApi_Packages_EdFiOdsMinimalTemplatePostgreSQL)
    buildType(OdsApi_Packages_EdFiDatabaseAdmin)
    buildType(OdsApi_Packages_EdFiOdsPopulatedTemplate)
    buildType(OdsApi_Packages_EdFiOdsExtensionsHomograph)
    buildType(OdsApi_Packages_EdFiOdsProfilesSample)
    buildType(OdsApi_Packages_EdFiOdsPopulatedTemplatePostgreSQL)
    buildType(OdsApi_Packages_PostgreSQLBinaries)
    buildType(OdsApi_PackagesNetCore31_EdFiOdsUtilitiesMigration)
    buildType(OdsApi_Packages_EdFiDatabaseSecurity)
    buildType(OdsApi_Packages_EdFiOdsExtensionsSample)
    buildType(OdsApi_Packages_EdFiOdsMinimalTemplate)
    buildType(OdsApi_Packages_EdFiOdsApiSdk)
    buildType(OdsApi_Packages_EdFiOdsPopulatedTemplateTpdm)
    buildType(OdsApi_Packages_EdFiStandardDescriptors)

    template(OdsApi_Packages_CreateDatabasePackageTemplate)
    template(OdsApi_Packages_EdFiExtensionsTemplate)
    template(OdsApi_PackagesNetCore31_NetCore31Packages)
    template(OdsApi_Packages_CreateDatabaseTemplate)
})
