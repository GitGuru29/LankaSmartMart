try {
    \ = Get-Content build_errors.txt
    \ = \ | Where-Object { \ -match "e: " }
    foreach (\ in \) {
        if (\ -match "file:///") {
            \ = \ -replace ".*file:///", ""
        }
        Write-Output \
    }
} catch {
    Write-Error \
}
