SHELL=bash

DOCLAGS=-sourcepath src -source 7 -encoding utf-8 -version -author -charset utf-8 -linksource -sourcetab 8 -keywords -docencoding utf-8 -d doc/javadoc -private


all:
	./build.sh


javadoc:
	javadoc7 $(DOCLAGS) se.kth.maandree.jcbnfp $(find "./src" | grep '\.java$')


clean:
	if [ -d "./doc"         ]; then  rm -r "./bin"         ; fi
	if [ -d "./doc/javadoc" ]; then  rm -r ".doc/javadoc"  ; fi


.PHONY: clean

