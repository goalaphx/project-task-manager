Write-Output 'Starting smoke tests against http://localhost:8080'
$s = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$register = @{email='smoketest@example.com'; password='Password123'; username='smoketest'} | ConvertTo-Json
Write-Output 'Registering user (ignore errors if already exists)'
$t1 = [datetime]::UtcNow
try {
    Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -Body $register -ContentType 'application/json' -WebSession $s -ErrorAction Stop
} catch {
    Write-Output "Register error: $_"
}
$t2=[datetime]::UtcNow
Write-Output ("Register elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)

Write-Output 'Logging in'
$login = @{email='smoketest@example.com'; password='Password123'} | ConvertTo-Json
$t1=[datetime]::UtcNow
Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $login -ContentType 'application/json' -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Login elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)

Write-Output 'Check /api/auth/me'
$t1=[datetime]::UtcNow
$me = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/me' -Method Get -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Me: {0}" -f $me)
Write-Output ("Me elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)

Write-Output 'List projects'
$t1=[datetime]::UtcNow
$projects = Invoke-RestMethod -Uri 'http://localhost:8080/api/projects' -Method Get -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Projects elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)
Write-Output (ConvertTo-Json $projects -Depth 4)

Write-Output 'Create project'
$projPayload = @{title='Smoke Project'; description='created by smoke test'} | ConvertTo-Json
$t1=[datetime]::UtcNow
$created = Invoke-RestMethod -Uri 'http://localhost:8080/api/projects' -Method Post -Body $projPayload -ContentType 'application/json' -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Create project elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)
Write-Output (ConvertTo-Json $created -Depth 4)
$projectId = $created.id
Write-Output ("ProjectId: {0}" -f $projectId)

Write-Output 'List tasks for project'
$t1=[datetime]::UtcNow
$tasks = Invoke-RestMethod -Uri "http://localhost:8080/api/projects/$projectId/tasks" -Method Get -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Tasks elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)
Write-Output (ConvertTo-Json $tasks -Depth 4)

Write-Output 'Create task'
$taskPayload = @{title='Smoke Task'; description='task from smoke test'} | ConvertTo-Json
$t1=[datetime]::UtcNow
$createdTask = Invoke-RestMethod -Uri "http://localhost:8080/api/projects/$projectId/tasks" -Method Post -Body $taskPayload -ContentType 'application/json' -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Create task elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)
Write-Output (ConvertTo-Json $createdTask -Depth 4)
$taskId = $createdTask.id
Write-Output ("TaskId: {0}" -f $taskId)

Write-Output 'Update task status to COMPLETED'
$t1=[datetime]::UtcNow
$updated = Invoke-RestMethod -Uri "http://localhost:8080/api/tasks/$taskId/status?status=COMPLETED" -Method Patch -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Update status elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)
Write-Output (ConvertTo-Json $updated -Depth 4)

Write-Output 'Delete task'
$t1=[datetime]::UtcNow
Invoke-RestMethod -Uri "http://localhost:8080/api/tasks/$taskId" -Method Delete -WebSession $s -ErrorAction Stop
$t2=[datetime]::UtcNow
Write-Output ("Delete elapsed (ms): {0}" -f [int]($t2-$t1).TotalMilliseconds)

Write-Output 'Smoke test complete'
