package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object EdFiOds : GitVcsRoot({
    name = "Ed-Fi-ODS"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-ODS.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    useTagsAsBranches = true
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    checkoutSubmodules = GitVcsRoot.CheckoutSubmodules.IGNORE
    serverSideAutoCRLF = true
    useMirrors = false
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
