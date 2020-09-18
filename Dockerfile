# SPDX-License-Identifier: Apache-2.0
# Licensed to the Ed-Fi Alliance under one or more agreements.
# The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
# See the LICENSE and NOTICES files in the project root for more information.

FROM mcr.microsoft.com/dotnet/core/sdk:3.1-alpine
LABEL maintainer="jmckay@certicasolutions.com"

ARG ODS_REPO=https://github.com/Ed-Fi-Alliance-OSS/Ed-Fi-ODS/archive/main.zip
ARG IMP_REPO=https://github.com/Ed-Fi-Alliance-OSS/Ed-Fi-ODS-implementation/archive/main.zip

# update the image packages to latests
RUN apk update && apk upgrade && apk add unzip --no-cache

# RUN mkdir /src
# RUN mkdir /src/Ed-Fi-ODS
# RUN mkdir /src/Ed-Fi-ODS-Implementation

WORKDIR /src

RUN wget ${ODS_REPO} -O ed-fi-ods.zip \
&& unzip ed-fi-ods.zip -d . \
&& mv Ed-Fi-ODS-main Ed-Fi-ODS \
&& rm ed-fi-ods.zip

RUN wget ${IMP_REPO} -O ed-fi-ods-implementation.zip \
&& unzip ed-fi-ods-implementation.zip -d . \
&& mv Ed-Fi-ODS-Implementation-main Ed-Fi-ODS-Implementation \
&& rm ed-fi-ods-implementation.zip

# COPY ./Ed-Fi-ODS ./src/Ed-Fi-ODS
# COPY  ./Ed-Fi-ODS-Implementation ./src/Ed-Fi-ODS-Implementation

# ADD  ./Ed-Fi-ODS-Implementation ./src/Ed-Fi-ODS-Implementation
# ADD ./Ed-Fi-ODS ./src/Ed-Fi-ODS

WORKDIR /src/Ed-Fi-ODS-Implementation

ENTRYPOINT [ "pwsh" ]