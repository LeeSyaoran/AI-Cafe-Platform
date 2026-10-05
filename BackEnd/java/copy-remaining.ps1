# AI Café Backend - Quick Copy Script
# Run this in PowerShell to copy remaining files

$Source = "D:\project code\AI Café Platform\BackEnd\java\order-service\src\main\java\com\example\backend"
$Target = "D:\project code\AI Café Platform\BackEnd\java\backend\src\main\java\com\example\backend"

# Copy remaining files
Write-Host "Checking for remaining files..."

# Controllers
$controllers = @(
    "controller\AuthController.java",
    "controller\ProductController.java",
    "controller\CafeController.java",
    "controller\NotificationController.java"
)

foreach ($ctrl in $controllers) {
    $src = Join-Path $Source $ctrl
    $dst = Join-Path $Target $ctrl
    if (Test-Path $src) {
        Copy-Item $src -Destination $dst -Force
        Write-Host "Copied: $ctrl" -ForegroundColor Green
    }
}

# Services
$services = @(
    "service\AuthService.java",
    "service\CafeService.java",
    "service\NotificationService.java"
)

foreach ($svc in $services) {
    $src = Join-Path $Source $svc
    $dst = Join-Path $Target $svc
    if (Test-Path $src) {
        Copy-Item $src -Destination $dst -Force
        Write-Host "Copied: $svc" -ForegroundColor Green
    }
}

# Repositories
$repos = @(
    "repository\UserRepository.java",
    "repository\AddressRepository.java",
    "repository\SeatRepository.java"
)

foreach ($repo in $repos) {
    $src = Join-Path $Source $repo
    $dst = Join-Path $Target $repo
    if (Test-Path $src) {
        Copy-Item $src -Destination $dst -Force
        Write-Host "Copied: $repo" -ForegroundColor Green
    }
}

Write-Host "`nDone! Run: cd 'D:\project code\AI Café Platform\BackEnd\java\backend'; mvn spring-boot:run'" -ForegroundColor Yellow