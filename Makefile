all: clean reload compile
	
clean:
	rm --recursive --force --verbose build .bloop .idea

reload: 
	seed all

compile-all:
	seed build protocol server client

run-server:
	bloop run server

run-client:
	bloop run client
