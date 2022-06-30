package Model;

import java.util.ArrayList;

public class Player {

    private String id = "";
    private int[] pos;
    private int point;
    private ArrayList<Card> cardList = new ArrayList<Card>();
    private Card saw;
    private Card hammer;
    private Card driver;
    private Card bridge;

    public Player(Integer id){
        this.id += Integer.toString(id);
        pos = new int[2];
        point = 0;
        saw = new SawCard();
        hammer = new HammerCard();
        driver = new PhilipsDriverCard();
        bridge = new BridgeCard();
        cardList.add(saw);
        cardList.add(hammer);
        cardList.add(driver);
        cardList.add(bridge);
    }


    public int rollDie(){           //주사위 굴려 값 반환
        double randomNum = Math.random();
        int num = (int)(randomNum*6)+1;
        return num;
    }



    public int[] getPos() {
        return pos;
    }
    public void setPos(int[] pos, int initialRow, int initialCol) {
        this.pos[0] = initialRow;
        this.pos[1] = initialCol;
    }
    public void setPos(int newRow, int newCol) {
        this.pos[0] = newRow;
        this.pos[1] = newCol;
    }
    public String getId() {
        return id;
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point += point;
    }
    public ArrayList<Card> getCardList() {
        return cardList;
    }
    public int getCardPoint(int id){
        return cardList.get(id).returnPoint();
    }
    public int getNumOfCard(int id){
        return cardList.get(id).returnNum();
    }
    public String getCardType(int id){
        return cardList.get(id).getType();
    }
    public void setNumOfCards(int id, int num) {
        cardList.get(id).setNum(num);
    }


}
