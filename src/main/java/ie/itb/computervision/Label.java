/*
 * Institute of Technology, Blanchardstown
 * Computer Vision (Year 4)
 * O-Ring Image Inspection Assignment
 * Label Object
 * Author: Dan Flynn
 */

package main.java.ie.itb.computervision;

public class Label {

    public int name;
    public Label Root;
    public int Rank;

    public Label(int Name) {
        this.name = Name;
        this.Root = this;
        this.Rank = 0;
    }

    public int getName()            {return name;}
	public void setName(int name)   {this.name = name;}

	public Label getRoot()          {return Root;}
	public void setRoot(Label root) {Root = root;}

	public int getRank()            {return Rank;}
	public void setRank(int rank)   {Rank = rank;}

	Label GetRoot() {
        if (this.Root != this) {
            this.Root = this.Root.GetRoot();
        }
        return this.Root;
    }

    void Join(Label root2) {

        //Is the rank of root2 less than that of root1?
        if (root2.Rank < this.Rank) {

            //Yes! Then root1 is the parent of root2 (since it has the higher rank)
            root2.Root = this;
        }
        //Otherwise, rank of root2 is greater than or equal to that of root1
        else {
            this.Root = root2; //Make root2 the parent

            //Are both ranks equal?
            if (this.Rank == root2.Rank) {

                //Increment root2, we need to reach a single root for the whole tree
                root2.Rank++;
            }
        }
    }
}
