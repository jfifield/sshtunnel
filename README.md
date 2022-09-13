# SSH Tunnel NG

A graphical SSH tunnel manager. It is extended from the original code here: https://github.com/jfifield/sshtunnel.

The extensions are included starting from v0.5.5.

![](img/sshtunnel.jpg)

## Features

* Manage multiple sessions and tunnels.
* Local and remote tunnels supported.
* ~~Minimize to tray~~. The feature is removed until the SWT bug in Linux (KDE) is fixed.
* Quick connect/disconnect from tray.
* Bind local ports to any local address.
* Basic (username/password) and private key authentications
* Configurable ciphers
* SSH compression
* Automatic connection monitoring (no hanging sessions)
* Multi-platform (it runs on JVM)


## Download

### 0.5.8

* [Linux (64-bit JVM)](https://github.com/agung-m/sshtunnel-ng/releases/download/0.5.8/sshtunnel-0.5.8-dist-linux-64.zip)
* [Windows (64-bit JVM)](https://github.com/agung-m/sshtunnel-ng/releases/download/0.5.8/sshtunnel-0.5.8-dist-windows-64.zip)

## Requirements

Java Runtime Environment (JRE) 6.0 or greater

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

--------------------------------------------------------------------------------

## Building

  For the current platform:

  `mvn clean assembly:assembly`

  For a different platform for example:

  `mvn -P +linux,-windows clean assembly:assembly`

  Support platforms: windows, windows-64, linux, linux-64

## Running

  Unzip the target distribution.

  `java -jar sshtunnel-VERSION.jar`


## Changes

### 0.5.7
* configure ciphers to use
* SSH compression

### 0.5.6
* support SSH private key

### 0.5.5
* a background thread that monitor and reconnect disconnected session automatically
 
### 0.5.3
* added support for linux & windows 64-bit
* updated swt libraries

### 0.5.2
* allow non-default port to be specified on a session
* notification when connection fails (host can't be found, auth fails, etc)
* allow session/tunnel panels to be resized (and persisted)
* sessions are now sorted by name
* tunnels are now sorted by local address & local port
