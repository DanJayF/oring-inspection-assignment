/*
 * Institute of Technology, Blanchardstown
 * Computer Vision (Year 4)
 * O-Ring Image Inspection Assignment
 * Connected Component Labelling class
 * Author: Dan Flynn
 */

package main.java.ie.itb.computervision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CCL {

    private int[][] _board;
    private BufferedImage _input;
    private Graphics inputGD;
    private int _width;
    private int _height;
    private int backgroundColor;

    //Main image processing controller
    Map<Integer, BufferedImage> Process(BufferedImage input) {

        //Instantiate variables
        backgroundColor = 0xFF000000; //Black (Hardcoded)
        _input = input;               //BufferedImage
        _width = input.getWidth();    //int
        _height = input.getHeight();  //int
        _board = new int[_width][];   //2D int array
        for(int i = 0;i < _width;i++)
        	_board[i] = new int[_height];

        //Retrieve patterns in image
        Map<Integer, List<Pixel>> patterns = FindPatterns();

        //Create image HashMap of patterns
        Map<Integer, BufferedImage> images = new HashMap<>();
        inputGD = _input.getGraphics();
        inputGD.setColor(Color.BLUE); //TODO: Change this to black or white?
        for(Integer id : patterns.keySet()) {
            BufferedImage bmp = CreateBitmap(patterns.get(id));
            images.put(id, bmp);
        }
        inputGD.dispose();

        //Return the images
        return images;
    }

    //Check if pixel color is equal to backgroundColor (black).
    private boolean CheckIsBackGround(Pixel currentPixel) {
    	return currentPixel.color == backgroundColor;
    }

    //TODO: Investigate this method and rename it!
    private static int Min(List<Integer> neighboringLabels, Map<Integer, Label> allLabels) {
    	int ret = allLabels.get(neighboringLabels.get(0)).GetRoot().name;
    	for(Integer n : neighboringLabels) {
    		int curVal = allLabels.get(n).GetRoot().name;
    		ret = (ret < curVal ? ret : curVal);
    	}
    	return ret;
    }

    //Get minimum x or y value of current pattern
    private static int Min(List<Pixel> pattern, boolean xOrY) {
    	int ret = (xOrY ? pattern.get(0).x : pattern.get(0).y);
    	for(Pixel p : pattern) {
    		int curVal = (xOrY ? p.x : p.y);
    		ret = (ret < curVal ? ret : curVal);
    	}
    	return ret;
    }

    //Get maximum x or y value of current pattern
    private static int Max(List<Pixel> pattern, boolean xOrY) {
    	int ret = (xOrY ? pattern.get(0).x : pattern.get(0).y);
    	for(Pixel p : pattern) {
    		int curVal = (xOrY ? p.x : p.y);
    		ret = (ret > curVal ? ret : curVal);
    	}
    	return ret;
    }

    //Searches and assigns labels to all pixels, discovers patterns in image
    private Map<Integer, List<Pixel>> FindPatterns() {

        int labelCount = 1;
        Map<Integer, Label> allLabels = new HashMap<>();

        for (int i = 0; i < _height; i++) {
            for (int j = 0; j < _width; j++) {

                //Get current pixel object
                Pixel currentPixel = new Pixel(j, i, _input.getRGB(j, i));

                //Check is background color black
                if (CheckIsBackGround(currentPixel)) {continue;}

                //Get the labels of neighbouring pixels
                List<Integer> neighboringLabels = GetNeighboringLabels(currentPixel);
                int currentLabel;

                //If there ARE NO neighbouring labels
                if (neighboringLabels.isEmpty()) {
                    currentLabel = labelCount;
                    allLabels.put(currentLabel, new Label(currentLabel));
                    labelCount++;
                }
                //If there ARE neighbouring labels
                else {
                    currentLabel = Min(neighboringLabels, allLabels);
                    Label root = allLabels.get(currentLabel).GetRoot();

                    for (Integer neighbor : neighboringLabels) {
                        if (root.name != allLabels.get(neighbor).GetRoot().name) {
                            allLabels.get(neighbor).Join(allLabels.get(currentLabel));
                        }
                    }
                }
                //Add the current label to the image board
                _board[j][i] = currentLabel;
            }
        }

        //Return all image patterns
        return AggregatePatterns(allLabels);
    }

    //Get the labels of current pixel neighbours
    private List<Integer> GetNeighboringLabels(Pixel pix) {

        //ArrayList to hold labels
        List<Integer> neighboringLabels = new ArrayList<>();

        //Add neighbour labels to ArrayList
        for(int i = pix.y - 1; i <= pix.y + 2 && i < _height - 1; i++) {
            for(int j = pix.x - 1; j <= pix.x + 2 && j < _width - 1; j++) {
                if(i > -1 && j > -1 && _board[j][i] != 0) {
                    neighboringLabels.add(_board[j][i]);
                }
            }
        }

        return neighboringLabels;
    }

    //Aggregate all patterns
    private Map<Integer, List<Pixel>> AggregatePatterns(Map<Integer, Label> allLabels) {

        Map<Integer, List<Pixel>> patterns = new HashMap<>();

        for (int i = 0; i < _height; i++) {
            for (int j = 0; j < _width; j++) {

                int patternNumber = _board[j][i];

                if (patternNumber != 0) {
                    patternNumber = allLabels.get(patternNumber).GetRoot().name;

                    if (!patterns.containsKey(patternNumber)) {
                        patterns.put(patternNumber, new ArrayList<>());
                    }
                    patterns.get(patternNumber).add(new Pixel(j, i, _input.getRGB(j, i)));
                }
            }
        }

        return patterns;
    }

    //Create a bitmap image of the current pattern
    private BufferedImage CreateBitmap(List<Pixel> pattern) {

        int minX = Min(pattern, true);
        int maxX = Max(pattern, true);
        int minY = Min(pattern, false);
        int maxY = Max(pattern, false);
        int width = maxX + 1 - minX;
        int height = maxY + 1 - minY;

        BufferedImage bmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (Pixel pix : pattern) {
            bmp.setRGB(pix.x - minX, pix.y - minY, pix.color); //Shift position according to minX & minY
        }
        inputGD.drawRect(minX, minY, maxX-minX, maxY-minY);

        return bmp;
    }

    //Create CCL Image directory path
    File getCCLImagesPath(String filePath, String directoryName) {
        //Get parent path of current file
        int i = filePath.lastIndexOf(File.separator);
        String parentPath = (i > -1) ? filePath.substring(0, i) : filePath;

        //Return CCL Image directory path
        return new File(parentPath + File.separator + directoryName);
    }

    //Extract "jpg" from "example.jpg"
    String getFileNameExtension(String fileName) {
        return fileName.substring(fileName.indexOf('.') + 1);
    }

    //Check if a directory exists, otherwise create it
    boolean checkIfDirectoryExists(File path) {
        //Return true if exists or if the directory was created successfully. Otherwise return false.
        return path.exists() || path.mkdirs();
    }

    //Extract "example" from "/parent/example.jpg"
    String getBaseFileName(File file) {
        String baseName = file.getName();
        int pos = baseName.lastIndexOf(".");
        if (pos > 0) {
            baseName = baseName.substring(0, pos);
        }
        return baseName;
    }

    //Return _input
    BufferedImage getProcessedImage() {
    	return _input;
    }
}