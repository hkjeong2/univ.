package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Math.abs;

public class Map {

    private RandomAccessFile raf;
    private int col = 0;
    private int row = 0;
    private int newCol = 0;                 //이후 플레이어 위치 찾을 시 사용
    private int newRow = 0;                 //이후 플레이어 위치 찾을 시 사용
    private int endCol = 0;
    private int endRow = 0;
    private int numOfR = 0;
    private int minCount = 0;
    private int maxCount = 0;
    private int y_change = 0;
    private int startRow;
    private int startCol;
    private int initialRow;                 //"START"의 "S"와 "Saw"의 "S" 구분에 사용
    private int initialCol;                 //"START"의 "S"와 "Saw"의 "S" 구분에 사용
    private int countBridgeCard;
    private boolean hasPlayerEnded;
    private boolean isBackMoveAllowed = true;
    private boolean[][] isPieceExist;
    private String[][] map;                 //고유 맵 정보가 담긴 map (불변)
    private String[][] mapWithPlayers;      //플레이어의 위치에 따라 변하는 map 정보를 나타내기 위한 map (고유 map의 정보 이용)
    private String[][] noBackMap;           //map with condition : no back move allowed
    private String fileStored = "lastOpenedMap.txt";
    private String defaultMap = "map1.txt";
    private File file = new File(fileStored);


