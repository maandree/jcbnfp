BOOK = jcbnfp
LANG = en

default: pdf

all: info pdf

info:
	makeinfo "$(BOOK).texinfo"
	gzip -9  "$(BOOK).info"

pdf:
	texi2pdf "$(BOOK).texinfo"
	for ext in `echo aux cp cps fn ky log pg toc tp vr vrs op ops`; do           \
	    (if [ -f "$(BOOK).$$ext" ]; then unlink "$(BOOK).$$ext"; fi);  \
	done
	if [ -d "$(BOOK).t2d" ]; then rm -r "$(BOOK).t2d"; fi

soft:
	pdfjam --pagecolor 249,249,249 -o "$(BOOK).pdf" "$(BOOK).pdf"

softer:
	pdfjam --pagecolor 249,246,240 -o "$(BOOK).pdf" "$(BOOK).pdf"

print:
	cp "$(BOOK).pdf" "$(BOOK)+print.pdf"

spell:
	aspell --lang="$(LANG)" check "$(BOOK).texinfo"

clean:
	for ext in `echo aux cp cps fn ky log pg toc tp vr vrs op ops bak`; do       \
	    (if [ -f "$(BOOK).$$ext" ]; then unlink "$(BOOK).$$ext"; fi);  \
	done
	if [ -d "$(BOOK).t2d" ]; then rm -r "$(BOOK).t2d"; fi
	if [ -f "$(BOOK).texinfo.bak" ]; then unlink "$(BOOK).texinfo.bak"; fi


.PHONY: default all

