package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object EdFiExtensions : GitVcsRoot({
    name = "Ed-Fi-Extensions"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-Extensions.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    useTagsAsBranches = true
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    checkoutSubmodules = GitVcsRoot.CheckoutSubmodules.IGNORE
    serverSideAutoCRLF = true
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
