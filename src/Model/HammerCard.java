package Model;

public class HammerCard extends Card{

    String type = "Hammer card";
    int numOfCards = 0;
    @Override
    public int returnPoint() {return 2;}
    @Override
    public int returnNum() {return numOfCards;}
    @Override
    public void setNum(int num) {this.numOfCards += num; }
    @Override
    public String getType() {
        return type;
    }
}
