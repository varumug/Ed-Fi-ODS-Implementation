// SPDX-License-Identifier: Apache-2.0
// Licensed to the Ed-Fi Alliance under one or more agreements.
// The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
// See the LICENSE and NOTICES files in the project root for more information.

using Nuke.Common.Tooling;

namespace DefaultNamespace
{
    public class Engine : Enumeration
    {
            public static Engine SQLServer = new Engine{Value = nameof(SQLServer)};
            public static Engine PostgreSQL = new Engine{Value = nameof(PostgreSQL)};
    }
}
