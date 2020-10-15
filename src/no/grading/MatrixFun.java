package no.grading;

public class MatrixFun {

    private String getTaskText() {
        return  "08 02 22 97 38 15 00 40 00 75 04 05 07 78 52 12 50 77 91 08\n"+
                "49 49 99 40 17 81 18 57 60 87 17 40 98 43 69 48 04 56 62 00\n"+
                "81 49 31 73 55 79 14 29 93 71 40 67 53 88 30 03 49 13 36 65\n"+
                "52 70 95 23 04 60 11 42 69 24 68 56 01 32 56 71 37 02 36 91\n"+
                "22 31 16 71 51 67 63 89 41 92 36 54 22 40 40 28 66 33 13 80\n"+
                "24 47 32 60 99 03 45 02 44 75 33 53 78 36 84 20 35 17 12 50\n"+
                "32 98 81 28 64 23 67 10 26 38 40 67 59 54 70 66 18 38 64 70\n"+
                "67 26 20 68 02 62 12 20 95 63 94 39 63 08 40 91 66 49 94 21\n"+
                "24 55 58 05 66 73 99 26 97 17 78 78 96 83 14 88 34 89 63 72\n"+
                "21 36 23 09 75 00 76 44 20 45 35 14 00 61 33 97 34 31 33 95\n"+
                "78 17 53 28 22 75 31 67 15 94 03 80 04 62 16 14 09 53 56 92\n"+
                "16 39 05 42 96 35 31 47 55 58 88 24 00 17 54 24 36 29 85 57\n"+
                "86 56 00 48 35 71 89 07 05 44 44 37 44 60 21 58 51 54 17 58\n"+
                "19 80 81 68 05 94 47 69 28 73 92 13 86 52 17 77 04 89 55 40\n"+
                "04 52 08 83 97 35 99 16 07 97 57 32 16 26 26 79 33 27 98 66\n"+
                "88 36 68 87 57 62 20 72 03 46 33 67 46 55 12 32 63 93 53 69\n"+
                "04 42 16 73 38 25 39 11 24 94 72 18 08 46 29 32 40 62 76 36\n"+
                "20 69 36 41 72 30 23 88 34 62 99 69 82 67 59 85 74 04 36 16\n"+
                "20 73 35 29 78 31 90 01 74 31 49 71 48 86 81 16 23 57 05 54\n"+
                "01 70 54 71 83 51 54 69 16 92 33 48 61 43 52 01 89 19 67 48";
    }

    private String theHighestProductCells = null;

    private long maxSoFar = Long.MIN_VALUE;

    public MatrixFun(int sequenceLength) { this.sequenceLength = sequenceLength; }
    public MatrixFun() { this.sequenceLength = defaultSequenceLength; }

    private final static int defaultSequenceLength = 4;
    private final int sequenceLength;

    public long getMaxSoFar() {
        return maxSoFar;
    }

