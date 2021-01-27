package OdsApi_Utilities.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object OdsApi_Utilities_EdFiOds_2 : GitVcsRoot({
    name = "Ed-Fi-ODS"
    url = "https://github.com/%github.organization%/Ed-Fi-ODS.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    useTagsAsBranches = true
    serverSideAutoCRLF = true
    useMirrors = false
    authMethod = password {
        userName = "%github.username%"
        password = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
