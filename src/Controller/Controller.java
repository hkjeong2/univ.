package Controller;

import Model.Model;
import View.View;
import java.util.Scanner;

public class Controller {
    private Model model = new Model();
    private View view = new View(model, this);

    private Scanner sc;
    private int nOption;
    private int totalPlayerNum = 0;
    private String sOption;
    private boolean hasPlayerChanged;

    public void init(){
        model.addObserver(view);
        startGame();
    }

    public void startGame(){    //게임 시작
        view.printMenu();
        nOption = getInput();

        if (nOption == 1){             //플레이할 맵 메뉴 선택
            mapOption();
            startGame();
        }
        else if (nOption == 2){         //map 로드 이후 game 플레이
            model.loadMap();
            playGame();
        }
        else{                           //오류: menu 이외 값 입력 시 재차 입력
            view.printInvalid();
            startGame();
        }
    }

    public void mapOption(){
        view.printMapMenu();
        sc = new Scanner(System.in);
        sOption = sc.nextLine();
        model.loadMap(sOption);             //입력된 맵 파일로 지도 생성
        view.printMap(model.getMap());
    }

    public void playGame(){
        setUp();            //플레이어 인원 입력 이후 player 객체 생성하여 준비
        doAction();         //플레이어 action
    }

    public void doAction(){
        hasPlayerChanged = false;       //도착한 플레이어가 있는지

        while(true){

            if(model.getArrivedPlayerList().size() == totalPlayerNum - 1){     //한 명을 제외한 모든 플레이어가 도착했을 시 종료 (종료 조건)
                model.getArrivedPlayerList().add(model.getPlayerList().get(0));     //sorting하기 위해 마지막 player도 ArriedPlayerList에 추가
                model.sortRanking();                //점수별로 순위 지정
                view.printTerminateGame();          //게임 종료
                return;
            }

            for(int i = 0; i < model.getPlayerList().size(); i++){              //참가한 플레이어가 돌아가면서 action

                view.askPlayerDecision(model.getPlayerList().get(i).getId());   //현재 플레이어 명시 후 어떤 행동 취할 지 (roll die or stay)
                int input = getInput();

                if (input == 1){            // ----- 1. ROLL DIE -----
                    int moveValue = 0;
                    moveValue = rollDie(i);
                    doCheckMoves(moveValue, i);              //나온 값으로 플레이어 이동
                    model.updateMapWithPlayer();            //이동 이후 map update
                    if(hasPlayerChanged){           //도착한 player 존재 시
                        doAction();                 //해당 player 제외한 player들 끼리 게임 resume
                        return;
                    }

                }
                else if(input == 2){        // ----- 2. STAY -----

                    if(!model.hasBridgeCard(i)){                //bridge card 없을 시
                        view.printNotEnoughBridgeCard(i);
                        i--;                                    //선택지 다시 choose
                    }
                    else{                                       //bridge card 있을 시 한 장 반납
                        model.getPlayerList().get(i).setNumOfCards(3, -1);
                        view.printReturnBridgeCard(i);
                    }
                    model.updateMapWithPlayer();

                }
                else{                       //오류: INVALID INPUT
                    view.printInvalid();
                    i--;
                }
            }
        }
    }

    int rollDie(int id){
        int numOfDie = model.getDieNum(id);      //주사위 굴림
        view.printDieNum(numOfDie);
        int moveValue = model.getMoveNum(id, numOfDie);          //값 = 주사위 값 - bridge card 개수
        view.printAllowedMoves(numOfDie, moveValue, model.getPlayerList().get(id).getNumOfCard(3));

        return moveValue;
    }

    public void doCheckMoves(int num, int playerID){
        while(true){

            if (num == 0){                  //굴린 주사위 값이 0이라 움직일 수 없을 때 턴만 넘김
                view.printZeroMove();
                return;
            }

            view.askMoves();
            sc = new Scanner(System.in);
            String moves = sc.nextLine();   //이동할 경로 입력

            if (moves.length() == num){     // 문자 수 == 주사위 값 일 때
                if((model.movePlayer(moves, playerID)) == true) {       // 유효한 move
                    view.printPlayerMoved(playerID);
                    if(model.hasPlayerEnded()){                 //해당 move 수행으로 END 도착한 플레이어 있을 시
                        sortPlayerList(playerID);
                    }
                    return;
                }
                else{                           //오류: 유효하지 않은 path
                    view.printInvalidPath();
                }
            }
            else{                               //오류: 문자 수와 주사위 값 다를 시
                view.printInvalidMovesDie();
            }
        }
    }

    public void sortPlayerList(int playerID){
        view.printPlayerArrived(model.getPlayerList().get(playerID).getId());       //도착
        view.printNoMoreBackMoves();
        model.getArrivedPlayerList().add(model.getPlayerList().get(playerID));      //도착한 플레이어 추가
        model.updatePlayerList(playerID);   //도착한 플레이어 기존 player list 에서 삭제 및 이후 순번부터 시작하기 위해 리스트 정렬
        hasPlayerChanged = true;
    }

    public void setUp(){
        view.printMap(model.getMap());
        view.askNumOfPlayer();
        setUpPlayer();                      //입력 수 만큼 플레이어 생성
        model.updateMapWithPlayer();
    }

    public int getInput(){
        sc = new Scanner(System.in);
        return sc.nextInt();
    }

    public void setUpPlayer(){
        Integer input = getInput();
        if (input > 4 || input < 2){
            view.printInvalid();
            setUp();
        }
        else{
            totalPlayerNum = input;
            for (int i = 0; i < input; i++){
                model.initPlayer(i+1);          //플레이어 객체 초기화
            }
        }
    }


}
