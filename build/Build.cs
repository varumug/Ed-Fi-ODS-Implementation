// SPDX-License-Identifier: Apache-2.0
// Licensed to the Ed-Fi Alliance under one or more agreements.
// The Ed-Fi Alliance licenses this file to you under the Apache License, Version 2.0.
// See the LICENSE and NOTICES files in the project root for more information.

using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using DefaultNamespace;
using Nuke.Common;
using Nuke.Common.Execution;
using Nuke.Common.Git;
using Nuke.Common.IO;
using Nuke.Common.ProjectModel;
using Nuke.Common.Tooling;
using Nuke.Common.Tools.DotNet;
using Nuke.Common.Tools.GitVersion;
using Nuke.Common.Tools.MSBuild;
using Nuke.Common.Tools.NuGet;
using Nuke.Common.Tools.NUnit;
using static Nuke.Common.Tools.MSBuild.MSBuildTasks;
using static Nuke.Common.Tools.NuGet.NuGetTasks;
using static Nuke.Common.IO.FileSystemTasks;
using static Nuke.Common.Tools.DotNet.DotNetTasks;
using static Nuke.Common.Tools.NUnit.NUnitTasks;
using static Nuke.Common.IO.PathConstruction;
using static Nuke.Common.ControlFlow;

[CheckBuildProjectConfigurations]
[UnsetVisualStudioEnvironmentVariables]
class Build : NukeBuild
{
    [Parameter("Configuration to build - Default is 'Debug' (local) or 'Release' (server)")]
    readonly Configuration Configuration = IsLocalBuild
        ? Configuration.Debug
        : Configuration.Release;

    [GitRepository] readonly GitRepository GitRepository;
    [GitVersion] readonly GitVersion GitVersion;

    [Parameter("Max CPU count  - Default is Environment.ProcessorCount")] readonly int MaxCpuCount = Environment.ProcessorCount;

    [Parameter("Database Engine - Default 'SQLServer' or 'PostgreSQL'")] readonly Engine Engine = Engine.SQLServer;

    [Parameter("Exclude Code - Default false")] readonly bool ExcludeCodeGen = false;

    [Solution] readonly Solution Solution;

    AbsolutePath ArtifactsDirectory => RootDirectory / "artifacts";

    AbsolutePath TestResultsDirectory => RootDirectory / "test_results";

    AbsolutePath OdsDirectory => RootDirectory / "../" / "Ed-Fi-Ods" / "Application";

    AbsolutePath OdsTestDirectory => RootDirectory / "../" / "Ed-Fi-Ods" / "tests";

    AbsolutePath ImplementationDirectory => RootDirectory / "Application";

    AbsolutePath ToolsDirectory => RootDirectory / "tools";

    Target Clean
        => _ => _
            .Before(Restore)
            .Triggers(InstallTools, Restore, RestoreNuGetPackages, InstallNuGetExe)
            .Executes(() =>
            {
                Logger.Info($"ODS Source Directory ==> {OdsDirectory}");
                Logger.Info($"Implementation Source Directory ==> {ImplementationDirectory}");

                EnsureCleanDirectory(TestResultsDirectory);
                EnsureCleanDirectory(ArtifactsDirectory);
            });

    // in the future we can remove this once we are fully net core
    Target RestoreNuGetPackages
        => _ => _
            .Executes(() =>
            {
                NuGetRestore(s => s.SetTargetPath(Solution)
                    .SetToolPath(ToolsDirectory / "nuget.exe"));
            });

    Target Restore
        => _ => _
            .Executes(() =>
            {
                DotNetRestore(s => s.SetProjectFile(Solution));
            });

    Target Compile
        => _ => _
            .DependsOn(RunCodeGen)
            .Executes(() =>
            {
                MSBuild(o => o
                    .SetTargetPath(Solution)
                    .SetTargets("Clean", "Build")
                    .SetConfiguration(Configuration)
                    .SetAssemblyVersion(GitVersion.AssemblySemVer)
                    .SetFileVersion(GitVersion.AssemblySemFileVer)
                    .SetInformationalVersion(GitVersion.InformationalVersion)
                    .SetMaxCpuCount(MaxCpuCount)
                    .EnableNodeReuse());

                // In the future we want to use dotnet build
                // DotNetBuild(s => s
                //     .SetProjectFile(Solution)
                //     .SetConfiguration(Configuration)
                //     .SetAssemblyVersion(GitVersion.AssemblySemVer)
                //     .SetFileVersion(GitVersion.AssemblySemFileVer)
                //     .SetInformationalVersion(GitVersion.InformationalVersion)
                //     .EnableNoRestore());
            });

    Target NUnitIntegrationTest
        => _ => _
            .Executes(() =>
            {
                var assembliesToTest = GetTestDirectories().Value
                    .Where(x => !x.EndsWith("UnitTests", StringComparison.InvariantCultureIgnoreCase)
                                && !x.Contains("NetCore", StringComparison.InvariantCultureIgnoreCase)
                                && !x.Contains("EdFi.Ods.Tests", StringComparison.InvariantCultureIgnoreCase)
                                && !x.Contains("EdFi.Ods.WebService.Tests"))
                    .SelectMany(t => GlobFiles(t, $"**/bin/{Configuration}/**/*Tests.dll"))
                    .Where(a => !a.EndsWith("ApprovalTests.dll", StringComparison.InvariantCultureIgnoreCase));

                NUnit3(s => s
                    .SetToolPath(GetNUnitExeFullPath().Value)
                    .SetInputFiles(assembliesToTest.NotEmpty())
                    .SetOutputFile(TestResultsDirectory / "integration_tests.xml")
                    .SetDisposeRunners(true)
                    .SetAgents(1)
                    .SetSkipNonTestAssemblies(true)
                    .SetStopOnError(true));

                File.Move(RootDirectory/"TestResult.xml", TestResultsDirectory / "nunit-integrationtests.xml", true);
            });

