verymmog-prototype
==================

# Build the applications
To build this project and get it working, you need to use maven.

Then type:
```
mvn package
```

This will create a '*-jar-with-dependencies' for each of the subprojects (botlauncher, application, manager)

# Quick info about the applications

## Manager

Manager for the application. It manages the users. Basically, the login information has to be put in a text file named "users.txt" in the same directory as the jar file.
It can be extended by implementing some interfaces in the codebase.

__This application has to be executed in a terminal.__

## Application

The application. It is the graphical client for all the players.

## Botlaucher

Utility to check the stability of the server by creating a large number of bots moving in random directions.

# Authors

Dalle Marion @:<mariondallesoulard, Gmail>

Piotaix Rémi @:<remi.piotaix, Gmail>
