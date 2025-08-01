package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Board {

    public void drawWhite() {
        PrintStream output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[][] rowArray = getInitialBoard();
        printWhiteBoard(rowArray, output);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "\n");
    }

    public void drawBlack() {
        PrintStream output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[][] rowArray = getInitialBoard();
        printBlackBoard(rowArray, output);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "\n");
    }

    private String[][] getInitialBoard() {
        String wp = EscapeSequences.SET_TEXT_COLOR_WHITE + "P";
        String wr = EscapeSequences.SET_TEXT_COLOR_WHITE + "R";
        String wn = EscapeSequences.SET_TEXT_COLOR_WHITE + "N";
        String wb = EscapeSequences.SET_TEXT_COLOR_WHITE + "B";
        String wq = EscapeSequences.SET_TEXT_COLOR_WHITE + "Q";
        String wk = EscapeSequences.SET_TEXT_COLOR_WHITE + "K";

        String bp = EscapeSequences.SET_TEXT_COLOR_BLACK + "P";
        String br = EscapeSequences.SET_TEXT_COLOR_BLACK + "R";
        String bn = EscapeSequences.SET_TEXT_COLOR_BLACK + "N";
        String bb = EscapeSequences.SET_TEXT_COLOR_BLACK + "B";
        String bq = EscapeSequences.SET_TEXT_COLOR_BLACK + "Q";
        String bk = EscapeSequences.SET_TEXT_COLOR_BLACK + "K";

        String[][] rowArray = {
                {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "},
                {"8", br, bn, bb, bq, bk, bb, bn, br, "8"},
                {"7", bp, bp, bp, bp, bp, bp, bp, bp, "7"},
                {"6", " ", " ", " ", " ", " ", " ", " ", " ", "6"},
                {"5", " ", " ", " ", " ", " ", " ", " ", " ", "5"},
                {"4", " ", " ", " ", " ", " ", " ", " ", " ", "4"},
                {"3", " ", " ", " ", " ", " ", " ", " ", " ", "3"},
                {"2", wp, wp, wp, wp, wp, wp, wp, wp, "2"},
                {"1", wr, wn, wb, wq, wk, wb, wn, wr, "1"},
                {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "}
        };
        return rowArray;
    }

    private void printWhiteBoard(String[][] rowArray, PrintStream output) {
        for (int rowNumber = 0; rowNumber < 10; rowNumber++) {
            String[] row = rowArray[rowNumber];
            printRow(output, row, rowNumber);
        }
    }

    private void printBlackBoard(String[][] rowArray, PrintStream output) {
        for (int rowNumber = 9; rowNumber >= 0; rowNumber--) {
            String[] row = rowArray[rowNumber];

            if (rowNumber == 0 || rowNumber == 9) {
                // Flip file labels (a-h)
                row = new String[] {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
            } else if (rowNumber == 1 || rowNumber == 8) {
                // Black back rank (row 1) or white back rank (row 8)
                row = row.clone();

                // Swap columns 4 and 5 (king <-> queen)
                String temp = row[4];
                row[4] = row[5];
                row[5] = temp;
            }

            printRow(output, row, 9 - rowNumber);
        }
    }

    private void printRow(PrintStream output, String[] row, int rowNumber) {
        output.print(EscapeSequences.SET_BG_COLOR_MAGENTA);
        output.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA);

        if (rowNumber == 0 || rowNumber == 9) {
            printEdge(output, row);
        } else if (rowNumber % 2 == 0) {
            printEvenRow(output, row);
        } else {
            printOddRow(output, row);
        }
        output.print(EscapeSequences.SET_BG_COLOR_BLACK + "\n");
    }

    private void printEdge(PrintStream output, String[] row) {
        for (String letter : row) {
            output.print(" ");
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            output.print(letter);
            output.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA);
            output.print(" ");
        }
    }

    private void printEvenRow(PrintStream output, String[] row) {
        int tileNumber = 0;
        for (String piece : row) {
            boolean isEdge = (tileNumber == 0 || tileNumber == 9);
            String bg = isEdge ? EscapeSequences.SET_BG_COLOR_MAGENTA
                    : (tileNumber % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_BLUE);
            String fg = isEdge ? EscapeSequences.SET_TEXT_COLOR_YELLOW
                    : (tileNumber % 2 == 0 ? EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY : EscapeSequences.SET_TEXT_COLOR_BLUE);

            output.print(bg + " " + fg + piece + " ");
            tileNumber++;
        }
    }

    private void printOddRow(PrintStream output, String[] row) {
        int tileNumber = 0;
        for (String piece : row) {
            boolean isEdge = (tileNumber == 0 || tileNumber == 9);
            String bg = isEdge ? EscapeSequences.SET_BG_COLOR_MAGENTA
                    : (tileNumber % 2 == 0 ? EscapeSequences.SET_BG_COLOR_BLUE : EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            String fg = isEdge ? EscapeSequences.SET_TEXT_COLOR_YELLOW
                    : (tileNumber % 2 == 0 ? EscapeSequences.SET_TEXT_COLOR_BLUE : EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY);

            output.print(bg + " " + fg + piece + " ");
            tileNumber++;

        }
    }
}

