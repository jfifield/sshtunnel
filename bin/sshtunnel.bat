set CLASSPATH="jsch-0.1.37.jar;j2ssh-core.jar;j2ssh-common.jar;commons-logging.jar;sshtunnel.jar"
javaw -cp %CLASSPATH% -Djava.library.path=windows org.programmerplanet.sshtunnel.ui.swing.Main
