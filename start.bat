@SET JAVA_HOME=C:\Program Files\Java
@FOR /F %%G IN ('DIR /B "%JAVA_HOME%\JDK*"') DO @SET JDK_HOME=%JAVA_HOME%\%%G
@SET PATH=%JDK_HOME%\bin;%PATH%

@javac -version
@echo.
@echo   %JDK_HOME%\bin successfully added to Windows PATH
@echo.
@echo   Now type 'javac'.
@echo.
@echo.
@echo.

@CMD