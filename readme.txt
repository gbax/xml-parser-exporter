Используется in-memory БД поэтому никаких скриптов выполнять не надо.
Изначально для заполнения БД выполняется скрипт xml-parser-exporter\classes\db\init.sql
Настройки приложения хранятся в application.properties
Для запуска экспорта нужно выполнить:
	run.bat export xml.xml
Для запуска импорта нужно выполнить:
	run.bat sync xml.xml