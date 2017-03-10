/*
 * Institute of Technology, Blanchardstown
 * Computer Vision (Year 4)
 * O-Ring Image Inspection Assignment
 * Connected Component Labelling class
 * Author: Dan Flynn
 */

package main.java.ccl;

import org.opencv.core.Mat;
import java.io.File;

public class CCL {

    //Main image processing controller
    public byte[] processCCL(Mat imgInput) {

        //Build byte array of input image
        byte imgSource[] = new byte[imgInput.rows() * imgInput.cols()];
        imgInput.get(0, 0, imgSource); //Get all pixels
        byte imgResult[] = imgSource.clone(); //Instantiate imgResult array for use later
        
        //Instantiate variables
        int currentLabel = 255; //Color first component white
        int pixel;
        DataQueue queue = new DataQueue();

        //Fill imgResult array with zeros
        for (int i = 0; i < imgResult.length; i++) {
            imgResult[i] = (0);
        }

        //Loop through all pixels
        //TODO: Remove spurious artifacts
        for (int i = 0; i < imgSource.length; i++) {

            if ((imgSource[i] & 0xff) == 255 && imgResult[i] == 0) {

                imgResult[i] = (byte) currentLabel;

                try {
                    //Add current pixel to the queue
                    queue.enQueue(i);

                    //While queue is not empty
                    while (!queue.isEmpty()) {

                        pixel = queue.deQueue();

                        //Get all 8 neighbouring pixels
                        //TODO: Get 4 instead for efficiency?
                        int [] neighbours = {pixel + 1, pixel - 1,
                                             pixel - imgInput.cols(), pixel + imgInput.cols(),
                                             pixel + imgInput.cols() + 1, pixel + imgInput.cols() - 1,
                                             pixel - imgInput.cols() + 1, pixel - imgInput.cols() - 1};

                        //Foreach neighbour pixel
                        for (int neighbour : neighbours) {
                            if ((imgSource[neighbour] & 0xff) == 255 && imgResult[neighbour] == 0) {
                                imgResult[neighbour] = (byte) (currentLabel);
                                queue.enQueue(neighbour);
                            }
                        }
                    }
                    currentLabel -= 85; //Change next component color to grey
                }
                catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        }

        //Save all processed pixels to imgResult
        imgInput.put(0, 0, imgResult);
        return imgResult;
    }

    //Create CCL Image directory path
    public File getCCLImagesPath(String filePath, String directoryName) {
        //Get parent path of current file
        int i = filePath.lastIndexOf(File.separator);
        String parentPath = (i > -1) ? filePath.substring(0, i) : filePath;

        //Return CCL Image directory path
        return new File(parentPath + File.separator + directoryName);
    }

    //Extract "jpg" from "example.jpg"
    public String getFileNameExtension(String fileName) {
        return fileName.substring(fileName.indexOf('.') + 1);
    }

    //Check if a directory exists, otherwise create it
    public boolean checkIfDirectoryExists(File path) {
        //Return true if exists or if the directory was created successfully. Otherwise return false.
        return path.exists() || path.mkdirs();
    }

    //Extract "example" from "/parent/example.jpg"
    public String getBaseFileName(File file) {
        String baseName = file.getName();
        int pos = baseName.lastIndexOf(".");
        if (pos > 0) {
            baseName = baseName.substring(0, pos);
        }
        return baseName;
    }
}