package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object EdFiTpdmExtension : GitVcsRoot({
    name = "Ed-Fi-TPDM-Extension"
    url = "git@github.com:Ed-Fi-Alliance-OSS/Ed-Fi-TPDM-Extension.git"
    branch = "%git.branch.default%"
    branchSpec = "%git.branch.specification%"
    serverSideAutoCRLF = true
    authMethod = uploadedKey {
        uploadedKey = "EdFiBuildAgent"
        passphrase = "%EdFiBuildAgent-GitHubKeyPasshrase%"

    }
})
