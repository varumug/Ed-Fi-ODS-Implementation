package OdsApi_Packages.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object OdsApi_Packages_EdFiMigrationUtility : GitVcsRoot({
    name = "Ed-Fi-MigrationUtility"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-MigrationUtility.git"
    pushUrl = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-MigrationUtility.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