    public void recordMapInFile(String fileName){       //지난 파일 맵 로드 기능을 위해 별도 파일에 열었던 맵 파일 기록
        try {
            if(!file.exists())
                file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileName);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadMap(){
        try {
            if(!file.exists()){
                loadMap(defaultMap);        //파일이 열린 적 없으면 default map
            }
            else{
                BufferedReader reader = new BufferedReader(new FileReader(fileStored));
                String line = reader.readLine();
                loadMap(line);              //열렸던 기록이 있으면 해당 파일 map load
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadMap(String fileName) {
        try {
            raf = new RandomAccessFile(fileName, "r");
            calculateNxN();         //파일 내 정보 파싱하여 필요한 row 와 col 계산
            createNxNMap();         //계산한 row 와 col으로 2차원 배열 생성
            setMap();               //파일 정보 읽어들여 2차원 배열에 저장
            recordMapInFile(fileName);  //열었던 맵 파일 별도 파일에 저장
        } catch (FileNotFoundException e) {
            loadMap(defaultMap);        //존재하지 않는 파일일 시 default map load
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void calculateNxN() throws IOException {
        while (true) {
            String line = raf.readLine();
            if (line == null) break;

            line = line.replace(" ", "");
            char[] chars = line.toCharArray();

            if (Character.compare(chars[chars.length - 1], 'R') == 0) {
                numOfR += 1;
            } else if (Character.compare(chars[chars.length - 1], 'D') == 0) {
                y_change += 1;
                if (y_change > maxCount)
                    maxCount = y_change;
            } else if (Character.compare(chars[chars.length - 1], 'U') == 0) {
                y_change -= 1;
                if (y_change < minCount)
                    minCount = y_change;
            }
        }
        col += numOfR + 1;
        row += abs(minCount) + maxCount + 1;
    }

    public void createNxNMap(){
        map = new String[row][col];
        noBackMap = new String[row][col];
        isPieceExist = new boolean[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = "x";
                noBackMap[i][j] = "x";
                isPieceExist[i][j] = false;         //추후 맵에 piece 존재 여부 저장하기 위한 2차원 배열
            }
        }
    }

    public void setMap() throws IOException {
        startRow = abs(minCount);
        startCol = 0;
        initialRow = startRow;
        initialCol = startCol;
        raf.seek(0);

        while (true) {
            String line = raf.readLine();
            if (line == null) break;

            line = line.replace(" ", "");
            char[] chars = line.toCharArray();

            switch (chars[0]) {
                case 'S':
                    if (startRow == initialRow && startCol == 0)
                        map[startRow][startCol] = "START";
                    else
                        map[startRow][startCol] = "S";
                    break;
                case 'E':
                    map[startRow][startCol] = "END";
                    endRow = startRow;
                    endCol = startCol;
                    break;
                case 'C':
                    map[startRow][startCol] = "C";
                    break;
                case 'B':
                    map[startRow][startCol] = "B";
                    map[startRow][startCol+1] = "=";
                    noBackMap[startRow][startCol] = "B";
                    break;
                case 'b':
                    map[startRow][startCol] = "b";
                    break;
                case 'H':
                    map[startRow][startCol] = "H";
                    break;
                case 'P':
                    map[startRow][startCol] = "P";
                    break;
                default:
                    break;
            }
            switch (chars[chars.length - 1]) {
                case 'U':
                    if(noBackMap[startRow][startCol]!="B"){
                        noBackMap[startRow][startCol] = "U";
                    }
                    else{
                        noBackMap[startRow][startCol] = "UB";
                    }
                    startRow -= 1;
                    break;
                case 'D':
                    if(noBackMap[startRow][startCol]!="B"){
                        noBackMap[startRow][startCol] = "D";
                    }
                    else{
                        noBackMap[startRow][startCol] = "DB";
                    }
                    startRow += 1;
                    break;
                case 'R':
                    noBackMap[startRow][startCol] = "R";
                    startCol += 1;
                    break;
                case 'L':
                    noBackMap[startRow][startCol] = "L";
                    startCol -= 1;
                    break;
                case 'E':
                    noBackMap[startRow][startCol] = "E";
                    break;
                default:
                    break;
            }
        }
        raf.close();
    }

    public void setUpMapWithPlayers(ArrayList<Player> pL){          //플레이어 위치에 따른 map 상태를 mapWithPlayers 에 따로 저장
        mapWithPlayers = new String[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(!isPieceExist[i][j]){                //해당 위치에 말이 존재하지 않을 시
                    mapWithPlayers[i][j] = map[i][j];   //기존 맵 정보만 저장
                }
                else{                                      //플레이어의 말이 위치할 시
                    for(int k = 0; k < pL.size(); k++){
                        if(i == pL.get(k).getPos()[0] && j == pL.get(k).getPos()[1]){
                            mapWithPlayers[i][j] = "";      //해당 위치에서 기존 고유 map의 정보는 지우고 플레이어만 표시하기 위함
                            break;
                        }
                        else{
                            if (k == pL.size()-1){
                                mapWithPlayers[i][j] = map[i][j]; //말이 존재한다고 표시 되어 있지만 실제 위치한 플레이어가 없을 시 기존 고유 map의 정보 복구
                                isPieceExist[i][j] = false;
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateMapWithPlayer(ArrayList<Player> pL){
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                for(int k = 0; k < pL.size(); k++){
                    if(i == pL.get(k).getPos()[0] && j == pL.get(k).getPos()[1]) {   //해당 위치에 플레이어가 위치할 시
                        if(isPieceExist[i][j]){
                            mapWithPlayers[i][j] += pL.get(k).getId() + ""; //플레이어 아이디 기록하여 지도에 표시하기 위함
                        }
                        else if (!isPieceExist[i][j]) {
                            {                                   //첫 piece일 시, 이전에 기록된 정보 삭제 후 플레이어 ID 기록
                                mapWithPlayers[i][j] = "";
                                mapWithPlayers[i][j] += pL.get(k).getId() + "";
                                isPieceExist[i][j] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean movePlayer(String moves, int[] pos, Player player){         //받아온 moves의 경로로 플레이어가 이동 가능한 지 확인 및 이동 실행
        hasPlayerEnded = false;                                                //경로로 유효한 이동이 있을 시에만
        newRow = pos[0];                                                       //변경된 플레이어 위치 따라 카드 보유 현황 변경
        newCol = pos[1];
        countBridgeCard = 0;

        moves = moves.toUpperCase(Locale.ROOT);             //uuddr --> UUDDR

        for(int i = 0; i < moves.length(); i++){

            if(checkBackMoves(i, moves) == false){          //뒤로가기 불가능한 경우에 뒤로가기 시행할 경우
                return false;                               //오류
            }

            if (checkChangingPosition(moves.charAt(i)) == false){     //해당 move로 position 변경이 불가능한 경우
                return false;                                         //오류
            }

            if(newRow == endRow && newCol == endCol){           //END 도착 시
                player.setNumOfCards(3, countBridgeCard);
                hasPlayerEnded = true;
                isBackMoveAllowed = false;                      //moving backwards is not allowed anymore
                return true;
            }
            else if(newRow>=this.row || newRow<0 || newCol>=this.col || newCol<0) {           //맵 바깥으로 나갈 시
                return false;                                                                 //오류
            }
            else if(map[newRow][newCol] == "x") {            //유효하지 않은 공간으로 이동 시
                return false;                                //오류
            }
            checkCrossBridge(moves.charAt(i), player);      //bridge를 건널 시 이후 유효 move일 때 적용시킬 bridge 카드 개수++
        }
                                                            //END 도착 이외의 유효 move
        player.setNumOfCards(3, countBridgeCard);        //증가시켜놓은 bridge 카드 개수 적용
        updateCardStatus(map[newRow][newCol], player);      //moves의 최종 지점에 도달했을 때 위치에 따라 카드 개수 변경
        return true;
    }

    public boolean checkChangingPosition(char c){           //move에 따른 포지션 change
        switch(c){
            case 'U':
                newRow -= 1;
                break;
            case 'D':
                newRow += 1;
                break;
            case 'R':
                newCol += 1;
                break;
            case 'L':
                newCol -= 1;
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean checkBackMoves(int i, String moves){
        if(isBackMoveAllowed == false){                 //더 이상 뒤로 가는 것이 불가능한 상태일 경우
                                                        //noBackMap cell의 정보와 같아야만 이동 가능
            if(!((noBackMap[newRow][newCol]).charAt(0) == (moves.charAt(i)))){              //noBackMap에 저장해둔 경로와 일치하지 않을 시 == back move 시도할 시
                if(!(noBackMap[newRow][newCol].length() == 2 && moves.charAt(i) == 'R')){   //Bridge일 때 R이면 허용이지만 반대일 시 false
                    return false;
                }
            }
        }
        return true;
    }

    public void checkCrossBridge(char move, Player player){
        if((move == 'R') && map[newRow][newCol] == "=") {    //"B" cell을 통해 건넜을 때
            newCol+=1;
            countBridgeCard+=1;
        }
        else if((move == 'L') && map[newRow][newCol] == "=") {   //"b" cell을 통해 건넜을 때
            newCol-=1;
            countBridgeCard+=1;
        }
    }

    public void updateCardStatus(String s, Player player){
        switch(s){          //특정 지점으로 도착 시 해당 카드 보유 +1
            case "S":
                player.setNumOfCards(0, 1);
                break;
            case "H":
                player.setNumOfCards(1, 1);
                break;
            case "P":
                player.setNumOfCards(2, 1);
                break;
            default:
                break;
        }
    }


    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }
    public int getNewCol() {
        return newCol;
    }
    public int getNewRow() {
        return newRow;
    }
    public String[][] getMap() {
        return map;
    }
    public String[][] getMapWithPlayers() {
        return mapWithPlayers;
    }
    public int getInitialRow() {
        return initialRow;
    }
    public int getInitialCol() {
        return initialCol;
    }
    public boolean isHasPlayerEnded() {
        return hasPlayerEnded;
    }


}