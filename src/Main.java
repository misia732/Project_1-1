import java.io.File;
import java.util.Scanner;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            // Adapt this when you want to read and display a different file.
            String fileName = "GraduateGrades.csv";
            String fileName2 = "StudentInfo.csv";
            String fileName3 = "CurrentGrades.csv";

            File file = new File(fileName);
            File file2 = new File(fileName2);
            File file3 = new File(fileName3);

            // This code uses two Scanners, one which scans the file line per line
            Scanner fileScanner = new Scanner(file);
            Scanner fileScanner2 = new Scanner(file2);
            Scanner fileScanner3 = new Scanner(file3);

            int linesDone = 0;
            while (fileScanner.hasNextLine() && linesDone < 25) {
                String line = fileScanner.nextLine();
                linesDone++;

                // and one that scans the line entry per entry using the commas as delimiters
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(",");
                while (lineScanner.hasNext()) {

                    // Separate commands can be used depending on the types of the entries
                    // (i) and (s) are added to the printout to show how each entry is recognized
                    if (lineScanner.hasNextInt()) {
                        int i = lineScanner.nextInt();
                        // System.out.print("(i)" + i + " ");
                    } else if (lineScanner.hasNextDouble()) {
                        double d = lineScanner.nextDouble();
                        // System.out.print("(d)" + d + " ");
                    } else {
                        String s = lineScanner.next();
                        // System.out.print("(s)" + s + " ");
                    }
                }


                lineScanner.close();
                //System.out.println();
            }


            // Transform dataset from csv into a two-dimensional array

            // numRows = number of students + 1 (because of the "StudentId" entry)
            // numCols = number of courses + 1 (because of the name of the course)

            String[][] arrayGraduateGrades = csvInto2Darray("GraduateGrades.csv", 18320, 31);

            // numRows = number of students + 1 (maximum 1129)
            // numCols = number of courses + 1 (maximum 31)

            String[][] arrayCurrentGrades = csvInto2Darray("CurrentGrades.csv", 1129, 31);

            // numRows = numbers of students + 1 (maximum 1129)
            // numCols is always equal to 5, because we have only 5 columns !!!!!!
            String[][] studentInfoArray = csvInto2Darray("StudentInfo.csv", 1129, 5);

            // Transposes the matrix
            String[][] transposedMatrix = transposeMatrix(arrayGraduateGrades);

            double[][] doubleArray = array1dNumericalValues(transposedMatrix);


            /*
            How to call this method:

            For Lal Count:
            - To view the upper quartile average, use "high"
            - To view the lower quartile average, use "low"

            For other values:
            - Set the property to "Suruna Value", "Hurni Level", or "Volta"
            - Set the parameter based on the chosen property:
                - For Suruna Value: "doot", "lobi", or "nulp"
                - For Hurni Level: "low" or "high".
                - For Volta: "1", "2", "3", "4", or "5" stars

            i = course index, where 1 = JTE-234, 2 = ATE-003, and so forth.

            */
            for (int i = 1; i < 2; i++) {
                System.out.println();
                System.out.println("for course " + i);
                System.out.println("----------------------------------------------");
                System.out.println(processValuesAverages(studentInfoArray, arrayCurrentGrades, "volta", "5 stars", i));
                System.out.println(processValuesAverages(studentInfoArray, arrayCurrentGrades, "suruna value", "doot", i));
                processLalCount(studentInfoArray, arrayCurrentGrades, "high", i);
                processLalCount(studentInfoArray, arrayCurrentGrades, "low", i);


            }



            fileScanner.close();
            fileScanner2.close();
            fileScanner3.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String[][] csvInto2Darray(String fileName, int numRows, int numCols) {

        String[][] arrayGraduateGrades = new String[numRows][numCols];
        int row = 0;
        try {
            File file = new File(fileName);
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine() && row < numRows) {
                String line = fileScanner.nextLine();
                // Split the line by commas to populate the 2D array
                String[] entries = line.split(",");
                for (int col = 0; col < numCols; col++) {
                    arrayGraduateGrades[row][col] = entries[col];
                }
                row++;
            }
            fileScanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        for (int i = 0; i < numRows; i++) {
//            for (int j = 0; j < numCols; j++) {
//                System.out.print(arrayGraduateGrades[i][j] + " ");
//            }
//            System.out.println();
//        }

        return arrayGraduateGrades;

    }

    public static String[][] transposeMatrix(String[][] array2D) {

        int numRows = array2D.length;
        int numCols = array2D[0].length;

        // int transposedNumRows = numCols;
        // int transposedNumCols = numRows;
        String[][] transposedMatrix = new String[numCols][numRows];


        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedMatrix[j][i] = array2D[i][j];
            }
        }

        // Display the transposed data
//        for (int i = 0; i < numCols; i++) {
//            for (int j = 0; j < numRows; j++) {
//                System.out.print(transposedMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }

        return transposedMatrix;

    }

    public static double[][] array1dNumericalValues(String[][] matrix) {

        double[][] result = new double[matrix.length - 1][matrix[0].length - 1];

        // Create 2D array with only numerical values
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                result[i - 1][j - 1] = Double.parseDouble(matrix[i][j]);
            }
        }

        return result;
    }

    public static double processValuesAverages(String[][] studentInfo, String[][] grades, String property, String parameter, int courseIndex) {

        //initialize additional variables
        double gradesSum = 0;
        double parameterCount = 0;
        double avgScore = 0;
        int whichProperty = 0;

        // transforms the course to a corresponding index in the studentInfo file while ignoring spacing and case

        if (property.replaceAll("\\s", "").equalsIgnoreCase("SurunaValue")) {
            whichProperty = 1;
        } else if (property.replaceAll("\\s", "").equalsIgnoreCase("HurniLevel")) {
            whichProperty = 2;
        } else if (property.replaceAll("\\s", "").equalsIgnoreCase("LalCount")) {
            whichProperty = 3;
        } else if (property.replaceAll("\\s", "").equalsIgnoreCase("Volta")) {
            whichProperty = 4;
        }

        // Loop over each student's Suruna Value count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String value = studentInfo[i][whichProperty];

            // Locate student's score for ATE-003 course
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check if the grade is not "NG"
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (value.equals(parameter)) {
                            gradesSum += score;
                            parameterCount++;

                        }
                    }
                }
            }

        }

        avgScore = gradesSum / parameterCount;
        return avgScore;


    }

    public static double processLalCount(String[][] studentInfo, String[][] grades, String parameter, int courseIndex) {

        // Initializing variables
        double upperQuartile = 0;
        int upperQuartileCount = 0;
        double lowerQuartile = 0;
        int lowerQuartileCount = 0;
        double lalCount = 0;

        // Looping over each students Lal count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            lalCount = Double.parseDouble(studentInfo[i][3]);

            // Locate students score for a course
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check for ng
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (lalCount >= 90) {
                            upperQuartile += score;
                            upperQuartileCount++;
                        } else if (lalCount <= 59) {
                            lowerQuartile += score;
                            lowerQuartileCount++;
                        }
                    }
                }
            }
        }
        //calculating final averages checks also if not dividing by 0
        double avgScoreUpperQuartile = upperQuartileCount != 0 ? upperQuartile / upperQuartileCount : 0;
        double avgScoreLowerQuartile = lowerQuartileCount != 0 ? lowerQuartile / lowerQuartileCount : 0;


        if (parameter.equalsIgnoreCase("High")) {
            return avgScoreUpperQuartile;
        } else {
            return avgScoreLowerQuartile;
        }
    }

}










