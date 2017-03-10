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

        //Build two byte arrays from input image
        byte imgData[] = new byte[imgInput.rows() * imgInput.cols()];
        byte label[]   = new byte[imgInput.rows() * imgInput.cols()];
        imgInput.get(0, 0, imgData); //Get all pixels
        
        //Instantiate variables
        int currentLabel = 1; //Label 1 is the ring
        int pixel;
        DataQueue queue = new DataQueue();

        //Loop through all pixels
        for (int i = 0; i < imgData.length; i++) {

            if ((imgData[i] & 0xff) == 255 && label[i] == 0) {

                label[i] = (byte) (currentLabel);

                try {
                    //Add current pixel to the queue
                    queue.enQueue(i);

                    //While queue is not empty
                    while (!queue.isEmpty()) {

                        pixel = queue.deQueue();

                        //Get all 8 neighbouring pixels
                        int [] neighbours = {pixel + 1, pixel - 1,
                                             pixel - imgInput.cols(), pixel + imgInput.cols(),
                                             pixel + imgInput.cols() + 1, pixel + imgInput.cols() - 1,
                                             pixel - imgInput.cols() + 1, pixel - imgInput.cols() - 1};

                        //Foreach neighbour pixel
                        for (int neighbour : neighbours) {
                            if ((imgData[neighbour] & 0xff) == 255 && label[neighbour] == 0) {
                                label[neighbour] = (byte) (currentLabel);
                                queue.enQueue(neighbour);
                            }
                        }
                    }
                    currentLabel += 1; //Next component is not part of the ring
                }
                catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        }

        //Remove imperfections using CCL data
        for (int i=0; i<imgData.length; i++) {
            if ((label[i] & 0xff) > 1) {
                imgData[i] = 0; //Set pixel to black
            }
        }

        //Save all processed pixels to label
        imgInput.put(0, 0, imgData);
        return imgData;
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