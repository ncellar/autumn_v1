#t?=com.norswap.autumn.test.Main
t?=com.norswap.autumn.test.benchmark.AutumnBench
LIBS=libs/java_utils/src
JVM_ARGS=-XX:CompileCommandFile=$(OUTDIR)/META-INF/hotspot_compiler
#JVM_ARGS+= -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
include library.mk
