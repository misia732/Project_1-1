package project.gui;

import java.io.File;
import java.util.Scanner;
import java.util.Arrays;


public class Main {

    // test

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

            // Stores the mean grade for every course
            double[] averages = courseAverages(arrayCurrentGrades);
            System.out.println();
            System.out.println("Our code performs data analysis of 18320 graduate students and 1129 current students. "
                    + "By group 16");
            System.out.println();

            // Finds the hardest and the easiest course based on the mean per grade
            int easiestIndex1 = -1, easiestIndex2 = -1;
            int hardestIndex1 = -1, hardestIndex2 = -1;

            for (int i = 0; i < averages.length; i++) {
                if (averages[i] != -1) {
                    if (easiestIndex1 == -1 || averages[i] < averages[easiestIndex1]) {
                        easiestIndex2 = easiestIndex1;
                        easiestIndex1 = i;
                    } else if (easiestIndex2 == -1 || averages[i] < averages[easiestIndex2]) {
                        easiestIndex2 = i;
                    }

                    if (hardestIndex1 == -1 || averages[i] > averages[hardestIndex1]) {
                        hardestIndex2 = hardestIndex1;
                        hardestIndex1 = i;
                    } else if (hardestIndex2 == -1 || averages[i] > averages[hardestIndex2]) {
                        hardestIndex2 = i;
                    }
                }
            }

            System.out.println("Hardest courses for graduate students: " + arrayGraduateGrades[0][easiestIndex1 + 1] + " (Average grade: " + averages[easiestIndex1] + ") and " + arrayGraduateGrades[0][easiestIndex2 + 1] + " (Average grade: " + averages[easiestIndex2] + ")");
            System.out.println("Easiest courses for graduate students: " + arrayGraduateGrades[0][hardestIndex1 + 1] + " (Average grade: " + averages[hardestIndex1] + ") and " + arrayGraduateGrades[0][hardestIndex2 + 1] + " (Average grade: " + averages[hardestIndex2] + ")");
            System.out.println();

            // Transforms from String[][] to double[][] to compare the values
            double[][] matrix = array1dNumericalValues(arrayGraduateGrades);

            // Calculates which students graduated cum-laude
            int[] rowsWithAverageGreaterThanEqual9 = findRowsWithAverageGreaterThanEqual(matrix, 8.0);
            double numStudentsWithCumLaude = 0;
            double numStudents = matrix.length;

            if (rowsWithAverageGreaterThanEqual9.length > 0) {
                //System.out.println("Students that graduated cum-laude: ");
                for (int rowNumber : rowsWithAverageGreaterThanEqual9) {
                    // the IDs
                    // System.out.println(rowNumber);
                    numStudentsWithCumLaude++;
                }
                System.out.println();
            } else {
                System.out.println("No students graduated cum-laude.");
            }

            System.out.println(numStudentsWithCumLaude + " students graduated cum - laude");
            System.out.println("That is " + (numStudentsWithCumLaude / numStudents) * 100 + "% of all of the students");
            System.out.println();

            // calculate Pearson's correlation

            double[][] doubleArray = array1dNumericalValues(transposedMatrix);

            int numCourses = doubleArray.length;
            boolean correlationFound = false;
            double threshold = 0.5;



            // Calculates the spread of the middle 50% of the data (IQR)
            // System.out.println("The spread of the middle 50% of the data (IQR): ");
            //System.out.println();
            // IQRprint(transposedMatrix);
            // System.out.println();

            // Analyses how many NG Entries are in the dataset
            analyzeNGEntries(arrayCurrentGrades);
            System.out.println();

            String[][] arrayCurrentGradesTransposed = transposeMatrix(arrayCurrentGrades);

            // Calculates how many students are eligible to graduate soon
            countNGPerStudent(arrayCurrentGradesTransposed);
            //System.out.println("\nThe average of each student: ");
            // printStudentAverages(arrayCurrentGradesTransposed);
            System.out.println();


            System.out.println();
            // findStudent(studentInfoArray, "1000243");
            System.out.println();


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

