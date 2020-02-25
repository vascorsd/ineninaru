
all: clean reload compile-all

clean:
	rm --recursive --force --verbose build .bloop .idea

clean-all: clean
	rm --recursive --force --verbose .scalafix-rules/*
	touch .scalafix-rules/rules.classpath

reload:
	seed-jvm all

compile-all:
	seed-jvm build protocol server client

run-server:
	bloop run server

run-client:
	bloop run client

check-format:
	scalafmt-jvm --git true --list --test --stdout

install-lint-rules:
	coursier fetch --cache .scalafix-rules \
		com.github.vovapolu:scaluzzi_2.12:0.1.3 \
		-p > .scalafix-rules/rules.classpath

check-lint:
	scalafix-jvm --tool-classpath="$$(eval cat .scalafix-rules/rules.classpath)" --check
