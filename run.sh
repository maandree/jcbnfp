#!/bin/sh

## program execution information
package="se.kth.maandree.jcbnfp"
hasMain=1
hasHome=0


## java executer if default is for Java 7
[[ $(echo `java -version 2>&1 | cut -d . -f 2` | cut -d ' ' -f 1) = '7' ]] &&
    function javaSeven()
    {   java "$@"
    }

## java executer if default is not for Java 7
[[ $(echo `java -version 2>&1 | cut -d . -f 2` | cut -d ' ' -f 1) = '7' ]] ||
    function javaSeven()
    {   java7 "$@"
    }


## libraries
jars=''
if [ -d lib ]; then
    jars=`echo $(find lib | grep .jar$) | sed -e 's/lib\//:/g' -e 's/ //g'`
fi


## run
javaSeven -ea -cp bin$jars "$package".Program "$@"
