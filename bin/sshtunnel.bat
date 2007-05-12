set CLASSPATH="j2ssh-core.jar;j2ssh-common.jar;commons-logging.jar;looks-1.3.1.jar;jdic.jar;windows\jdic_stub.jar;sshtunnel.jar"
javaw -cp %CLASSPATH% -Djava.library.path=windows org.programmerplanet.sshtunnel.Main
