package server.model;

import server.ClientHandler;
import shared.GameConstants;
import shared.NetworkMessage;

public class GameSession {
    private ClientHandler player1;
    private ClientHandler player2;
    private GameBoard gameBoard;
    private int currentTurn;
    private boolean gameActive;

    public GameSession(ClientHandler player1, ClientHandler player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameBoard = new GameBoard();
        this.currentTurn = GameConstants.PLAYER1;
        this.gameActive = true;

        player1.setCurrentGame(this);
        player2.setCurrentGame(this);

        System.out.println("New game session created for " + player1.getUsername() + " and " + player2.getUsername());
    }

    public void startGame() {
        gameActive = true;
        System.out.println("Game started between " + player1.getUsername() + " and " + player2.getUsername());

        // Send initial board state to both players
        sendBoardState();
    }

    public void processMove(ClientHandler player, int column) {
        System.out.println("\n==== MOVE PROCESSING ====");
        System.out.println("Player: " + player.getUsername() + ", Column: " + column);
        System.out.println("Game active: " + gameActive);
        System.out.println("Current turn: " + currentTurn);
        System.out.println("Player1: " + player1.getUsername() + ", Player2: " + player2.getUsername());
        System.out.println("Is player1's turn? " + (currentTurn == GameConstants.PLAYER1));
        System.out.println("Is this player1? " + (player == player1));

        if (!gameActive) {
            System.out.println("Game not active, ignoring move");
            return;
        }

        // Check if it's this player's turn
        boolean isPlayer1Turn = (currentTurn == GameConstants.PLAYER1 && player == player1);
        boolean isPlayer2Turn = (currentTurn == GameConstants.PLAYER2 && player == player2);

        System.out.println("Is valid player's turn? " + (isPlayer1Turn || isPlayer2Turn));

        if (isPlayer1Turn || isPlayer2Turn) {
            // Show current board
            System.out.println("Board BEFORE move:");
            printBoard();

            boolean moveResult = gameBoard.makeMove(column, currentTurn);
            System.out.println("Move result: " + moveResult);

            if (moveResult) {
                // Print board after move
                System.out.println("Board AFTER move:");
                printBoard();

                // Switch turns
                int oldTurn = currentTurn;
                currentTurn = (currentTurn == GameConstants.PLAYER1) ?
                        GameConstants.PLAYER2 : GameConstants.PLAYER1;
                System.out.println("Turn switched from " + oldTurn + " to " + currentTurn);

                // Check game status
                checkGameStatus();

                // Send updated board state to both players
                System.out.println("Sending updated board state to both players");
                sendBoardState();
            } else {
                System.out.println("Invalid move in column " + column);
                player.sendMessage(new NetworkMessage(
                        NetworkMessage.GAME_STATE,
                        "SERVER",
                        "Invalid move. Try again."
                ));
            }
        } else {
            System.out.println("Not player's turn. Current player: " +
                    (currentTurn == GameConstants.PLAYER1 ? player1.getUsername() : player2.getUsername()));
            player.sendMessage(new NetworkMessage(
                    NetworkMessage.GAME_STATE,
                    "SERVER",
                    "Not your turn."
            ));
        }
        System.out.println("==== END MOVE PROCESSING ====\n");
    }

    private void printBoard() {
        int[][] board = gameBoard.getBoardState();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
    }

    private void sendBoardState() {
        // Get current board state
        int[][] boardState = gameBoard.getBoardState();

        NetworkMessage player1Message = new NetworkMessage(
                NetworkMessage.GAME_STATE,
                "SERVER",
                new Object[] {
                        boardState,
                        currentTurn,
                        (currentTurn == GameConstants.PLAYER1) ? "Your turn." : "Opponent's turn."
                }
        );

        NetworkMessage player2Message = new NetworkMessage(
                NetworkMessage.GAME_STATE,
                "SERVER",
                new Object[] {
                        boardState,
                        currentTurn,
                        (currentTurn == GameConstants.PLAYER2) ? "Your turn." : "Opponent's turn."
                }
        );

        System.out.println("Sending board state:");
        for (int row = 0; row < boardState.length; row++) {
            for (int col = 0; col < boardState[0].length; col++) {
                System.out.print(boardState[row][col] + " ");
            }
            System.out.println();
        }

        // Send to both players
        player1.sendMessage(player1Message);
        player2.sendMessage(player2Message);
    }

    public void checkGameStatus() {
        if (gameBoard.checkWin()) {
            gameActive = false;
            int winner = gameBoard.getWinner();
            System.out.println("Game over! Winner: " + (winner == GameConstants.PLAYER1 ? player1.getUsername() : player2.getUsername()));

            NetworkMessage resultMessage = null;
            if (winner == GameConstants.PLAYER1) {
                resultMessage = new NetworkMessage(
                        NetworkMessage.GAME_RESULT,
                        "SERVER",
                        player1.getUsername() + " wins!"
                );
            } else {
                resultMessage = new NetworkMessage(
                        NetworkMessage.GAME_RESULT,
                        "SERVER",
                        player2.getUsername() + " wins!"
                );
            }

            System.out.println("Sending game result message: " + resultMessage.getContent());
            notifyPlayers(resultMessage);
        } else if (gameBoard.isFull()) {
            gameActive = false;
            System.out.println("Game over! Board full - draw game");

            NetworkMessage resultMessage = new NetworkMessage(
                    NetworkMessage.GAME_RESULT,
                    "SERVER",
                    "Game ended in a draw."
            );

            System.out.println("Sending game result message: " + resultMessage.getContent());
            notifyPlayers(resultMessage);
        }
    }

    public void handlePlayerResign(ClientHandler player) {
        System.out.println("\n==== PLAYER RESIGNATION ====");
        System.out.println("Player: " + player.getUsername() + " is resigning");
        System.out.println("Game active: " + gameActive);

        if (!gameActive) {
            System.out.println("Game not active, ignoring resignation");
            return;
        }

        // Set game to inactive immediately
        gameActive = false;

        // Determine which player resigned
        String resignedPlayer = player.getUsername();
        String winner = (player == player1) ? player2.getUsername() : player1.getUsername();

        // Send resignation result to both players
        NetworkMessage resultMessage = new NetworkMessage(
                NetworkMessage.GAME_RESULT,
                "SERVER",
                winner + " wins! " + resignedPlayer + " resigned."
        );

        System.out.println("Player resigned: " + resignedPlayer);
        System.out.println("Winner: " + winner);
        System.out.println("Sending resignation game result: " + resultMessage.getContent());

        // Send the message to both players
        player1.sendMessage(resultMessage);
        player2.sendMessage(resultMessage);

        System.out.println("==== END PLAYER RESIGNATION ====\n");
    }

    public void notifyPlayers(Object message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }

    public void handlePlayerDisconnect(ClientHandler player) {
        if (!gameActive) {
            return;
        }

        gameActive = false;

        if (player == player1) {
            player2.sendMessage(new NetworkMessage(
                    NetworkMessage.GAME_RESULT,
                    "SERVER",
                    "Opponent disconnected. You win!"
            ));
        } else {
            player1.sendMessage(new NetworkMessage(
                    NetworkMessage.GAME_RESULT,
                    "SERVER",
                    "Opponent disconnected. You win!"
            ));
        }
    }
}