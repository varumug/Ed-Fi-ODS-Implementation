package OdsApi_Packages.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object OdsApi_Packages_EdFiAllianceEdFiStandard : GitVcsRoot({
    name = "Ed-Fi-Alliance/Ed-Fi-Standard"
    url = "git@github.com:Ed-Fi-Alliance/Ed-Fi-Standard.git"
    branch = "development"
    branchSpec = "%git.branch.specification%"
    userNameStyle = GitVcsRoot.UserNameStyle.NAME
    checkoutSubmodules = GitVcsRoot.CheckoutSubmodules.IGNORE
    serverSideAutoCRLF = true
    useMirrors = false
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
