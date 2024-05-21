cd ..

mkdir lib
cd lib

echo Installing JavaFX...
powershell -c "Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/22.0.1/openjfx-22.0.1_windows-x64_bin-sdk.zip' -OutFile 'temp.zip'"
echo ""

tar -xf temp.zip

echo Installing Connector/J...
powershell -c "Invoke-WebRequest -Uri 'https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.3.0.zip' -OutFile 'temp.zip'"
echo ""

tar -xf temp.zip

echo Apache Commons DBCP...
powershell -c "Invoke-WebRequest -Uri 'https://dlcdn.apache.org//commons/dbcp/binaries/commons-dbcp2-2.12.0-bin.zip' -OutFile 'temp.zip'"
echo ""

tar -xf temp.zip

echo Apache Commons Pooling...
powershell -c "Invoke-WebRequest -Uri 'https://dlcdn.apache.org//commons/pool/binaries/commons-pool2-2.12.0-bin.zip' -OutFile 'temp.zip'"
echo ""

tar -xf temp.zip

echo Apache Commons Logging...
powershell -c "Invoke-WebRequest -Uri 'https://dlcdn.apache.org//commons/logging/binaries/commons-logging-1.3.2-bin.zip' -OutFile 'temp.zip'"
echo ""

tar -xf temp.zip
del temp.zip

cd javafx-sdk-22.0.1\lib
set JAVAFXLIB=%cd%

cd ..
cd ..
cd ..

mkdir .vscode

powershell -c "(gc setup\vscode\launch.json) -replace 'JAVAFX_PATH', '%JAVAFXLIB%' | Out-File -encoding ASCII .vscode\launch.json"
powershell -c "(gc .vscode\launch.json) -replace '\\', '\\' | Out-File -encoding ASCII .vscode\launch.json"
copy setup\vscode\settings.json .vscode\settings.json

pause