package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object EdFiDatabases : GitVcsRoot({
    name = "Ed-Fi Databases"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-Databases.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    serverSideAutoCRLF = true
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
