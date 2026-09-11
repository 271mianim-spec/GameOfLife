/**
 * Model for Conway's Game of Life.
 *
 * This class stores the society in a 2D boolean array.
 * true  = live cell
 * false = empty location
 *
 * IMPORTANT FOR THIS PROJECT:
 * The board does NOT wrap around. Any location outside the array is simply
 * ignored when counting neighbors.
 */
public class GameOfLife {

    private boolean[][] society;

    private int rows;
    private int cols;

    /**
     * Creates an empty society with the requested number of rows and columns.
     */
    public GameOfLife(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be positive.");
        }

        this.rows = rows;
        this.cols = cols;

        society = new boolean[rows][cols];
    }

    /** Returns the number of rows in the society. */
    public int numberOfRows() {
        return rows;
    }

    /** Returns the number of columns in the society. */
    public int numberOfColumns() {
        return cols;
    }

    /** Makes the location at row, col alive. */
    public void growCellAt(int row, int col) {
        society[row][col] = true;
    }

    /** Makes the location at row, col dead. */
    public void killCellAt(int row, int col) {
        society[row][col] = false;
    }

    /** Returns true if the location contains a live cell. */
    public boolean cellAt(int row, int col) {
        if  (row >= 0 && row < rows && col >= 0 && col < cols) {
            return society[row][col];
        }

        return false;
    }

    /** Makes every location in the society dead. */
    public void clear() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                society[i][j] = false;
            }
        }
    }

    /**
     * Counts the live neighbors surrounding one location.
     *
     * A location can have at most eight neighbors. Locations outside the
     * board DO NOT wrap around to the other side.
     *
     * TODO: Complete this method.
     */
    public int neighborCount(int row, int col) {
        // TODO: Traverse the 3 x 3 neighborhood around row, col.
        //       Skip row, col itself.
        //       Check bounds before reading society[r][c].

        int neighbors = 0;
        for (int r = row-1; r < row+1; r++) {
            for (int c = col-1; c < col+1; c++) {
                if ((r != row && c != row) && cellAt(r, c)) {
                    neighbors++;
                }
            }
        }


        return neighbors;
    }

    /**
     * Advances the entire society by one generation.
     *
     * Rules:
     * 1. A dead cell with exactly 3 live neighbors becomes alive.
     * 2. A live cell with 2 or 3 live neighbors survives.
     * 3. A live cell with fewer than 2 neighbors dies from isolation.
     * 4. A live cell with more than 3 neighbors dies from overpopulation.
     *
     * TODO: Complete this method.
     */
    public void update() {
        // TODO: Create a SECOND 2D boolean array for the next generation.

        boolean[][] newSociety = new boolean[rows][cols];

        for (int r  = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cellAt(r,c) == true) {
                    // alive
                    int neighbors = neighborCount(r,c);
                    if (neighbors < 2) {
                        // underpopulation
                        newSociety[r][c] = false;
                        continue;
                    } else if  (neighbors == 2 || neighbors == 3) {
                        // stays alive
                        newSociety[r][c] = true;
                        continue;
                    } else if  (neighbors > 3) {
                        // overpopulation
                        newSociety[r][c] = false;
                        continue;
                    }
                } else {
                    // dead
                    if (neighborCount(r,c) == 3) {
                        // neighbors
                        newSociety[r][c] = true;
                        continue;
                    }
                }
                newSociety[r][c] = society[r][c];
            }
        }

        society = newSociety;
    }

    /**
     * Returns a text version of the board.
     * O = live cell
     * . = dead cell
     *
     * TODO: Complete this method.
     */
    @Override
    public String toString() {
        // TODO: Use nested loops to build one String containing the board.
        //       Add a newline after every row.

        String board = "";

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if  (cellAt(r,c) == true) {
                    board += "O";
                } else {
                    board += ".";
                }
            }
            board += "\n";
        }

        return board;
    }
}
