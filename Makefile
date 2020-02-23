all: clean reload compile-all
	
clean:
	rm --recursive --force --verbose build .bloop .idea

reload: 
	seed-jvm all

compile-all:
	seed-jvm build protocol server client

run-server:
	bloop run server

run-client:
	bloop run client
