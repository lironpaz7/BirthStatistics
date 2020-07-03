package part1;

import csv.CSVParser;
import csv.CSVRecord;
import csv.SEFileUtil;

import java.io.File;

/**
 * This class prints statistics of children births.
 */
public class BirthStatistics {

    private final static String MALE = "M";
    private final static String FEMALE = "F";
    public final String pathToDirCSVs;

    public BirthStatistics(String pathCSVs) {
        pathToDirCSVs = pathCSVs;
    }

    /**
     * This method returns the path to the CSV file of the specified year
     *
     * @param year
     * @return
     */
    private String getPathToCSV(int year) {
        File[] csvFiles = new File(pathToDirCSVs).listFiles();
        for (File csvF : csvFiles) {
            if (csvF.getName().contains(Integer.toString(year))) {
                return csvF.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * This method returns the row number in the CSV file of the most popular name by the given gender
     *
     * @param year
     * @param gender
     * @return
     */
    private int getCsvRowOfMostPopularNameByGender(int year, String gender) {
        int rank = -1;
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        for (CSVRecord record : seFileUtil.getCSVParser()) {
            String currGender = record.get(1);
            if (currGender.equals(gender)) {
                rank = (int) record.getRecordNumber();
                break;
            }
        }
        return rank;
    }

    /**
     * This method returns the total births of a chosen year. The data is divided into 3 categories:
     * Total births, female births and boys births.
     *
     * @param year
     */
    private void totalBirths(int year) {
        int femaleGirls = 0, maleBoys = 0;
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        CSVParser parser = seFileUtil.getCSVParser();
        for (CSVRecord rec : parser) {
            String gender = rec.get(1);
            int births = Integer.parseInt(rec.get(2));
            if (gender.equals(FEMALE)) {
                femaleGirls += births;
            } else {
                maleBoys += births;
            }
        }
        System.out.println("total births = " + (int) (femaleGirls + maleBoys));
        System.out.println("female girls = " + femaleGirls);
        System.out.println("male boys = " + maleBoys);
    }

    /**
     * This function returns the rank of a chosen born (given by year, name and gender) if existed.
     * The rank for female is the exact "line" they were placed and for boys the function calculates the
     * relative position of the line among the "male" only.
     *
     * @param year
     * @param nameWanted
     * @param genderWanted
     * @return
     */
    private int getRank(int year, String nameWanted, String genderWanted) {
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        CSVParser parser = seFileUtil.getCSVParser();
        int rank = -1;
        int maleRowDecrease = 0;
        if (genderWanted.equals(MALE)) {
            maleRowDecrease = getCsvRowOfMostPopularNameByGender(year, genderWanted) - 1;
        }
        for (CSVRecord rec : parser) {
            String name = rec.get(0);
            String gender = rec.get(1);
            if (name.equals(nameWanted) && gender.equals(genderWanted)) {
                rank = (int) rec.getRecordNumber() - maleRowDecrease;
                break;
            }
        }
        return rank;
    }

    /**
     * This function returns the name of a child that has a specific rank (in a specific year and gender)
     * if exists. If not exist then the function returns "NO NAME".
     *
     * @param year
     * @param rank
     * @param genderWanted
     * @return
     */
    private String getName(int year, int rank, String genderWanted) {
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        CSVParser parser = seFileUtil.getCSVParser();
        String nameOfChild = "NO NAME";
        int maleRowDecrease = 0;
        if (genderWanted.equals(MALE)) {
            maleRowDecrease = getCsvRowOfMostPopularNameByGender(year, genderWanted) - 1;
        }
        for (CSVRecord rec : parser) {
            String name = rec.get(0);
            String gender = rec.get(1);
            if (((int) rec.getRecordNumber() - maleRowDecrease) == rank && (gender.equals(genderWanted))) {
                nameOfChild = name;
                break;
            }
        }
        return nameOfChild;
    }

    /**
     * This function returns the year that has the highest rank in a given range of years, child name and gender.
     * If there no child that meets the requirements then the function returns -1.
     *
     * @param beginYear
     * @param endYear
     * @param name
     * @param gender
     * @return
     */
    private int yearOfHighestRank(int beginYear, int endYear, String name, String gender) {
        int mostPopularYear = -1;
        boolean foundName = false;
        int highestRank = 0;
        for (int year = beginYear; year <= endYear; year++) {
            int rank = getRank(year, name, gender);
            if (!foundName && rank != -1) {
                highestRank = rank;
                foundName = true;
            } else if (rank != -1 && rank < highestRank) {
                highestRank = rank;
                mostPopularYear = year;
            }
        }
        return mostPopularYear;
    }

    /**
     * This function returns the average rank of a given child name and gender in a given range of years.
     * If the child isn't found in any year then the function returns -1.
     *
     * @param beginYear
     * @param endYear
     * @param name
     * @param gender
     * @return
     */
    private double getAverageRank(int beginYear, int endYear, String name, String gender) {
        double totalRank = 0;
        int totalYears = 0;
        for (int year = beginYear; year <= endYear; year++) {
            int rank = getRank(year, name, gender);
            if (rank != -1) {
                totalRank += rank;
                totalYears++;
            }
        }
        if (totalRank == 0) return -1;
        return (totalRank / totalYears);
    }

    /**
     * This function returns the total sum of births happened before the given child's name, gender and year.
     *
     * @param year
     * @param nameWanted
     * @param genderWanted
     * @return
     */
    private int getTotalBirthsRankedHigher(int year, String nameWanted, String genderWanted) {
        SEFileUtil seFileUtil = new SEFileUtil(getPathToCSV(year));
        CSVParser parser = seFileUtil.getCSVParser();
        int totalBirths = 0;
        for (CSVRecord rec : parser) {
            String name = rec.get(0);
            String gender = rec.get(1);
            int births = Integer.parseInt(rec.get(2));
            if (gender.equals(genderWanted)) {
                if (name.equals(nameWanted)) {
                    return totalBirths;
                }
                totalBirths += births;
            }
        }
        return totalBirths;
    }


    public static void main(String[] args) {
        BirthStatistics birthStatistics = new BirthStatistics(args[0]);
        birthStatistics.totalBirths(2010);
        int rank = birthStatistics.getRank(2010, "Asher", "M");
        System.out.println("Rank is: " + rank);
        String name = birthStatistics.getName(2012, 10, "M");
        System.out.println("Name: " + name);
        System.out.println(birthStatistics.yearOfHighestRank(1880, 2010,"David", "M"));
        System.out.println(birthStatistics.yearOfHighestRank(1880, 2014,"Jennifer", "F"));
        System.out.println(birthStatistics.getAverageRank(1880, 2014, "Benjamin", "M"));
        System.out.println(birthStatistics.getAverageRank(1880,2014, "Lois", "F"));
        System.out.println(birthStatistics.getTotalBirthsRankedHigher(2014, "Draco", "M"));
        System.out.print(birthStatistics.getTotalBirthsRankedHigher(2014, "Sophia", "F"));
        System.out.println();
    }
}
