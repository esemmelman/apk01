param([string]$Sdk = 'C:/Android')
$ErrorActionPreference = 'Stop'
$tools = Join-Path $Sdk 'build-tools/36.0.0'
$platform = Join-Path $Sdk 'platforms/android-36/android.jar'
function Check { if ($LASTEXITCODE -ne 0) { throw "Build failed with exit code $LASTEXITCODE" } }
New-Item -ItemType Directory -Force build/classes,build/dex,dist | Out-Null
& javac -encoding UTF-8 -source 8 -target 8 -classpath $platform -d build/classes src/com/esemmelman/foundingquiz/MainActivity.java
Check
$classes = @(Get-ChildItem build/classes -Recurse -Filter '*.class' | ForEach-Object FullName)
& "$tools/d8.bat" --lib $platform --min-api 30 --output build/dex $classes
Check
& "$tools/aapt2.exe" link -I $platform --manifest AndroidManifest.xml -A assets -o build/unsigned.apk
Check
& 'C:/Program Files/Java/jdk-17.0.18/bin/jar.exe' uf build/unsigned.apk -C build/dex classes.dex
Check
& "$tools/zipalign.exe" -f -p 4 build/unsigned.apk build/aligned.apk
Check
$keyDir = Join-Path $env:USERPROFILE '.android/apk01'
New-Item -ItemType Directory -Force $keyDir | Out-Null
$key = Join-Path $keyDir 'quiz.jks'
if (!(Test-Path $key)) {
    & 'C:/Program Files/Java/jdk-17.0.18/bin/keytool.exe' -genkeypair -keystore $key -storepass android -keypass android -alias quiz -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=Founding Documents Quiz'
    Check
}
& "$tools/apksigner.bat" sign --ks $key --ks-pass pass:android --key-pass pass:android --out dist/Founding-Documents-Quiz.apk build/aligned.apk
Check
& "$tools/apksigner.bat" verify --verbose dist/Founding-Documents-Quiz.apk
Check
Write-Host 'Created dist/Founding-Documents-Quiz.apk'