    public static void main(String[] args) {
        var fun = new MatrixFun();

        try {
            fun.computeAndUpdateHighest(fun.getTaskText());
            fun.displayResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void computeAndUpdateHighest(String text) throws IncomputableException {
        var data = populateMatrixFromText(text);
        var matrix = data.matrix;
        var lastColumn = data.columnCount - 1;
        var lastRow = data.rowCount - 1;

        handleInvalidDimensionsOrSequenceLength(lastColumn, lastRow);

        for (int row = 0; row <= lastRow; row++)
            for (int column = 0; column <= lastColumn; column++) {

                if (column <= lastColumn - sequenceLength + 1) {
                    computeAlongDirectionAndUpdateHighest(Direction.Right, matrix, row, column);

                    if (row + 1 >= sequenceLength)
                        computeAlongDirectionAndUpdateHighest(Direction.DiagonallyUpRight, matrix, row, column);
                }

                if (row <= lastRow - sequenceLength + 1) {
                    computeAlongDirectionAndUpdateHighest(Direction.Down, matrix, row, column);

                    if (column <= lastColumn - sequenceLength + 1)
                        computeAlongDirectionAndUpdateHighest(Direction.DiagonallyDownRight, matrix, row, column);
                }
            }
    }

    private void handleInvalidDimensionsOrSequenceLength(int lastInitialColumn, int lastRow) throws IncomputableException {
        var prefix = "Not computable when the ";
        var postfix = " is less than " + sequenceLength;

        if (sequenceLength < 1) {
            throw new IncomputableException(prefix + "sequenceLength is less than 1");
        } else if (lastInitialColumn + 1 < sequenceLength) {
            if (lastRow + 1 < sequenceLength)
                throw new IncomputableException(prefix + "column count (and row count for that matter)" + postfix);
            throw new IncomputableException(prefix + "column count" + postfix);
        } else if (lastRow + 1 < sequenceLength) {
            throw new IncomputableException(prefix + "row count" + postfix);
        }
    }

    private void displayResult() {
        System.out.println("Max: " + maxSoFar + " = " + theHighestProductCells);
    }

    /**
     * Gives an integer matrix representation of the text if possible
     * @param text Text representation of integers separated by space horizontally and \n vertically
     * @return A matrix (MatrixData) representation that can possibly be sparse
     * (filled with zero for possibly non-existing values if the text input has different number of columns/numbers in each line)
     */
    private MatrixData populateMatrixFromText(String text) {
        var lines = text.split("\n");
        var rowCount = lines.length;
        var columnCount = getMaxColumnCount(lines);
        var matrix = new int[rowCount][columnCount];

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            var elementsAtRow = lines[rowIndex].split(" ");

            for (int columnIndex = 0; columnIndex < elementsAtRow.length; columnIndex++)
                matrix[rowIndex][columnIndex] = Integer.parseInt(elementsAtRow[columnIndex]);
        }

        return new MatrixData(matrix, rowCount, columnCount);
    }

    /**
     * Get the maximum columns in the lines
     * @param lines Text separated by space between numbers only and newlines
     * @return The maximum columns (number count) in the lines
     */
    private int getMaxColumnCount(String[] lines) {
        int maxColumnCount = 0;

        for (String line : lines) {
            var columnsInLine = line.split(" ").length;
            if (columnsInLine > maxColumnCount)
                maxColumnCount = columnsInLine;
        }

        return maxColumnCount;
    }

    private void computeAlongDirectionAndUpdateHighest(Direction direction, int[][] matrix, int row, int column) {
        var sequence = new MatrixElementSequence(generateOffsets(direction), matrix, row, column);
        computeProductAndUpdateHighest(direction, sequence);
    }

    /**
     * Generate offsets according to direction and sequenceLength
     * @param direction Direction (enum)
     * @return The offsets
     */
    private int[][] generateOffsets(Direction direction) {
        var offsets = new int[sequenceLength - 1][2];

        for (int i = 0; i < sequenceLength - 1; i++) {
            switch (direction) {
                case DiagonallyUpRight:
                    offsets[i][0] = -(offsets[i][1] = (i + 1));
                    break;

                case DiagonallyDownRight:
                    offsets[i][0] = (offsets[i][1] = (i + 1));
                    break;

                case Down:
                    offsets[i][0] = (i + 1);
                    break;

                case Right:
                    offsets[i][1] = (i + 1);
                    break;

                default:
                    break;
            }
        }

        return offsets;
    }

    /**
     * Computes the product of itself (specified by row and column)
     * and e.g. if the sequence length is 4, the three other matrix elements specified by offsets, i.e.
     *   matrix [ row                 ] [ col                ]
     * * matrix [ row + offsets[0][0] ] [ col + offsets[0][1] ]
     * * matrix [ row + offsets[1][0] ] [ col + offsets[1][1] ]
     * * matrix [ row + offsets[2][0] ] [ col + offsets[2][1] ],
     * encapsulated in a MatrixElementSequence sequence,
     * plus updates highest so far.
     * @param direction The kind of sequence you want to call it (in case it becomes the highest product)
     * @param sequence Wrapper object containing
     */
    private void computeProductAndUpdateHighest(Direction direction, MatrixElementSequence sequence) {
        int[][] matrix = sequence.matrix;
        int[][] offsets = sequence.offsets;
        int row = sequence.row;
        int column = sequence.column;

        var product =  matrix[row][column];
        for (int[] offset : offsets) product *= matrix[row + offset[0]][column + offset[1]];

        if (product > maxSoFar) {
            maxSoFar = product;
            theHighestProductCells = elaborateOnTheHighestProductNumbers(direction, sequence);
        }
    }

    /**
     * Get a description of what kind of sequence of elements and which
     * @param direction The kind of sequence you want to call it (in case it becomes the highest product)
     * @param sequence The sequence of elements in the matrix
     * @return A human-readable description of what kind of sequence of elements and which
     */
    private String elaborateOnTheHighestProductNumbers(Direction direction, MatrixElementSequence sequence) {
        int[][] matrix = sequence.matrix;
        int[][] offsets = sequence.offsets;
        int row = sequence.row;
        int column = sequence.column;

        StringBuilder elaborationBuilder = new StringBuilder("" + matrix[row][column]);
        for (int i = 0; i < sequenceLength - 1; i++)
            elaborationBuilder.append(" * ").append(matrix[row + offsets[i][0]][column + offsets[i][1]]);

        elaborationBuilder  .append(" from ")
            .append(direction)
            .append(": matrix[").append(row).append("][").append(column).append("] ");

        for (int i = 0; i < sequenceLength - 1; i++)
            elaborationBuilder.append("* matrix[").append(row + offsets[i][0]).append("][")
                .append(column + offsets[i][1]).append("] ");

        return elaborationBuilder.toString();
    }

    enum Direction {
        Right,
        DiagonallyUpRight,
        DiagonallyDownRight,
        Down
    }

    static class MatrixData {
        private final int[][] matrix;
        private final int rowCount;
        private final int columnCount;

        public MatrixData(int[][] matrix, int rowCount, int columnCount) {
            this.matrix = matrix;
            this.rowCount = rowCount;
            this.columnCount = columnCount;
        }
    }

    static class MatrixElementSequence {
        /** The offsets */
        private final int[][] offsets;

        /** The input matrix */
        private final int[][] matrix;

        /** The reference row */
        private final int row;

        /** The reference column */
        private final int column;

        /**
         * @param offsets The offsets
         * @param matrix The input matrix
         * @param row The reference row
         * @param column The reference column
         */
        public MatrixElementSequence(int[][] offsets, int[][] matrix, int row, int column) {
            this.offsets = offsets;
            this.matrix = matrix;
            this.row = row;
            this.column = column;
        }
    }

    public static class IncomputableException extends Exception {
        public IncomputableException(String message) {
            super(message);
        }
    }
}