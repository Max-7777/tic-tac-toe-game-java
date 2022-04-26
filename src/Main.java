public class Main {

    public static void main(String[] args) {
        /*

        To play and type in pos for grid, type [column][row] ex. 'a7' 'bbb21'

         */


        //current team in teams array
        int teamId = 0;
        //teams array
        char[] teams = {'X','O'};
        //char of current team
        char currTeam;
        //empty chars in ticTacToe grid
        char empty = '_';
        //grid of ticTacToe board
        char[][] grid = new char[3][3];
        //length of same team chars needed to win
        int winLength = 3;

        Game game = new Game();

        //fill ticTacToe grid with empty chars
        grid = game.filledGrid(grid,empty);

        //game loop
        while (true) {
            //team id loops through teams array with %
            teamId %= teams.length;
            currTeam = teams[teamId];

            //ask for guess and update grid based on valid guess
            grid = game.askGuessUpdatedGrid(grid,currTeam,empty);

            //returns char of team that has won game, if no winner, char is empty char
            char c = game.isGameDone(grid, teams, winLength, empty);

            //if char isn't empty and is a team char, that team has won
            if (c != empty) {
                game.printGrid(grid);
                game.won(c);
                //break out of game loop
                break;
            }

            //switch to next team
            teamId++;
        }

        System.out.println("End of game");
    }
}
