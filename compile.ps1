$sources = Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java" | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' }
$sources | Out-File -FilePath "sources.txt" -Encoding ascii
& javac -encoding UTF-8 -cp "lib/*;src/main/java" -d bin '@sources.txt'
if ($LASTEXITCODE -eq 0) {
    Remove-Item sources.txt
    Write-Output "Compilation finished successfully."
} else {
    Write-Output "Compilation failed."
}
