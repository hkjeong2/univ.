package Model;

public class BridgeCard extends Card{

    String type = "Bridge card";
    int numOfCards = 0;
    @Override
    public int returnPoint() {
        return 0;
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
