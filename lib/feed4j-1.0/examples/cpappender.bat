IF NOT "%CP%" == "" GOTO setted

SET CP=%1
GOTO end

:setted
SET CP=%CP%;%1

:end
