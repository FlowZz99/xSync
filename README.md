# xSync

This is a plugin designed for multi-server survival networks.
It uses database, cache and a pub/sub system to sync data across multiple server.

### What Data can be synced?

* Inventory
* Enderchest
* Health
* Food
* Effects
* Experience and Levels
* Everything that you can save on a database

### What are "Zones"?

Zones are section of the map that every server is responsible to handle.
Whenever a player walk from a zone to another it will be sended to the corresponding server.
This allows for virtually unlimited player counts by dividing the map into multiple servers.

### Are Zones configurable?

Yes, you can configure the zone corners and specify which world will they handle.
You can also configure the worldborder feature to display within the zone limits.
(Worldborder feature needs zone to be perfect square)

### Is it safe to sync data?

It works by saving everthing into a database, when a player logout or switches server
the data is saved on the disk and after the save is complete the server will notify
all others server that the player can now be loaded.
That makes the data saving very secure and almost impossible to lose data.

### How do I hook with other plugins?

xSync implements a complete API that allows you to fully use its potential.

* There are events such as PlayerPostSaveEvent and PlayerPostLoadEvent that can be used
  to implement your custom plugin saving logic.

* There are API methods that allows you to define your custom Message(Packet) to
  communicate to all the connected servers.

### Where can i find some examples on how to use the API?

You can start by taking a look at [xChat](https://github.com/FlowZz99/xChat "xChat") repository.

