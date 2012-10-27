SHELL=bash

DOCLAGS=-sourcepath src -source 7 -encoding utf-8 -version -author -charset utf-8 -linksource -sourcetab 8 -keywords -docencoding utf-8 -d doc/javadoc -private
PACKAGES=$$(find ./src/se/kth/maandree/jcbnfp -exec bash -c 'test -d "{}" && echo "{}"' \; | sed -e 's/\.\/src\///' | sed -e 's/\//\./g')
JAVAFILES=$$(find "./src" | grep -v | grep '\/\.java$' | grep '\.java$')


all: info java

doc: pdf javadoc


info:
	(cd "./doc" ; make info)

pdf:
	(cd "./doc" ; make pdf)

java:
	./build.sh

javadoc:
	javadoc7 $(DOCLAGS) $(PACKAGES) $(JAVAFILES)


clean:
	if [ -d "./bin"         ]; then  rm -r "./bin"          ; fi
	if [ -d "./doc/javadoc" ]; then  rm -r "./doc/javadoc"  ; fi
	(cd "./doc" ; make clean)


.PHONY: clean all doc

