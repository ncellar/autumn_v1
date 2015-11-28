#t?=com.norswap.autumn.test.Main
t?=com.norswap.autumn.test.benchmark.AutumnBench
LIBS=libs/java_utils/src
JVM_ARGS=-XX:CompileCommandFile=$(OUTDIR)/META-INF/hotspot_compiler
include library.mk
