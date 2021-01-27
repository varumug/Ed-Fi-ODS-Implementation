package OdsApi_MetaEdIntegration_PostgreSQL

import OdsApi_MetaEdIntegration_PostgreSQL.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("OdsApi_MetaEdIntegration_PostgreSQL")
    name = "PostgreSQL"

    buildType(OdsApi_MetaEdIntegration_PostgreSQL_OdsApiMetaEdDeployExtensionArtifactsInitDevTest)
    buildType(OdsApi_MetaEdIntegration_PostgreSQL_OdsApiMetaEdDeployStudentTranscriptArtifactsInitDevTest)
    buildType(OdsApi_MetaEdIntegration_PostgreSQL_OdsApiMetaEdDeployStudentTransportationArtifactsInitDevTest)
    buildType(OdsApi_MetaEdIntegration_PostgreSQL_OdsApiMetaEdDeployDataStandardArtifactsInitDevTest)

    params {
        param("script.initdev.parameters", "-NoCredentials -Engine PostgreSQL")
    }
})
