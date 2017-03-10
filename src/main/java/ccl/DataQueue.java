/*
 * Institute of Technology, Blanchardstown
 * Computer Vision (Year 4)
 * O-Ring Image Inspection Assignment
 * CCL Queue Data Structure
 * Author: Dan Flynn
 */

package main.java.ccl;

class DataQueue {

    //Private Variables
    private int size = 80;
    private int [] queue = new int[size];
    private int front = -1;
    private int rear = -1;

    //Is queue empty?
    boolean isEmpty() {return (front == -1 && rear == -1);}

    //Add to queue
    void enQueue(int value) {
        if (isEmpty()) {
            front++;
            rear++;
            queue[rear] = value;
        }
        else {
            rear = (rear + 1) % size;
            queue[rear] = value;
        }
    }

    //Remove from queue
    int deQueue() {
        int value;
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty, cannot dequeue");
        }
        else if (front == rear) {
            value = queue[front];
            front = -1;
            rear = -1;
        }
        else {
            value = queue[front];
            front = (front + 1) % size;
        }
        return value;
    }
}