#!/bin/sh
CLASSPATH="j2ssh-core.jar;j2ssh-common.jar;commons-logging.jar;looks-1.3.1.jar;jdic.jar;windows\jdic_stub.jar;sshtunnel.jar"
java -cp $CLASSPATH -Djava.library.path=linux org.programmerplanet.sshtunnel.Main
