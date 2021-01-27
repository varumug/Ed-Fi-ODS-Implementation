package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object TeamCityConfigsOdsImplementation : GitVcsRoot({
    name = "TeamCity-Configs-ODS-Implementation"
    url = "https://github.com/%github.organization.teamCity%/Ed-Fi-TeamCity-Configs"
    branch = "master-ods-implementation"
    authMethod = password {
        userName = "%github.username%"
        password = "%EdFiBuildAgent-GitHubKeyPasshrase%"
    }
})
