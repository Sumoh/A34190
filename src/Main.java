/**
 * Created by Tristan on 2017-04-13.
 */
class Main {

    public static void main(String args[]){

        int numObjects = 50;
        int planeSize = 500;

        if (args.length > 1){
            numObjects = Integer.parseInt(args[1]);
        }

        GUI gui = new GUI(numObjects, planeSize);
        gui.setVisible(true);
    }

}
