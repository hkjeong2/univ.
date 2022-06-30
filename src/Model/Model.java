package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

public class Model extends Observable {

    private Map map;
    private ArrayList<Player> playerList = new ArrayList<Player>();
    private ArrayList<Player> arrivedPlayerList = new ArrayList<Player>();


    public void notifyChange(){
        setChanged();
        notifyObservers();
    }

    public void loadMap(){
        map = new Map();
        map.loadMap();
    }

    public void loadMap(String fileName){
        map = new Map();
        map.loadMap(fileName);
    }

    public void initPlayer(Integer id){             //player 객체 생성 및 위치 초기화
        Player player = new Player(id);
        player.setPos(player.getPos(), map.getInitialRow(), map.getInitialCol());
        playerList.add(player);         //생성한 player들을 하나의 list에 추가
    }

    public void updateMapWithPlayer(){          //플레이어의 위치 변화에 따른 map update
        map.setUpMapWithPlayers(playerList);
        map.updateMapWithPlayer(playerList);
        notifyChange();
    }

    public int getMoveNum(int id, int dieNum){              //주사위 값 계산
        int value = dieNum - playerList.get(id).getNumOfCard(3);
        if (value < 0)
            return 0;
        return value;
    }

    public boolean hasBridgeCard(int id){                   //bridge card 소유 여부
        if(playerList.get(id).getNumOfCard(3)>=1){
            return true;
        }
        return false;
    }

    public boolean movePlayer(String moves, int i){         //주사위 값에 따른 player 이동
        if (map.movePlayer(moves, playerList.get(i).getPos(), playerList.get(i))){     //해당 플레이어가 입력한 moves 값으로 이동 가능할 시
            playerList.get(i).setPos(map.getNewRow(),map.getNewCol());      //위치 변경
            return true;
        }
        return false;
    }

    public void updatePlayerList(int playerID){
        if(playerID > 0 && playerID < playerList.size()){
            for(int i = 0; i<playerID; i++){
                playerList.add(playerList.get(i));        //끝난 플레이어의 이전 순번 플레이어들을 리스트의 맨 뒷 순서로 배치
            }
            for(int i = 0; i<playerID; i++){
                playerList.remove(0);               //뒷 순서에 추가된 플레이어들이 리스트의 앞부분에 남아있으므로 해당 내용 삭제
            }
        }
        playerList.remove(0);
    }

    public void sortRanking(){
        int cardPoint = 0;
        for (int i = 0; i < arrivedPlayerList.size(); i++){         //각 플레이어의 최종 카드 점수 정산
            for (int j = 0; j < arrivedPlayerList.get(i).getCardList().size() - 1; j++){
                cardPoint += arrivedPlayerList.get(i).getCardPoint(j) * arrivedPlayerList.get(i).getNumOfCard(j);
            }
            arrivedPlayerList.get(i).setPoint(cardPoint);
            cardPoint = 0;
        }
                                                                    // 1등 7점    2등 3점     3등 1점
        for (int i = 0; i < arrivedPlayerList.size() - 1; i++){         //각 플레이어의 최종 등수 점수 정산 (마지막 플레이어는 못 들어왔으므로 size - 1)
            if (i == 0){
                arrivedPlayerList.get(i).setPoint(7);
            }
            else if(i == 1){
                arrivedPlayerList.get(i).setPoint(3);
            }
            else if(i == 2){
                arrivedPlayerList.get(i).setPoint(1);
            }
        }

        Collections.sort(arrivedPlayerList, new PlayerPointComparator().reversed());        //포인트 순으로 내림차순 정렬
    }

    public boolean hasPlayerEnded(){
        return map.isHasPlayerEnded();
    }
    public int getDieNum(int id){
        return playerList.get(id).rollDie();
    }
    public int getCol() {
        return map.getCol();
    }
    public int getRow() {
        return map.getRow();
    }
    public String[][] getMap() {
        return map.getMap();
    }
    public String[][] getMapWithPlayers() {
        return map.getMapWithPlayers();
    }
    public ArrayList<Player> getPlayerList() {
        return playerList;
    }
    public ArrayList<Player> getArrivedPlayerList() {
        return arrivedPlayerList;
    }


}
