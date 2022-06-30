package Model;

public class SawCard extends Card{

    String type = "Saw card";
    int numOfCards = 0;
    @Override
    public int returnPoint() {return 3;}
    @Override
    public int returnNum() {return numOfCards;}
    @Override
    public void setNum(int num) {this.numOfCards += num; }
    @Override
    public String getType() {
        return type;
    }
}
