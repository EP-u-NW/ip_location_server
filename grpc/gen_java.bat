@echo off
color 0A

where /q protoc
IF %errorlevel% NEQ 0 (
	echo protoc not found! Make sure its on your path!
	goto :END
)
for /f %%i in ('where protoc') do set I=%%i
set PROTOHOME=%I:~0,-15%

SET OUT_DIR=..\src\main\java
mkdir %OUT_DIR%
protoc --plugin="protoc-gen-grpc-java" --grpc_out=%OUT_DIR% --java_out=%OUT_DIR% --proto_path . .\ip_location_server.proto
protoc --plugin="protoc-gen-grpc-java" --grpc_out=%OUT_DIR% --java_out=%OUT_DIR% --proto_path %PROTOHOME%\include\google\protobuf %PROTOHOME%\include\google\protobuf\empty.proto
:END
echo Done
pause