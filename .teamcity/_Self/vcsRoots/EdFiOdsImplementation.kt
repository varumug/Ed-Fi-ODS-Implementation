package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object EdFiOdsImplementation : GitVcsRoot({
    name = "Ed-Fi-ODS-Implementation"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-ODS-Implementation.git"
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