    Target NUnitWebServiceIntegrationTest
        => _ => _
            .Executes(() =>
            {
                var assembliesToTest = GetTestDirectories().Value.Where(x => x.Contains("EdFi.Ods.WebService.Tests"))
                    .SelectMany(t => GlobFiles(t, $"**/bin/{Configuration}/**/*Tests.dll"))
                    .Where(a => !a.EndsWith("ApprovalTests.dll", StringComparison.InvariantCultureIgnoreCase));

                NUnit3(s => s
                    .SetToolPath(GetNUnitExeFullPath().Value)
                    .SetInputFiles(assembliesToTest.NotEmpty())
                    .SetOutputFile(TestResultsDirectory / "webservice_integration_tests.xml")
                    .SetDisposeRunners(true)
                    .SetAgents(1)
                    .SetSkipNonTestAssemblies(true)
                    .SetStopOnError(true));

                File.Move(RootDirectory/"TestResult.xml", TestResultsDirectory / "nunit-webservicetests.xml", true);
            });

    Target NUnitUnitTests
        => _ => _
            .Executes(() =>
            {
                var assembliesToTest = GetTestDirectories().Value.Where(x => x.Contains("EdFi.Ods.Tests"))
                    .SelectMany(t => GlobFiles(t, $"**/bin/{Configuration}/**/*Tests.dll"))
                    .Where(a => !a.EndsWith("ApprovalTests.dll", StringComparison.InvariantCultureIgnoreCase));

                NUnit3(s => s
                    .SetToolPath(GetNUnitExeFullPath().Value)
                    .SetInputFiles(assembliesToTest.NotEmpty())
                    .SetDisposeRunners(true)
                    .SetAgents(MaxCpuCount)
                    .SetSkipNonTestAssemblies(true)
                    .SetStopOnError(true)
                );

                File.Move(RootDirectory/"TestResult.xml", TestResultsDirectory / "nunit-unittests.xml", true);
            });

    Target Test
        => _ => _
            .Triggers(NUnitIntegrationTest, NUnitUnitTests, NUnitWebServiceIntegrationTest)
            .DependsOn(Compile)
            .Executes(() =>
            {
                var projectsToTest = GetTestDirectories().Value
                    .Where(x => x.EndsWith("UnitTests", StringComparison.InvariantCultureIgnoreCase)
                                || x.Contains("NetCore", StringComparison.InvariantCultureIgnoreCase))
                    .SelectMany(t => GlobFiles(t, "*.csproj"));

                DotNetTest(s =>
                    s.SetConfiguration(Configuration)
                    .SetNoRestore(true)
                    .SetNoBuild(true)
                    .SetResultsDirectory(TestResultsDirectory)
                    .SetDataCollector()
                    .CombineWith(projectsToTest, (settings, path) =>
                        settings.SetProjectFile(path)), degreeOfParallelism: 4, completeOnFailure: false);
            });

    Target InstallNuGetExe
        => _ => _
            .Executes(() =>
            {
                DownloadFileToToolsDirectory("https://dist.nuget.org/win-x86-commandline/v5.3.1/", "nuget.exe");
            });
    Target InstallCodeGen
        => _ => _
            .Executes(() =>
            {
                InstallTool("edfi.ods.codegen.suite3", "5.0.0-b10307");
            });

    Target InstallDbDeploy
        => _ => _
            .Executes(() =>
            {
                InstallTool("edfi.db.deploy.suite3","2.0.0-b10015");
            });

    Target InstallConfigTransformerCore
        => _ => _
            .Executes(() =>
            {
                InstallTool("ConfigTransformerCore", "2.0.0");
            });

    Target InstallTools
        => _ => _
            .Triggers(InstallCodeGen, InstallDbDeploy, InstallConfigTransformerCore);
    Target RunCodeGen
        => _ => _
            .Requires(() => !ExcludeCodeGen)
            .DependsOn(InstallCodeGen)
            .Executes(() =>
            {
                var codegen = ToolResolver.GetLocalTool(ToolsDirectory / "edfi.ods.codegen.exe");
                string arguments = $" -r {RootDirectory.Parent} -e {Engine}";
                Logger.Trace(arguments);
                codegen(arguments);
            });

    /// Support plugins are available for:
    ///   - JetBrains ReSharper        https://nuke.build/resharper
    ///   - JetBrains Rider            https://nuke.build/rider
    ///   - Microsoft VisualStudio     https://nuke.build/visualstudio
    ///   - Microsoft VSCode           https://nuke.build/vscode
    public static int Main() => Execute<Build>(x => x.Compile);

    Lazy<List<string>> GetTestDirectories()
        => new Lazy<List<string>>(()
            => GlobDirectories(OdsDirectory, "**Tests").Concat(GlobDirectories(OdsTestDirectory, "**Tests")).ToList());

    Lazy<string> GetNUnitExeFullPath()
        => new Lazy<string>(() => ToolPathResolver.GetPackageExecutable("nunit.consolerunner", "nunit3-console.exe", "3.11.1"));

    void InstallTool(string toolName, string version)
    {
        var source = "https://www.myget.org/F/ed-fi/";

        SuppressErrors(() => DotNet($"tool uninstall {toolName} --tool-path {ToolsDirectory}"), false);
        DotNet($"tool install {toolName} --tool-path {ToolsDirectory} --version {version} --add-source {source}");
    }

    void DownloadFileToToolsDirectory(string uri, string filename)
    {
        if (!uri.EndsWith("/"))
            uri = uri + "/";

        var webClient = new WebClient();
        webClient.DownloadFile($"{uri}{filename}", ToolsDirectory / filename);
    }
}
