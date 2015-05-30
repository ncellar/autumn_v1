# NOTE(norswap): The quotes around "deps/*" are necessary to avoid shell
# wildcard expansion. The wildcard must be processed by javac.

t?=com.norswap.autumn.test.Main
o?=build
a?=

OUTDIR=out/$o
DEBUG=

ifeq ($o,debug)
	DEBUG=-g
else ifeq ($o,opti)
	DEBUG=-g:none
endif

build: outdir
	javac -Xlint:unchecked $(DEBUG) -d $(OUTDIR) -cp "deps/*" `find src -name *.java`

run:
	java -cp "$(OUTDIR);deps/*" $t $a

trace:
	java -cp "$(OUTDIR);deps/*" -agentlib:hprof=cpu=samples $t $a

clean:
	rm -rf $(OUTDIR)

outdir:
	mkdir -p $(OUTDIR)

.PHONY: \
	build \
	clean \
	run \
	trace \
	outdir
