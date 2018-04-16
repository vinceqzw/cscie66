# sets shell to Bash so we can use curly brace expansions
SHELL=/bin/bash


%.class:
	hadoop com.sun.tools.javac.Main $*.java

%.jar: %.class
	jar cf $*.jar *.class

p%: Problem%.class
	@echo ""

output%: Problem%.jar input
	rm -rf output$* output$*_final
	hadoop jar Problem$*.jar Problem$* input output$* output$*_final

clean:
	rm -rf *.class *.jar output*

wc:	WordCount.jar input_wc
	rm -rf output_wc
	hadoop jar WordCount.jar WordCount input_wc output_wc

# tells Make to never delete intermediate dependencies (like .class files)
.SECONDARY:

# tells Make that these targets are not actual files
.PHONY: clean
