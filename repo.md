# Tic-Tac-Toe Project

This project is a Java-based Tic-Tac-Toe game with multiple AI implementations.

## Project Structure

- **`src/main/java/dk/easv/tictactoe/`**: Main source code.
    - **`bll/`**: Business Logic Layer containing the game engine and AI.
        - **`SteroidsAI.java`**: A 3x3 AI that prioritizes winning and blocking.
        - **`MinimaxAI.java`**: Advanced AI using the Minimax algorithm.
        - **`RandomAI.java`**: Simple AI that picks random moves.
        - **`PrioListOriginal.java.txt`**: Original reference code for the bot.
    - **`gui/`**: JavaFX graphical user interface.
- **`src/main/resources/`**: Project resources including FXML files and sounds.
- **`pom.xml`**: Maven configuration file.

## Features
- **Player vs Player** mode.
- **Player vs AI** with three difficulty levels: Easy, Hard, and Steroids.
- Retro sound effects and visual highlights for winning lines.

## How to Build
Run the following command in the root directory:
```bash
./mvnw compile
```
