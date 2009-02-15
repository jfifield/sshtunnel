#!/bin/sh
CLASSPATH="jsch-0.1.37.jar;j2ssh-core.jar;j2ssh-common.jar;commons-logging.jar;jdic.jar;windows\jdic_stub.jar;sshtunnel.jar"
java -cp $CLASSPATH -Djava.library.path=linux org.programmerplanet.sshtunnel.Main
