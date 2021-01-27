package OdsApi.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.finishBuildTrigger

object OdsApi_OdsApiDeployToStaging : BuildType({
    templates(_Self.buildTypes.OdsApiDeployToStagingTemplate)
    name = "ODS/API: Deploy to Staging"

    buildNumberPattern = "${OdsApi_OdsApiInitDevUnitTestPackage.depParamRefs.buildNumber}"

    params {
        param("odsapi.package.sandboxAdmin", "EdFi.Ods.SandboxAdmin.Web")
        param("odsapi.package.swaggerUI", "EdFi.Ods.SwaggerUI")
        param("git.branch.default", "main")
        param("version", "${OdsApi_OdsApiInitDevUnitTestPackage.depParamRefs["version"]}")
        param("odsapi.package.databases", "EdFi%odsapi.package.suffix%.RestApi.Databases")
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_14"
            buildType = "${OdsApi_OdsApiInitDevUnitTestPackage.id}"
            successfulOnly = true
            branchFilter = """
                +:<default>
                +:master-v3
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(OdsApi_OdsApiInitDevUnitTestPackage) {
        }
    }
})
