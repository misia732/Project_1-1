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

            for (int i = 0; i < numCourses; i++) {
                for (int j = i + 1; j < numCourses; j++) {
                    double correlation = calculateCorrelation(doubleArray, i, j);
                    if (correlation > threshold || correlation < -threshold) {
                        correlationFound = true;
                        // System.out.println("Correlation between Course " + i + " and Course " + j + ": " + correlation);
                        System.out.println("Correlation between Course " + transposedMatrix[i + 1][0] + " and Course " + transposedMatrix[j + 1][0] + ": " + correlation);
                    }
                }
            }

            if (!correlationFound) {
                System.out.println("Correlation between courses was not found");
                System.out.println();
            }

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

            for (int i = 1; i < 31; i++) {
                System.out.println();
                System.out.println("for course " + i);
                printCourseAverage(averages, i);
                printVarianceCourse(arrayCurrentGrades, i);
                System.out.println("----------------------------------------------");
                processSurunaValue(studentInfoArray, arrayCurrentGrades, "nulp", i);
                processSurunaValue(studentInfoArray, arrayCurrentGrades, "doot", i);
                processSurunaValue(studentInfoArray, arrayCurrentGrades, "lobi", i);
                processLalCount(studentInfoArray, arrayCurrentGrades, i);
                processHurniValue(studentInfoArray, arrayCurrentGrades, i);
                processVoltaValue(studentInfoArray, arrayCurrentGrades, i);
                System.out.println("----------------------------------------------");
                calculateVarianceSurunaDoot(arrayCurrentGrades, studentInfoArray, i, "nulp");
                NGpredictionDoot(arrayCurrentGrades, studentInfoArray, i, "nulp");

            }

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

    public static int median(Double[] array, int m, int r) {

        int n = r - m + 1;
        n = ((n + 1) / 2) - 1;
        return n + 1;
    }

    public static double IQR(Double[] array, int n) {
        // Calculate Q1 and Q3 positions

        Arrays.sort(array);
        int mid_index = median(array, 0, n);  //the mid - index

        double q1 = array[median(array, 0, mid_index)];

        double q3 = array[mid_index + median(array, mid_index + 1, n)];
        System.out.println(q1);
        System.out.println(q3);

        // Calculate the interquartile range (IQR) for the subject

        return (q3 - q1);

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

    public static double calculateCorrelation(double[][] data, int course1, int course2) {
        int n = data[0].length;
        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = data[course1][i];
            y[i] = data[course2][i];
        }
        double xMean = calculateMean(x);
        double yMean = calculateMean(y);

        double sumProductDeviations = 0.0;
        double sumSquaredDeviationsX = 0.0;
        double sumSquaredDeviationsY = 0.0;

        for (int i = 0; i < n; i++) {
            double xDeviation = x[i] - xMean;
            double yDeviation = y[i] - yMean;
            sumProductDeviations += xDeviation * yDeviation;
            sumSquaredDeviationsX += xDeviation * xDeviation;
            sumSquaredDeviationsY += yDeviation * yDeviation;
        }

        double correlation = sumProductDeviations / Math.sqrt(sumSquaredDeviationsX * sumSquaredDeviationsY);
        return correlation;
    }

    public static double calculateMean(double[] data) {
        int n = data.length;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += data[i];
        }
        return sum / n;
    }

    public static void processSurunaValue(String[][] studentInfo, String[][] grades, String parameter, int courseIndex) {

        //initialize additional variables
        double gradesSum = 0;
        double parameterCount = 0;
        double avgScore = 0;
        double variance = 0;

        // Loop over each student's Suruna Value count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String surunaValue = studentInfo[i][1];

            // Locate student's score for ATE-003 course
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check if the grade is not "NG"
                    if (!grades[j][courseIndex].equals("NG")) { // !!!!!!!!!!!!!!!!!!!!!!!!!!
                        double score = Double.parseDouble(grades[j][courseIndex]);  // Assuming column index for ATE-003 is 2

                        if (surunaValue.equals(parameter)) {
                            gradesSum += score;
                            parameterCount++;

                        }
                    }
                }
            }

        }

        avgScore = gradesSum / parameterCount;

        // Calculate variance
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String star = studentInfo[i][4];

            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (star.equals("low")) {
                            variance += Math.pow(score - avgScore, 2);
                        }
                    }
                }
            }
        }

        double varianceSurunaValue = parameterCount != 0 ? variance / parameterCount : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!

        // System.out.println(parameterCount);
        System.out.println("Average score for students with Suruna Value: " + parameter + " - " + avgScore);
        // System.out.println("Variance for students with Suruna Value: " + parameter + " - " + varianceSurunaValue);


    }

    public static void processLalCount(String[][] studentInfo, String[][] grades, int courseIndex) {

        //initializingg
        double upperQuartile = 0;
        int upperQuartileCount = 0;
        double lowerQuartile = 0;
        int lowerQuartileCount = 0;
        double upperQuartileVariance = 0;
        double lowerQuartileVariance = 0;
        double lalCount = 0;

        // Looping over each students Lal count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            lalCount = Double.parseDouble(studentInfo[i][3]);

            // Locate students score for ATE-003 course
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check for ng
                    if (!grades[j][courseIndex].equals("NG")) { // !!!!!!!!!!!!!!!!!!!!!!!!!!
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
        double avgScoreUpperQuartile = upperQuartileCount != 0 ? upperQuartile / upperQuartileCount : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!
        double avgScoreLowerQuartile = lowerQuartileCount != 0 ? lowerQuartile / lowerQuartileCount : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!

        // Calculate variance
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String star = studentInfo[i][4];

            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (lalCount >= 85) {
                            upperQuartileVariance += Math.pow(score - avgScoreUpperQuartile, 2);
                        } else if (lalCount <= 73) {
                            lowerQuartileVariance += Math.pow(score - avgScoreLowerQuartile, 2);
                        }
                    }
                }
            }
        }

        // Calculate variance (divide by n-1 for sample variance)
        double varianceUpperQuartile = upperQuartileCount != 0 ? upperQuartileVariance / (upperQuartileCount - 1) : 0;
        double varianceLowerQuartile = lowerQuartileCount != 0 ? lowerQuartileVariance / (lowerQuartileCount - 1) : 0;


        // System.out.println("sum grades: " + upperQuartile + " count: " + upperQuartileCount);
        System.out.println("Average score for students above the Lal count average: " + avgScoreUpperQuartile);
        System.out.println("Average score for students below the Lal count average: " + avgScoreLowerQuartile);
        // System.out.println("Variance for students above the Lal count average: " + varianceUpperQuartile);
        // System.out.println("Variance for students below the Lal count average: " + varianceLowerQuartile);

    }

    public static void processHurniValue(String[][] studentInfo, String[][] grades, int courseIndex) {

        //initialize  variables
        double highValueCount = 0;
        double highValueScore = 0;
        double lowValueCount = 0;
        double lowValueScore = 0;
        double highValueVariance = 0;
        double lowValueVariance = 0;

        // Loop over each student's Hurny Value count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String hurniValue = studentInfo[i][2];

            // Locate student's score for course index
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check if the grade is not "NG"
                    if (!grades[j][courseIndex].equals("NG")) { // !!!!!!!!!!!!!!!!!!!!!!!!!!
                        double score = Double.parseDouble(grades[j][courseIndex]);  // Assuming column index for a given course

                        if (hurniValue.equals("low")) {
                            lowValueScore += score;
                            lowValueCount++;
                        } else if (hurniValue.equals("high")) {
                            highValueScore += score;
                            highValueCount++;
                        }
                    }
                }
            }
        }
        //calculating final averages checks also if not dividing by 0
        double avgHighHurniValue = highValueCount != 0 ? highValueScore / highValueCount : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!
        double avgLowHurniValue = lowValueCount != 0 ? lowValueScore / lowValueCount : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!

        // Calculate variance
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String hurniLevel = studentInfo[i][2];

            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (hurniLevel.equals("low")) {
                            lowValueVariance += Math.pow(score - avgHighHurniValue, 2);
                        } else if (hurniLevel.equals("high")) {
                            highValueVariance += Math.pow(score - avgLowHurniValue, 2);
                        }
                    }
                }
            }
        }

        // Calculate variance (divide by n-1 for sample variance)
        double varianceHighValue = highValueCount != 0 ? highValueVariance / (highValueCount - 1) : 0;
        double varianceLowValue = lowValueCount != 0 ? lowValueVariance / (lowValueCount - 1) : 0;

        System.out.println("Average score for students with high HurniValue: " + avgHighHurniValue);
        System.out.println("Average score for students with low HurniValue: " + avgLowHurniValue);
        System.out.println("Variance for students with high HurniValue: " + varianceHighValue);
        System.out.println("Variance for students with low HurniValue: " + varianceLowValue);

    }

    public static void processVoltaValue(String[][] studentInfo, String[][] grades, int courseIndex) {

        //initialize  variables
        double stars5Count = 0;
        double stars5Score = 0;
        double star1Count = 0;
        double star1Score = 0;
        double stars5Variance = 0;
        double star1Variance = 0;

        // Loop over each student's Volta Value count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String star = studentInfo[i][4];

            // Locate student's score for course index
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check if the grade is not "NG"
                    if (!grades[j][courseIndex].equals("NG")) { // !!!!!!!!!!!!!!!!!!!!!!!!!!
                        double score = Double.parseDouble(grades[j][courseIndex]);  // Assuming column index for a given course

                        if (star.equals("5 stars")) {
                            stars5Score += score;
                            stars5Count++;
                        } else if (star.equals("1 star")) {
                            star1Score += score;
                            star1Count++;
                        }
                    }
                }
            }

        }

        //calculating final averages checks also if not dividing by 0
        double avg5stars = stars5Count != 0 ? stars5Score / stars5Count : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!
        double avg1star = star1Count != 0 ? star1Score / star1Count : 0; // !!!!!!!!!!!!!!!!!!!!!!!!!!

        // Calculate variance
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            String star = studentInfo[i][4];

            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (star.equals("5 stars")) {
                            stars5Variance += Math.pow(score - avg5stars, 2);
                        } else if (star.equals("1 star")) {
                            star1Variance += Math.pow(score - avg1star, 2);
                        }
                    }
                }
            }
        }

        // Calculate variance (divide by n-1 for sample variance)
        double variance5stars = stars5Count != 0 ? stars5Variance / (stars5Count - 1) : 0;
        double variance1star = star1Count != 0 ? star1Variance / (star1Count - 1) : 0;


        // System.out.println("5 stars count: " + stars5Count + " 1 star count: " + star1Count);
        System.out.println("Average score for students with VoltaValue: 5 stars - " + avg5stars);
        System.out.println("Average score for students with low VoltaValue: 1 star - " + avg1star);
        System.out.println("Variance for students with VoltaValue: 5 stars - " + variance5stars);
        System.out.println("Variance for students with low VoltaValue: 1 star - " + variance1star);

    }

    public static void printCourseAverage(double[] averages, int courseIndex) {
        // Check if the courseIndex is valid
        if (courseIndex >= 1 && courseIndex <= averages.length) {
            double average = averages[courseIndex - 1];
            if (average == -1) {
                System.out.println("The average for course " + courseIndex + " cannot be calculated due to excessive 'NG' grades.");
            } else {
                //this is a better print
                System.out.printf("The average for course %d is %.2f\n", courseIndex, average);
            }
        } else {
            System.out.println("Invalid course index: " + courseIndex);
        }
    }

    public static double printVarianceCourse(String[][] grades, int courseIndex) {
        double sumSquaredDifferences = 0;
        int count = 0;
        int ngCount = 0;

        // Calculate the mean for the selected course (courseIndex)
        double sum = 0;
        for (int i = 1; i < grades.length; i++) {
            if (grades[i][courseIndex] != null && !grades[i][courseIndex].isEmpty()) {
                if (grades[i][courseIndex].equalsIgnoreCase("NG")) {
                    ngCount++;
                } else {
                    sum += Double.parseDouble(grades[i][courseIndex]);
                    count++;
                }
            }
        }

        if (count == 0 || 1.0 * ngCount / (ngCount + count) > 0.9) {
            // Return a special value or handle this case as needed
            return -1;
        }

        double mean = sum / count;

        // Calculate the variance
        for (int j = 1; j < grades.length; j++) {
            if (grades[j][courseIndex].equals("NG")) {
                continue;
            }
            double grade = Double.parseDouble(grades[j][courseIndex]);
            double difference = grade - mean;
            sumSquaredDifferences += difference * difference;
            double variance = sumSquaredDifferences / (count - 1);
        }

        double variance = sumSquaredDifferences / (count - 1);
        System.out.println("Variance for course " + courseIndex + " for : " + variance);
        return variance;
    }

    public static void printStudentAverages(String[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        // Iterate over each student (column-wise)
        for (int j = 1; j < numCols; j++) {
            double sum = 0;
            int count = 0;

            // Iterate over each course for the current student
            for (int i = 1; i < numRows; i++) {
                if (!matrix[i][j].equals("NG")) {
                    sum += Double.parseDouble(matrix[i][j]);
                    count++;
                }
            }

            double average = count > 0 ? sum / count : 0.0;

            if (average > 6.5) {
                System.out.println("Student: " + matrix[0][j] + " is eligible to pass with an average of " + average);
            } else {
                System.out.println("Student: " + matrix[0][j] + " is not eligible to pass with an average: " + average);
            }
        }


    }

    public static void IQRprint(String[][] transposedMatrix) {
        //Transforming the two dimensions into one dimension
        for (int i = 1; i < transposedMatrix[i].length; i++) {
            String[] arrayGraduateGrades = new String[transposedMatrix[i].length - 1];
            System.arraycopy(transposedMatrix[i], 1, arrayGraduateGrades, 0, transposedMatrix[i].length - 1);

            Double[] doubleArray = new Double[arrayGraduateGrades.length];
            for (int j = 0; j < arrayGraduateGrades.length; j++) {
                doubleArray[j] = Double.parseDouble(arrayGraduateGrades[j]);
            }

            Arrays.sort(doubleArray);
            // System.out.println(Arrays.toString((doubleArray)));

            System.out.println("Subject " + i + ": IQR = " + IQR(doubleArray, doubleArray.length));
        }
    }

    public static void findStudent(String[][] studentData, String studentID) {

        for (String[] student : studentData) {
            if (student[0].equals(studentID)) {
                System.out.println("Student Number: " + student[0]);
                System.out.println("Suruna Value: " + student[1]);
                System.out.println("Hurni Level: " + student[2]);
                System.out.println("Lal Count: " + student[3]);
                System.out.println("Volta: " + student[4]);
                return; // Found the student, no need to continue searching
            }
        }
        System.out.println("Student with number " + studentID + " not found.");
    }

    public static double Surunadoot(String[][] grades, String[][] studentInfo, int courseIndex, String parameter) {
        int count = 0;
        double sum = 0;
        for (int i = 1; i < studentInfo.length; i++) {
            String count1 = studentInfo[i][1];
            if (count1.equals(parameter)) {
                for (int j = 1; j < grades.length; j++) {
                    if (grades[j][courseIndex].equals("NG") || !grades[j][0].equals(studentInfo[i][0])) {
                        continue;
                    }
                    double grade = Double.parseDouble(grades[j][courseIndex]);
                    if (studentInfo[i][1].equals(parameter)) {
                        sum += grade;
                        count++;
                    }
                }
            }
        }
        double averageHigh = (count > 0) ? (sum / count) : 0;
        System.out.println("Average grade for students with "+ parameter+ " level: " + averageHigh);
        return averageHigh;
    }

    public static double calculateVarianceSurunaDoot(String[][] grades, String[][] studentInfo, int courseIndex, String parameter) {
        int count = 0;
        double sum = 0;
        double mean = 0;
        double sumSquaredDifferences = 0.0;

        for (int i = 1; i < studentInfo.length; i++) {
            String count1 = studentInfo[i][1];
            if (count1.equals(parameter)) {
                for (int j = 1; j < grades.length; j++) {
                    if (grades[j][courseIndex].equals("NG") || !grades[j][0].equals(studentInfo[i][0])) {
                        continue;
                    }
                    double grade = Double.parseDouble(grades[j][courseIndex]);
                    sum += grade;
                    count++;
                }
            }
        }

        if (count > 0) {
            mean = sum / count;

            for (int i = 1; i < studentInfo.length; i++) {
                String count1 = studentInfo[i][1];
                if (count1.equals(parameter)) {
                    for (int j = 1; j < grades.length; j++) {
                        if (grades[j][courseIndex].equals("NG") || !grades[j][0].equals(studentInfo[i][0])) {
                            continue;
                        }
                        double grade = Double.parseDouble(grades[j][courseIndex]);
                        double difference = grade - mean;
                        sumSquaredDifferences += difference * difference;
                    }
                }
            }

        }
        double varience = sumSquaredDifferences / (count - 1);
        System.out.println("variance for students with Suruna : " + parameter +" = "+ varience);
        return varience;

    }

    public static double variancecourse(String[][] grades, String[][] studentInfo, int courseIndex, String parameter) {
        double sumSquaredDifferences = 0;
        int count = 0;
        int ngCount = 0;

        // Calculate the mean for the selected course (courseIndex)
        double sum = 0;
        for (int i = 1; i < grades.length; i++) {
            if (grades[i][courseIndex] != null && !grades[i][courseIndex].isEmpty()) {
                if (grades[i][courseIndex].equalsIgnoreCase("NG")) {
                    ngCount++;
                } else {
                    sum += Double.parseDouble(grades[i][courseIndex]);
                    count++;
                }
            }
        }

        if (count == 0 || 1.0 * ngCount / (ngCount + count) > 0.9) {
            // Return a special value or handle this case as needed
            return -1;
        }

        double mean = sum / count;

        // Calculate the variance
        for (int j = 1; j < grades.length; j++) {
            if (grades[j][courseIndex].equals("NG")) {
                continue;
            }
            double grade = Double.parseDouble(grades[j][courseIndex]);
            double difference = grade - mean;
            sumSquaredDifferences += difference * difference;
            double variance = sumSquaredDifferences / (count - 1);
        }

        double variance = sumSquaredDifferences / (count - 1);
        double Variancehigh = calculateVarianceSurunaDoot(grades, studentInfo, courseIndex, parameter);
        double variancereduction = variance - Variancehigh;
        System.out.println("Variance reduction for course " + courseIndex + " is : " + variancereduction);
        return variancereduction;
    }

    public static void NGpredictionDoot(String[][] grades, String[][] studentInfo, int courseIndex, String parameter) {
        double predictedGrade = -1.0;
        double average = Surunadoot(grades, studentInfo, courseIndex, "nulp");
        double variancereduction = variancecourse(grades, studentInfo, courseIndex, parameter);


        for (int i = 1; i < studentInfo.length; i++) {
            String count1 = studentInfo[i][1];
        }

        if (variancereduction < 0.3) {
            System.out.println("The prediction is considered accurate. Variance reduction equals: " + variancereduction + " the prediction :  " + average);
        } else if (variancereduction >= 0.3) {
            System.out.println("The prediction is considered unaccurate. Variance reduction equals: " + variancereduction + " the prediction :  " + average);

        }
    }

}