    public static int[] findRowsWithAverageGreaterThanEqual(double[][] array, double threshold) {

        int rowCount = array.length;
        int colCount = array[0].length;

        int count = 0;
        for (int i = 0; i < rowCount; i++) { // header
            double sum = 0;
            for (int j = 0; j < colCount; j++) { // header
                sum += array[i][j];
            }
            double average = (double) sum / (colCount); // Subtract 1 for the skipped

            if (average >= threshold) {
                count++;
            }
        }

        int[] result = new int[count];
        int index = 0;
        for (int i = 0; i < rowCount; i++) {
            double sum = 0;
            for (int j = 0; j < colCount; j++) {
                sum += array[i][j];
            }
            double average = (double) sum / (colCount);

            if (average >= threshold) {
                result[index++] = i; // Subtract 1 to get the original row number (excluding the first row).
            }
        }
        return result;
    }

    public static double[] courseAverages(String[][] data) {  // !!!!!!!!!! Fixed here
        double[] averages = new double[data[0].length - 1];
        for (int j = 1; j < data[0].length; j++) {
            double sum = 0;
            int count = 0;
            int ngCount = 0;  // !!!!!!!!!!
            for (int i = 1; i < data.length; i++) {
                if (data[i][j] != null && !data[i][j].isEmpty()) {
                    if (data[i][j].equalsIgnoreCase("NG")) {
                        ngCount++;  //!!!!!!!!!!
                    } else {
                        sum += Double.parseDouble(data[i][j]);
                        count++;
                    }
                }
            }
            if (1.0 * ngCount / (ngCount + count) > 0.9) {  //!!!!!!!!!!
                averages[j - 1] = -1;
            } else {
                averages[j - 1] = (count == 0) ? 0 : sum / count;
            }
        }
        return averages;
    }

    public static void analyzeNGEntries(String[][] data) {
        int numRows = data.length;
        int numCols = data[0].length;

        int[] colCounts = new int[numCols];
        int maxCount = 0;
        int maxCol = -1;

        for (int row = 1; row < numRows; row++) {  // Start from 1, header
            for (int col = 1; col < numCols; col++) {  // Same here
                String currentEntry = data[row][col];

                if (currentEntry.contains("NG")) { // check nG!!!
                    colCounts[col]++;
                    if (colCounts[col] > maxCount) { // updating
                        maxCount = colCounts[col];
                        maxCol = col;
                    }
                }
            }
        }

        if (maxCol != -1) {
            String columnName = data[0][maxCol];
            int amountNG = colCounts[maxCol];
            double percentage = (double) amountNG / (double) (numRows - 1) * 100.0;
            double percentageThreshold = 75.0;

            System.out.println("Column with the most NG entries is: " + columnName);
            System.out.println("The percentage of NG entries for " + columnName + " is " + percentage + "%");
            if (percentage >= percentageThreshold) {
                System.out.println("As the percentage is equal or higher than " + percentageThreshold + "%, we can safely assume that classes are offered in an order.");
            } else {
                System.out.println("As the percentage is lower than " + percentageThreshold + "%, we can safely assume that classes are not offered in an order.");
            }
        } else {
            System.out.println("No 'NG' entries found.");
        }
    }

    public static void countNGPerStudent(String[][] matrix) {
        double numCourses = matrix.length - 1; // header
        int count = 0;
        int firstYears = 0;
        int secondYears = 0;
        int thirdYearsWithHighAvg = 0;

        for (int j = 1; j < matrix[0].length; j++) {
            double ngCounts = 0;
            double totalScore = 0;
            int gradeCounts = 0;

            for (int i = 1; i < matrix.length; i++) {
                if (matrix[i][j].equals("NG")) {
                    ngCounts++;
                } else {

                        totalScore += Double.parseDouble(matrix[i][j]);
                        gradeCounts++;

                }
            }

            double averageScore = (gradeCounts > 0) ? totalScore / gradeCounts : 0;


            if (ngCounts <= 5) {
                //System.out.println("Student: " + matrix[0][j] + " will graduate soon");
                count++;

                if (averageScore > 6.5) {
                    thirdYearsWithHighAvg++;
                }
            } else {
                //System.out.println("Student: " + matrix[0][j] + " will not graduate, because their NG Count is: " + (ngCounts / numCourses) * 100 + "%");
                if (ngCounts / numCourses > 0.66) {
                    firstYears++;
                } else if (ngCounts / numCourses > 0.33 && ngCounts / numCourses < 0.66) {
                    secondYears++;
                }
            }
        }

        System.out.println();
        System.out.println(count + " students will graduate this year");
        System.out.println("First years: " + firstYears);
        System.out.println("Second years: " + secondYears);
        System.out.println("Third years: " + count);
        System.out.println("Third-year students with an average grade above 6.5: " + thirdYearsWithHighAvg); // Output the new count
    }




}








