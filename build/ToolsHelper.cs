// SPDX-License-Identifier: Apache-2.0
// Licensed to the Ed-Fi Alliance under one or more agreements.
// The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
// See the LICENSE and NOTICES files in the project root for more information.

using System.Net;
using Nuke.Common;
using Nuke.Common.IO;
using static Nuke.Common.IO.FileSystemTasks;
using static Nuke.Common.ControlFlow;
using static Nuke.Common.Tools.DotNet.DotNetTasks;

namespace DefaultNamespace
{
    public static class ToolsHelper
    {
        public static void InstallTool(string toolName, string version, AbsolutePath directory)
        {
            var source = "https://www.myget.org/F/ed-fi/";

            SuppressErrors(() => DotNet($"tool uninstall {toolName} --tool-path {directory}"), false);
            DotNet($"tool install {toolName} --tool-path {directory} --version {version} --add-source {source}");
        }

        public static void DownloadFileToToolsDirectory(string uri, string filename, AbsolutePath directory)
        {
            if (!uri.EndsWith("/"))
            {
                uri = uri + "/";
            }

            var webClient = new WebClient();
            Logger.Info($"Downloading {uri}{filename} to {directory / filename}");
            webClient.DownloadFile($"{uri}{filename}", directory / filename);
            FileExists(directory / filename);
        }
    }
}
