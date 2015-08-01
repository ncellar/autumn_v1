# Parameters:
# t is the target: the class to run
# o is the output directory (defaults to "build")
# a is an argument list

# Example:
# make run t=com.norswap.autumn.Main o=debug a="foo bar baz"

o?=build

OUTDIR=out/$o

ifeq ($(OS),Windows_NT)
	SEP=;
else
	SEP=:
endif

ifeq ($o,debug)
	DEBUG=-g
else ifeq ($o,opti)
	DEBUG=-g:none
endif

# NOTE(norswap): The quotes around "deps/*" are necessary to avoid shell
# wildcard expansion. The wildcard must be processed by javac.

build: outdir
	cp -R resources/* $(OUTDIR)
	javac -Xlint:unchecked $(DEBUG) -d $(OUTDIR) -cp "deps/*" `find src -name *.java`

run:
	java -cp "$(OUTDIR)$(SEP)deps/*$(SEP)$(OUTDIR)/resources" $t $a

trace:
	java -cp "$(OUTDIR)$(SEP)deps/*$(SEP)$(OUTDIR)/resources" -agentlib:hprof=cpu=samples $t $a

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
