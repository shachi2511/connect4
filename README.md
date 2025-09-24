# Connect Four 🔴🔵

A multiplayer Connect Four game with graphical user interface and client-server networking, built in Java.

## Features

- **Multiplayer Support**: Multiple clients can connect and play simultaneously
- **Graphical User Interface**: Clean Swing-based GUI for both client and server
- **Real-time Communication**: Network messaging between clients and server
- **Game Management**: Lobby system, chat functionality, and game session handling
- **Cross-platform**: Runs on any system with Java

    
## How to Run

### Starting the Server
1. Navigate to `ConnectFourServer/src/`
2. Compile and run `server/Main.java`
3. The server GUI will open - configure settings and start the server

### Starting the Client
1. Navigate to `CoonectFourclient/src/`
2. Compile and run `client/Main.java`
3. Enter server details in the login panel
4. Connect and start playing!

## Technologies Used

- **Java**: Core programming language
- **Swing**: GUI framework
- **Socket Programming**: Network communication
- **Multithreading**: Handling multiple client connections

## Game Components

- **GameBoard**: Core game logic and win condition checking
- **NetworkMessage**: Communication protocol between client and server
- **ClientHandler**: Manages individual client connections on server
- **GameSession**: Handles game state and player interactions

## Development

This project was developed as part of a computer science course, demonstrating:
- Object-oriented programming principles
- Network programming concepts
- GUI development
- Multi-threaded application design



