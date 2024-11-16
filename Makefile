ROOT_DIR:=$(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))

dev:
	./gradlew bootRun --args='--spring.profiles.active=dev'

test:
	./gradlew test

lint:
	docker run --rm -v /var/run/docker.sock:/var/run/docker.sock:rw -v $(ROOT_DIR):/tmp/lint:rw oxsecurity/megalinter:v8
