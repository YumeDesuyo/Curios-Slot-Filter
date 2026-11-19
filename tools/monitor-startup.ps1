[CmdletBinding()]
param(
    [string]$VersionName = "Closing Song1.6.7.1",
    [string]$MinecraftRoot = "D:\pcl\.minecraft",
    [double]$GapThresholdSeconds = 2.0,
    [int]$InitialLines = 20,
    [int]$PollMilliseconds = 250,
    [int]$MaxSeconds = 0
)

$logPath = Join-Path $MinecraftRoot "versions\$VersionName\logs\latest.log"
$timestampPattern = '^\[[^\]]*\s(\d{1,2}):(\d{2}):(\d{2})\.(\d{3})\]'
$previousTime = $null
$knownLineCount = -1
$previousLength = -1
$lastActivity = [DateTime]::UtcNow
$lastStallNoticeSeconds = 0.0
$startedAt = [DateTime]::UtcNow

Write-Host "Watching: $logPath" -ForegroundColor Cyan
Write-Host "Gaps over $GapThresholdSeconds seconds are highlighted. Press Ctrl+C to stop." -ForegroundColor DarkGray
Write-Host ""

while ($true) {
    if (-not (Test-Path -LiteralPath $logPath)) {
        Start-Sleep -Milliseconds $PollMilliseconds
        continue
    }

    try {
        $lines = @(Get-Content -LiteralPath $logPath -Encoding UTF8 -ErrorAction Stop)
        $currentLength = (Get-Item -LiteralPath $logPath -ErrorAction Stop).Length
    } catch {
        Start-Sleep -Milliseconds $PollMilliseconds
        continue
    }

    $rotated = $previousLength -ge 0 -and (
        $currentLength -lt $previousLength -or
        $lines.Count -lt $knownLineCount
    )

    if ($rotated) {
        $knownLineCount = 0
        $previousTime = $null
        $lastActivity = [DateTime]::UtcNow
        $lastStallNoticeSeconds = 0.0
        Write-Host "===== LOG ROTATED / NEW GAME LAUNCH =====" -ForegroundColor Magenta
    }

    if ($knownLineCount -lt 0) {
        $knownLineCount = [Math]::Max(0, $lines.Count - $InitialLines)
    }

    for ($index = $knownLineCount; $index -lt $lines.Count; $index++) {
        $line = $lines[$index]
        $match = [regex]::Match($line, $timestampPattern)

        if ($match.Success) {
            $currentTime = [timespan]::new(
                0,
                [int]$match.Groups[1].Value,
                [int]$match.Groups[2].Value,
                [int]$match.Groups[3].Value,
                [int]$match.Groups[4].Value
            )

            if ($null -ne $previousTime -and $currentTime -ge $previousTime) {
                $gapSeconds = ($currentTime - $previousTime).TotalSeconds

                if ($gapSeconds -ge $GapThresholdSeconds) {
                    Write-Host (
                        "===== LOG GAP: {0:N3}s ({1} -> {2}) =====" -f
                        $gapSeconds,
                        $previousTime.ToString(),
                        $currentTime.ToString()
                    ) -ForegroundColor Red
                }
            }
            $previousTime = $currentTime
        }

        if ($line -match '\b(ERROR|FATAL)\b') {
            Write-Host $line -ForegroundColor Red
        } elseif ($line -match '\bWARN\b') {
            Write-Host $line -ForegroundColor Yellow
        } else {
            Write-Host $line
        }
    }

    if ($lines.Count -ne $knownLineCount) {
        $lastActivity = [DateTime]::UtcNow
        $lastStallNoticeSeconds = 0.0
    } else {
        $stallSeconds = ([DateTime]::UtcNow - $lastActivity).TotalSeconds
        if ($stallSeconds -ge $GapThresholdSeconds -and
                ($lastStallNoticeSeconds -eq 0 -or
                    $stallSeconds - $lastStallNoticeSeconds -ge 5)) {
            Write-Host (
                "===== NO NEW LOG FOR {0:N1}s =====" -f $stallSeconds
            ) -ForegroundColor DarkRed
            $lastStallNoticeSeconds = $stallSeconds
        }
    }

    $knownLineCount = $lines.Count
    $previousLength = $currentLength

    if ($MaxSeconds -gt 0 -and
            ([DateTime]::UtcNow - $startedAt).TotalSeconds -ge $MaxSeconds) {
        break
    }

    Start-Sleep -Milliseconds $PollMilliseconds
}
