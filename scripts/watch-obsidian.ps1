param(
  [Parameter(Mandatory = $false)]
  [string]$VaultPath = $env:OBSIDIAN_VAULT_PATH,
  [string]$ProjectRoot = (Split-Path -Parent $PSScriptRoot)
)

if ([string]::IsNullOrWhiteSpace($VaultPath)) {
  throw 'Set OBSIDIAN_VAULT_PATH or pass -VaultPath with the full Obsidian vault path.'
}

& (Join-Path $PSScriptRoot 'sync-obsidian.ps1') -VaultPath $VaultPath -ProjectRoot $ProjectRoot
$watchRoot = [IO.Path]::GetFullPath($ProjectRoot)
$watcher = New-Object IO.FileSystemWatcher $watchRoot
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true
$watcher.Filter = '*.md'

$action = {
  Start-Sleep -Milliseconds 250
  & (Join-Path $using:PSScriptRoot 'sync-obsidian.ps1') -VaultPath $using:VaultPath -ProjectRoot $using:ProjectRoot
}

Register-ObjectEvent $watcher Changed -Action $action | Out-Null
Register-ObjectEvent $watcher Created -Action $action | Out-Null
Register-ObjectEvent $watcher Renamed -Action $action | Out-Null
Register-ObjectEvent $watcher Deleted -Action $action | Out-Null

Write-Output "Watching project documentation for Obsidian sync. Press Ctrl+C to stop."
try { while ($true) { Wait-Event -Timeout 5 | Out-Null } }
finally { $watcher.Dispose() }
