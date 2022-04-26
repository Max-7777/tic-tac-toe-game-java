import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Game {

    List<Character> letters = Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');

    public Tuple stringToCords(String s, char[][] grid) {
        try {
            //if user input is less than 2 chars, it can't be valid
            if (s.length() < 2) throw new Exception();
            //if the first char isn't a letter, throw exception
            if (!Character.isLetter(s.charAt(0))) throw new Exception();
            //row of tuple to be returned
            int row = 0;
            //string of row of tuple to be returned (need stringBuilder to add possible multiple letters for row ex: '12')
            StringBuilder rowString = new StringBuilder();
            //column of tuple to be returned
            int column = 0;
            //char to be looped through user input
            char c = s.charAt(0);
            //add to column based on first letter to its index in letters
            column += letters.indexOf(c);
            //loop through user input
            for (int i = 1; i < s.length(); i++) {
                c = s.charAt(i);
                //if multiple same letters add 26 to column for each
                if (Character.isAlphabetic(c) && c == s.charAt(0)) column += 26;
                //if multiple different letters throw exception
                else if (Character.isAlphabetic(c) && c != s.charAt(0)) throw new Exception();
                //append to row stringBuilder if char is a digit
                if (Character.isDigit(c)) rowString.append(c);
            }
            //row is = rowString - 1 (-1 accounting for discrepancy of ticTacToe board rows starting at 1 and indexes starting at 0)
            row = Integer.parseInt(String.valueOf(rowString)) - 1;
            //new tuple based on row and column
            Tuple t = new Tuple(row,column);
            //if tuple's rows and columns are not contained in ticTacToe grid (row or column vals. too big) throw exception
            if (!doesContain(grid,t)) throw new Exception();
            return t;
        //throw error version of tuple 99999,99999
        } catch(Exception ignored) {return new Tuple(99999,99999);}
    }

    public String columnToString(int i) {
        StringBuilder o = new StringBuilder();
        //ex. for 50 == yy, only needs to know i contains which letter (y), % 26 deletes extra letters leaving remainder
        //c = type of letter column index should be
        char c = letters.get(i % 26);

        //adds one letter
        o.append(c);
        //adds more of same letter if i > 26 which requires more than 1 letter
        o.append(String.valueOf(c).repeat(i / 26));

        return String.valueOf(o);
    }

    public boolean doesContain(char[][] grid, Tuple t) {
        boolean col = false;
        boolean row = false;
        //if tuple row and col values fit into array
        if (t.getColumn() <= grid.length - 1 && t.getColumn() >= 0) col = true;
        if (t.getRow() <= grid[0].length - 1 && t.getRow() >= 0) row = true;

        return row && col;
    }

    public void printGrid(char[][] grid) {
        int leftSpacing = 1;
        int columnSpacing = 1;
        //left spacing is the length of the longest row value ex. '22'
        leftSpacing += Integer.toString(grid.length - 1).length();
        //spacing between each column is the length of the longest column value label ex. 'aaa'
        columnSpacing += columnToString(grid[0].length - 1).length();

        //spacing to left of printed array needs to fit largest row label value, likewise for spacing in between columns
        //first line is leftSpacing needed for rows + loop through column values and adequate spacing
        //after first line, just print column + column spacing and loop through each column in row and print adequate spacing

        for (int i = 0; i <= leftSpacing; i++) {
            System.out.print(" ");
        }

        for (int c = 0; c < grid[0].length; c++) {
            System.out.print(columnToString(c));
            int l = columnToString(c).length();
            while (l <= columnSpacing) {
                System.out.print(" ");
                l++;
            }
        }

        System.out.println();

        for (int r = 0; r < grid.length; r++) {
            System.out.print(r+1);

            int l = Integer.toString(r+1).length();
            while (l <= leftSpacing) {
                System.out.print(" ");
                l++;
            }
            for (int c = 0; c < grid[0].length; c++) {
                System.out.print(grid[r][c]);
                for (int i = 0; i < columnSpacing; i++) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    public char[][] updatedGrid(char[][] grid, Tuple t, char c) {
        //return updated grid based on char and its position
        char[][] o = grid;
        o[t.getRow()][t.getColumn()] = c;
        return o;
    }

    public char[][] filledGrid(char[][] a, char empty) {
        //fill array o with empty char in size of a, return o
        char[][] o = new char[a.length][a[0].length];
        for (char[] i : o) {
            Arrays.fill(i, empty);
        }
        return o;
    }

    public boolean spaceEmpty (char[][] grid, Tuple t, char empty) {
        //returns whether indicated tuple pos in grid contains empty char
        return grid[t.getRow()][t.getColumn()] == empty;
    }

    public char[][] askGuessUpdatedGrid(char[][] grid, char currTeam, char empty) {
            Tuple cords;
            String userInput;
        //loop until valid guess
        while (true) {
            Scanner scanner = new Scanner(System.in);

            printGrid(grid);
            System.out.println(currTeam + "'s turn:");

            userInput = scanner.nextLine();

            //make cords based on userInput
            cords = stringToCords(userInput,grid);
            //if stringToCords() returned error tuple, don't break out of while loop
            if (cords.getRow() == 99999) System.out.println("Invalid space");
            //if the space indicated by cords isn't empty, don't break out of while loop
            else if (!spaceEmpty(grid, cords, empty)) System.out.println("Team '" + grid[cords.getRow()][cords.getColumn()] + "' already occupies this grid space");
            //if no errors break out of loop and return valid, updated grid
            else break;
        }
        return updatedGrid(grid,cords,currTeam);
    }

    public char isGameDone(char[][] grid, char[] teams, int winLength, char empty) {
        //runs checkTeam() for right, down and diagonal directions for each space in grid
        //if the sum of consecutive same team spaces in grid is greater than or equal the amount needed to win, that team win
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                for (char team : teams) {
                    if (checkTeam("right", grid, team, winLength, new Tuple(r, c)) >= winLength) return team;
                    if (checkTeam("down", grid, team, winLength, new Tuple(r, c)) >= winLength) return team;
                    if (checkTeam("diagonal", grid, team, winLength, new Tuple(r, c)) >= winLength) return team;
                }
            }
        }

        return empty;
    }

    public int checkTeam(String s, char[][] grid, char team, int winLength, Tuple t) {
        int o = 0;
        //if check right, try to go along row and tally how many consecutive same team spots there are
        //catch if error throws about being out of bounds of array, regardless return o
        //down, check down column
        //diagonal, check diagonal down right and down left
        if (s.equals("right")) {
            for (int i = 0; i < winLength; i++) {
                try {
                    if (grid[t.getRow()+i][t.getColumn()] == team) o++;
                    else break;
                }   catch(Exception ignored) {}
            }
        }
        else if (s.equals("down")) {
            for (int i = 0; i < winLength; i++) {
                try {
                    if (grid[t.getRow()][t.getColumn()+i] == team) o++;
                    else break;
                }   catch(Exception ignored) {}
            }
        }
        else if (s.equals("diagonal")) {
            for (int i = 0; i < winLength; i++) {
                try {
                    if (grid[t.getRow()+i][t.getColumn()+i] == team) o++;
                    else {
                        if (o >= winLength) return o;
                        o = 0;
                        break;
                    }
                }   catch(Exception ignored) {}
            }
            for (int i = 0; i < winLength; i++) {
                try {
                    if (grid[t.getRow()+i][t.getColumn()-i] == team) o++;
                    else break;
                }   catch(Exception ignored) {}
            }

        }

        return o;
    }

    public void won(char team) {
        System.out.println(team + " won!");
    }
}
