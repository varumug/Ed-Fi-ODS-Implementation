package OdsApi_Packages.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object OdsApi_Packages_EdFiStandard : GitVcsRoot({
    name = "Ed-Fi-Standard"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-Standard.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    useTagsAsBranches = true
    userNameStyle = GitVcsRoot.UserNameStyle.NAME
    checkoutSubmodules = GitVcsRoot.CheckoutSubmodules.IGNORE
    serverSideAutoCRLF = true
    useMirrors = false
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
