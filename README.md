# SSH Tunnel NG

![maven-publish](https://github.com/agung-m/sshtunnel-ng/actions/workflows/maven-publish.yml/badge.svg)

A user-friendly, fast, reliable, and cross-platform SSH tunnel manager.

What is SSH tunneling? \
https://en.wikipedia.org/wiki/Tunneling_protocol#Secure_Shell_tunneling \
https://www.ssh.com/academy/ssh/tunneling

<img src="img/sshtunnel-ng_screenshot.png" width=70% height=70%>

## Features

1. Simple UI
2. Manage multiple sessions and tunnels
3. Local and remote tunnels supported
4. Basic username/password and private key authentications
5. Configurable encryption ciphers
6. Enable/disable compression
7. Quick connect/disconnect from the system tray
8. Fast and lightweight (multithreaded with a small memory footprint of ~16 MB RAM)
9. Reliable (automatic reconnection, hanging session prevention)
10. Multi-platform (it runs on Linux, Windows, and Mac OS)
11. Portable and standalone installation (it can be run from an external disk or a USB flash drive)

## Download

### Latest version

* [Linux x86-64](https://github.com/agung-m/sshtunnel-ng/releases/download/0.6.0/sshtunnel-ng-0.6.0-dist-linux-64.zip)
* [Windows 64-bit](https://github.com/agung-m/sshtunnel-ng/releases/download/0.6.0/sshtunnel-ng-0.6.0-dist-windows-64.zip)
* [Mac OS x86-64](https://github.com/agung-m/sshtunnel-ng/releases/download/0.6.0/sshtunnel-ng-0.6.0-dist-mac-64.zip)

## Requirements

Java Runtime Environment (JRE) 8.0 or newer

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Contact
hello@agungmulya.com

--------------------------------------------------------------------------------

## Building

  For the current platform:

  `mvn clean assembly:single`

  For a different platform for example:

  `mvn -P +linux,-windows clean assembly:single`

  Supported platforms: windows, windows-64, linux, linux-64, mac-64


## Running

  Unzip the target distribution.

  `java -jar sshtunnel-ng-{VERSION}.jar`

## Changes
See [Releases](https://github.com/agung-m/sshtunnel-ng/releases).
