# CS4262-Project
Distributed Systems Project - Distributed File search engine

### Building

```sh
$ mvn clean install -Dmaven.test.skip
```

### Usage

```sh
-r --rmi        use RMI instead of plain text UDP for communication
-b --bootstrap  ip and port of bootstrap server in ip:port format
-a --addr       ip and port of this node in ip:port format
-c --console    use console instead of GUI
-u --username   the username for the node
```  
