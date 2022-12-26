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
	docker-compose -f docker/docker-compose.yml up -d

stop-dev:
	docker-compose -f docker/docker-compose.yml down

status-dev:
	docker-compose -f docker/docker-compose.yml ps

push-image:
	docker tag homebudget-api:0.0.1 jfrossetto/homebudget-api:0.0.1
	docker push jfrossetto/homebudget-api:0.0.1

build-image:
	gradle clean build
	docker rmi -f jfrossetto/homebudget-api:0.0.1
	docker build --force-rm -t homebudget-api:0.0.1 .

kill-nones:
	docker images | grep none | awk '{print $3}' | xargs docker rmi
