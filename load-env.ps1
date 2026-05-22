# Load .env into current PowerShell session
# Usage: .\load-env.ps1  (dot-source to preserve variables in parent session)
# Example: . .\load-env.ps1

$envFile = Join-Path $PSScriptRoot '.env'
if (-not (Test-Path $envFile)) {
    Write-Error "No .env file found at $envFile"
    return
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq '' -or $line.StartsWith('#')) { return }
    if ($line -match '^(.*?)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        # Remove surrounding quotes if present
        if ($value.StartsWith('"') -and $value.EndsWith('"')) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        Write-Host "Setting environment variable: $name"
        Set-Item -Path Env:\$name -Value $value
    }
}

Write-Host "Loaded environment variables from $envFile"

