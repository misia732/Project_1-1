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
            for (int i = 1; i < 31; i++) {
                System.out.println();
                System.out.println("for course " + i);
                System.out.println("----------------------------------------------");
                // System.out.println(processValuesAverages(studentInfoArray, arrayCurrentGrades, "volta", "5 stars", i));
                // System.out.println(processValuesAverages(studentInfoArray, arrayCurrentGrades, "suruna value", "doot", i));
                // processLalCount(studentInfoArray, arrayCurrentGrades, "high", i);
                // processLalCount(studentInfoArray, arrayCurrentGrades, "low", i);
                System.out.println(bestPrediction(studentInfoArray, arrayCurrentGrades, i, courseAverages(arrayCurrentGrades)));
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

    public static double getCourseAverage(double[] averages, int courseIndex) {
        // Check if the courseIndex is valid
        if (courseIndex >= 1 && courseIndex <= averages.length) {
            double average = averages[courseIndex - 1];
            if (average == -1) {
                System.out.println("The average for course " + courseIndex + " cannot be calculated due to excessive 'NG' grades.");
                return -1; // You can choose a different sentinel value if needed
            } else {
                return average;
            }
        } else {
            System.out.println("Invalid course index: " + courseIndex);
            return -1; // You can choose a different sentinel value if needed
        }
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

    public static double processLalCount(String[][] studentInfo, String[][] grades, int parameter, int courseIndex) {

        // Initializing variables
        double avgScore = 0;
        double avgScoreCount = 0;
        double lalCount = 0;
        double whichProperty = 0;

        if (parameter >= 59 && parameter <= 69) {
            whichProperty = 1;
        } else if (parameter >= 70 && parameter <= 79) {
            whichProperty = 2;
        } else if (parameter >= 80 && parameter <= 89) {
            whichProperty = 3;
        } else {
            whichProperty = 4;
        }

        // Looping over each students Lal count to categorize them
        for (int i = 1; i < studentInfo.length && studentInfo[i][0] != null; i++) {
            lalCount = Double.parseDouble(studentInfo[i][3]);

            // Locate students score for a course
            for (int j = 1; j < grades.length && grades[j][0] != null; j++) {
                if (studentInfo[i][0].equals(grades[j][0])) {
                    // Check for ng
                    if (!grades[j][courseIndex].equals("NG")) {
                        double score = Double.parseDouble(grades[j][courseIndex]);

                        if (whichProperty == 1 && lalCount >= 59 && lalCount <= 69) {
                            avgScore += score;
                            avgScoreCount++;
                        } else if (whichProperty == 2 && lalCount >= 70 && lalCount <= 79) {
                            avgScore += score;
                            avgScoreCount++;
                        } else if (whichProperty == 3 && lalCount >= 80 && lalCount <= 89) {
                            avgScore += score;
                            avgScoreCount++;
                        } else if (whichProperty == 4 && lalCount >= 90 && lalCount <= 100) {
                            avgScore += score;
                            avgScoreCount++;
                        }
                    }
                }
            }
        }
        //calculating final averages checks also if not dividing by 0
        double avgScoreLalCount = avgScoreCount != 0 ? avgScore / avgScoreCount : 0;
        return avgScoreLalCount;


    }

    public static String bestPrediction(String[][] studentInfo, String[][] grades,int courseIndex, double[] averages) {

        double courseAverage = getCourseAverage(averages, courseIndex) ;

        String [] SurunaValue = {"doot","lobi","nulp"};
        double avarageSurunaValue = 0;
        String [] HurniValue = {"full", "high" , "medium" , "low", "nothing"};
        double  avarageHurniValue = 0;
        String [] VoltaValue = {"1 star","2 stars","3 stars","4 stars","5 stars"};
        double avarageVoltaValue = 0;
        double averageLalCount = 0;


        double SurunaValueSum=0;
        double HurniValueSum=0;
        double VoltaValueSum=0;
        double lalCountSum = 0;

        double varianceSuruna= 0;
        double varianceHurni= 0;
        double varianceVolta= 0;

        double[] variances = new double[4];

        // SURUNA VALUE

        for (int j = 0; j <SurunaValue.length ; j++  ){

            avarageSurunaValue = processValuesAverages(studentInfo, grades, "Suruna Value", SurunaValue[j] ,courseIndex);

            double varianceReduction = Math.abs(courseAverage - avarageSurunaValue);

            SurunaValueSum += varianceReduction;
        }
        varianceSuruna = SurunaValueSum / SurunaValue.length;
        variances[0] = varianceSuruna;

        System.out.println("The variance of SurunaValue: " + varianceSuruna);

        // HURNI LEVEL


        for (int j = 0; j < HurniValue.length; j++){

            avarageHurniValue = processValuesAverages(studentInfo, grades, "Hurni Level", HurniValue[j], courseIndex);

            double varianceReduction = Math.abs(courseAverage - avarageHurniValue);

            HurniValueSum += varianceReduction;
        }
        varianceHurni = HurniValueSum / HurniValue.length;
        variances[1] = varianceHurni;

        System.out.println("The variance of HurniValue: " + varianceHurni);

        // VOLTA

        for (int j = 0; j < VoltaValue.length ; j++){

            avarageVoltaValue = processValuesAverages(studentInfo, grades , "Volta", VoltaValue[j], courseIndex);

            double varianceReduction = Math.abs(courseAverage - avarageVoltaValue);

            VoltaValueSum += varianceReduction;
        }
        varianceVolta = VoltaValueSum / VoltaValue.length;
        variances[2] = varianceVolta;

        System.out.println("The variance of VoltaValue: "+ varianceVolta);

        // LAL COUNT

        for (int j = 59; j < 101 ; j += 11){

            averageLalCount = processLalCount(studentInfo, grades, j, courseIndex);

            double varianceReduction = Math.abs(courseAverage - averageLalCount);

            lalCountSum += varianceReduction;
        }
        double varianceLal = lalCountSum / 4;
        variances[3] = varianceLal;
        System.out.println("The variance of lalCount: " + varianceLal);

        double maxVariance = variances[0]; // Initialize maxVariance with the first element of the array

        // Iterate through the array to find the maximum value
        for (int i = 1; i < variances.length; i++) {
            if (variances[i] > maxVariance) {
                maxVariance = variances[i];
            }
        }

        if (maxVariance == varianceSuruna) {
            return "The best value to predict with is: Suruna Value";
        } else if (maxVariance == varianceHurni) {
            return "The best value to predict with is: Hurni Value";
        } else if (maxVariance == varianceVolta) {
            return "The best value to predict with is: Volta Value";
        } else {
            return "The best value to predict with is: lal Count";
        }
    }


}




