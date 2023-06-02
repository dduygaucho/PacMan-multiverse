package src;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AccessibleLevelChecker implements ILevelChecking {
    private String filePath;
    private String logFilePath = "ErrorLog.txt";
    private FileWriter fileWriter = null;

    public AccessibleLevelChecker(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean check(String maze) {
        ArrayList<Integer> list = new ArrayList<>();
        String[] lines = maze.split("\n");
        int row = 0;
        boolean checkLevel = true;

//        contains list of unaccessible pills and gold
        HashMap<String, ArrayList<Point>> pillGoldUnaccessible = new HashMap<>();

//     Iterate over rows
        for (String line : lines) {
//     Iterate over columns
//            This function looks for bounding rectangle 'x":
//            xxxxx
//            x..xx
//            xxxxx
//            In this case pills are unaccessible

            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                if (c != 'x') {
                    continue;
                }
                if (row == 0 && col == 0){
                    continue;
                }
//                System.out.println("Coordinates are: "+ row + " " + col);
//                --->
                for (int rectangleWidth = 3; rectangleWidth < line.length() - 3; rectangleWidth++) {
//                    System.out.println("Rectanglewidth of "+ rectangleWidth);
//                    [--->]T
                    for (int rectangleHeight = 3; rectangleHeight < lines.length - 3; rectangleHeight++) {
//                        System.out.println("RectangleHeight of "+ rectangleHeight);
                        if (row + rectangleHeight >= lines.length || col + rectangleWidth >= line.length()){
                            continue;
                        }

//                        return a list of unaccessible items inside the current rectangle
//                        with left-up point and 2 dimensions: width and height
                        HashMap<String, ArrayList<Point>> unaccessibleItems = isRectangle(lines, row, col,
                                rectangleWidth, rectangleHeight);
                        boolean isolated = unaccessibleItems.size() >= 1;

//                        bounding rectangle found
                        if (isolated){
//
                            for (String string: unaccessibleItems.keySet()){
                                if (!pillGoldUnaccessible.containsKey(string)){
                                    pillGoldUnaccessible.put(string, new ArrayList<>());
                                }
                                for (Point point: unaccessibleItems.get(string)){
                                    if (!pillGoldUnaccessible.get(string).contains(point)){
                                        pillGoldUnaccessible.get(string).add(point);
                                    }
                                }
                            }
                            checkLevel = false;
                        }
                    }
                }
            }
            row ++;
        }

        if (pillGoldUnaccessible.size() == 0){
            return checkLevel;
        }

//        input to errorlog.txt
        for(String string: pillGoldUnaccessible.keySet()){
            try {
                fileWriter = new FileWriter(logFilePath, true);
                fileWriter.write("Level "+ this.filePath+ " - "+
                        string + " not accessible: "+ this.convertToString(pillGoldUnaccessible.get(string)));
                fileWriter.write("\n");

                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return checkLevel;


    }

    public HashMap<String, ArrayList<Point>> isRectangle(String[] lines, int x, int y, int width, int height){
        int max_row = x + width;
        int max_col = y + height;
        ArrayList<Character> list = new ArrayList<>();
        boolean signal = true;
        boolean containPills = false;

        HashMap<String, ArrayList<Point>> pillGoldUnaccessible = new HashMap<>();
//      pill index is 0, gold is 1

        for (int row_ind = x; row_ind < x + height; row_ind ++){
            for (int col_ind = y; col_ind < y + width; col_ind ++){

//              sketch
//                if row_ind = x; append everything, all the symbols of the line
//                if row_ind > x and row_ind < x + height -1; just append the first and end elements
//                   in here check if any character either equals gold or pills, then change containpills = true
//                this case [row_ind][y; y+ width - 1]
//                else if row_ind = x + height - 1; append everything

                if (row_ind == x){
                    if(lines[row_ind].charAt(col_ind) != 'x'){
                        signal = false;
                    }
                    list.add(lines[row_ind].charAt(col_ind));
                }

                else if (row_ind > x && row_ind < x + height - 1){

                    if(col_ind == y || col_ind == y+width - 1){
                        if(lines[row_ind].charAt(col_ind) != 'x'){
                            signal = false;
//                        return false;
                        }
                        list.add(lines[row_ind].charAt(col_ind));
                    }

                    else{
//                        found items inside the rectangle
                        if(lines[row_ind].charAt(col_ind) == '.' || lines[row_ind].charAt(col_ind) == 'g'  ){
//                            found pills
                            if(lines[row_ind].charAt(col_ind) == '.'){
                                if(!pillGoldUnaccessible.containsKey("Pill")){
                                    pillGoldUnaccessible.put("Pill", new ArrayList<>());
                                    pillGoldUnaccessible.get("Pill").add(new Point(row_ind + 1, col_ind + 1));
                                }
                                else{
                                    pillGoldUnaccessible.get("Pill").add(new Point(row_ind + 1, col_ind + 1));
                                }
                            }

//                            found gold
                            else if(lines[row_ind].charAt(col_ind) == 'g'){
                                if(!pillGoldUnaccessible.containsKey("Gold")){
                                    pillGoldUnaccessible.put("Gold", new ArrayList<>());
                                    pillGoldUnaccessible.get("Gold").add(new Point(row_ind + 1, col_ind+ 1));

                                }
                                else{
                                    pillGoldUnaccessible.get("Gold").add(new Point(row_ind+ 1, col_ind+ 1));
                                }
                            }
                            containPills = true;
                        }
                    }
                }

                else if (row_ind == x + height - 1){
                    if(lines[row_ind].charAt(col_ind) != 'x'){
                        signal = false;
                    }
                    list.add(lines[row_ind].charAt(col_ind));
                }
            }
        }

//        if signal and containPills:  return true; else return false
        if (signal && containPills){
            return pillGoldUnaccessible;
        }

        else{
            return new HashMap<String, ArrayList<Point>>();
        }
    }

    public String convertToString(ArrayList<Point> list){
        String sentence = "";
        for (Point ele: list){
            sentence += "(";
            sentence += (int)ele.getY();
            sentence += ",";
            sentence += (int)ele.getX();
            sentence += ")";
            sentence += "; ";
        }
        return sentence.substring(0, sentence.length() - 2);
    }

}
