param(
  [Parameter(Mandatory = $false)]
  [string]$VaultPath = $env:OBSIDIAN_VAULT_PATH,
  [string]$ProjectRoot = (Split-Path -Parent $PSScriptRoot)
)

if ([string]::IsNullOrWhiteSpace($VaultPath)) {
  throw 'Set OBSIDIAN_VAULT_PATH or pass -VaultPath with the full Obsidian vault path.'
}

$resolvedVault = [IO.Path]::GetFullPath($VaultPath)
$resolvedProject = [IO.Path]::GetFullPath($ProjectRoot)
$targetRoot = Join-Path $resolvedVault 'Aiyige Project'
New-Item -ItemType Directory -Force -Path $targetRoot | Out-Null

foreach ($relativeRoot in @('docs', '.agent')) {
  $source = Join-Path $resolvedProject $relativeRoot
  if (-not (Test-Path -LiteralPath $source)) { continue }
  $target = Join-Path $targetRoot $relativeRoot
  New-Item -ItemType Directory -Force -Path $target | Out-Null
  Get-ChildItem -LiteralPath $source -Recurse -File -Include '*.md','*.json' | ForEach-Object {
    $relative = $_.FullName.Substring($source.Length).TrimStart('\','/')
    $destination = Join-Path $target $relative
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $destination) | Out-Null
    Copy-Item -LiteralPath $_.FullName -Destination $destination -Force
  }
}

Write-Output "Obsidian documentation synced to $targetRoot"
