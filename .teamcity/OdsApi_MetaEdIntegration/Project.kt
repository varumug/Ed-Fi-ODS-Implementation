package OdsApi_MetaEdIntegration

import OdsApi_MetaEdIntegration.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("OdsApi_MetaEdIntegration")
    name = "MetaEd Integration"
    description = "Utillizes MetaEd to Deploy the Latest Data Standard to the ODS/API for Testing"

    buildType(OdsApi_MetaEdIntegration_OdsApiMetaEdDeployExtensionArtifactsInitDevTest)
    buildType(OdsApi_MetaEdIntegration_2_OdsApiMetaEdDeployStudentTranscriptArtifactsInitDevTest)
    buildType(OdsApi_MetaEdIntegration_2_OdsApiMetaEdDeployStudentTransportationArtifactsInitDevTest)
    buildType(OdsApi_MetaEdIntegration_2_OdsApiMetaEdDeployDataStandardArtifactsInitDevTest)

    params {
        param("metaed.technologyVersion", "%version.core%")
        param("metaed.source", "MetaEd-js")
        param("datastandard.source", """%metaed.source%\node_modules\ed-fi-model-%datastandard.version%""")
        param("datastandard.version", "3.2c")
    }

    subProject(OdsApi_MetaEdIntegration_PostgreSQL.Project)
})
