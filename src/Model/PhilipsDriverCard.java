package Model;

public class PhilipsDriverCard extends Card{

    String type = "Philips Driver card";
    int numOfCards = 0;
    @Override
    public int returnPoint() {
        return 1;
    }
    @Override
    public int returnNum() {return numOfCards;}
    @Override
    public void setNum(int num) {this.numOfCards += num; }
    @Override
    public String getType() {
        return type;
    }
}
