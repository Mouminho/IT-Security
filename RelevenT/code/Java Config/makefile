JFLAGS = -g
JC = javac
CP = -cp ./dependencies/*:./test/*:./utils/*

SOURCE_FILES = $(shell find . -name "*.java")
CLASS_FILES = $(shell echo $(SOURCE_FILES) | sed "s/\.java/\.class/g")

Default: compile_classes create_bin

compile_classes:
	$(JC) $(CP) $(SOURCE_FILES)

create_bin:
	jar -cvmf MANIFEST_LA.MF logappend.jar $(CLASS_FILES) -C dependencies .
	cat stub.sh logappend.jar > logappend && chmod +x logappend
	jar -cvmf MANIFEST_LR.MF logread.jar $(CLASS_FILES) -C dependencies .
	cat stub.sh logread.jar > logread && chmod +x logread

clean:
	$(RM) l_read/*.class l_append/*.class utils/*.class model/*.class log*