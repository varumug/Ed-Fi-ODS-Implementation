package OdsApi_Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object OdsApi_Packages_EdFiDatabaseSecurity : BuildType({
    templates(OdsApi_Packages_CreateDatabasePackageTemplate)
    name = "EdFi.Database.Security"

    params {
        param("script.create.database.package.database.type", "Security")
    }
})
