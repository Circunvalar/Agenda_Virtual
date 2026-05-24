# Load .env and override variables to use Ollama local instance for AI
# Usage: dot-source this script to keep variables in the current session:
# . .\load-env-ollama.ps1

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
        if ($value.StartsWith('"') -and $value.EndsWith('"')) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        Set-Item -Path Env:\$name -Value $value
    }
}

# Override to use Ollama local API (change port if needed)
Set-Item -Path Env:\AI_PROVIDER_URL -Value "http://localhost:11434"
Set-Item -Path Env:\AI_PROVIDER_MODEL -Value "mistral"

Write-Host "Environment set to use Ollama local at $($Env:AI_PROVIDER_URL)"
