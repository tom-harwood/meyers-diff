mkdir -p classes
javac -sourcepath src -d classes src/*.java

if [ $? -eq 0 ]
then
    if [ "$*" == "" ]
    then
        java -ea -cp classes Main testdata/foo.txt testdata/bar.txt
    else
        java -ea -cp classes Main $*
    fi
fi
