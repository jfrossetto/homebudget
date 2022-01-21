# defaul shell
SHELL = /bin/bash

# Rule "help"
.PHONY: help
.SILENT: help
help:
	echo "Uso make [param]"
	echo "param:"
	echo ""
	echo "start-dev  	  - inicia docker-compose "
	echo "stop-dev  	  - para docker-compose "
	echo "status-dev  	  - status docker-compose "
	echo ""
	echo ""
	echo "help		      - show this message"

start-dev:
	docker-compose -f docker-compose.yml up -d

stop-dev:
	docker-compose -f docker-compose.yml down

status-dev:
	docker-compose -f docker-compose.yml ps

