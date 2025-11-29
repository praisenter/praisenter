<#
.SYNOPSIS
    Detects module dependencies for the project.
.DESCRIPTION
    Inspects Praisenter classes and all maven dependencies for module dependencies 
    to ensure we have all the dependencies necessary added to the pom.xml for jlink.
.PARAMETER Classes
    The path to the built Praisenter class files.  Use the target/classes folder
.PARAMETER Dependencies
    The path to the Maven dependencies.  Use the target/dependency folder
.PARAMETER JdepsPath
    The path to the jdeps tool.  Should be packaged in a JDK like Azul
.PARAMETER LogFile
    The path to an output file where the raw output from jdeps will be placed
.EXAMPLE
    .\GetModuleDependencies.ps1 -Classes "target/classes" -Dependencies "target/dependency" -JdepsPath "path/to/jdk/bin/jdeps.exe" -LogFile "jdeps-output.txt"
#>

param(
    [Parameter(Mandatory = $true)]
    [string]$Classes,

    [Parameter(Mandatory = $true)]
    [string]$Dependencies,

    [Parameter(Mandatory = $true)]
    [string]$JdepsPath,

    [string]$LogFile
)

# Verify Dependencies exists
if (-not (Test-Path $Dependencies)) {
    Write-Error "Dependencies '$Dependencies' does not exist."
    exit 1
}

# Verify jdeps exists
if (-not (Test-Path $JdepsPath)) {
    Write-Error "jdeps path '$JdepsPath' does not exist."
    exit 1
}

# Get JAR files
$searchArgs = @{
    Path   = $Dependencies
    Filter = "*.jar"
}
if ($Recurse) { $searchArgs.Recurse = $true }

$jarFiles = Get-ChildItem @searchArgs

if ($jarFiles.Count -eq 0) {
    Write-Host "No .jar files found in: $Dependencies"
    exit 0
}

$moduleDeps = @()

# Function to run jdeps on a single jar
$processJar = {
    param($jar, $JdepsPath, $LogFile)

    Write-Host "Running jdeps on: $($jar.FullName)"
    $output = & $JdepsPath -R --multi-release 25 --module-path $Dependencies $jar.FullName 2>&1

    $deps = @()
    foreach ($line in $output) {
        if ($line -match "(.+)->(.+)\s+(.+)") {
            $deps += $Matches.3
        }
    }

    # Log file output
    if ($LogFile) {
        Add-Content -Path $LogFile -Value "===== $($jar.FullName) ====="
        Add-Content -Path $LogFile -Value $output
        Add-Content -Path $LogFile -Value "`n"
    }

    return $deps
}

# Process jars
foreach ($jar in $jarFiles) {
    $moduleDeps += $processJar.Invoke($jar, $JdepsPath, $LogFile)
}

$output = & $JdepsPath -R --multi-release 25 -cp $Dependencies $Classes 2>&1

foreach ($line in $output) {
    if ($line -match "(.+)->(.+)\s+(.+)") {
        $moduleDeps += $Matches.3
    }
}

$moduleDeps = $moduleDeps | Sort-Object -Unique

Write-Host ""
Write-Host "Required Modules:"
foreach ($dep in $moduleDeps) {
    Write-Host $dep
}