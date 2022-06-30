package View;

import Controller.Controller;
import Model.Model;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    private Controller controller;
    private Model model;

    public View(Model model, Controller controller){
        this.model = model;
        this.controller = controller;
    }


    @Override
    public void update(Observable o, Object arg) {
        printMap(model.getMapWithPlayers());         //player의 위치가 업데이트된 map
        printNumOfCards();                           //각 player들의 카드 현황
    }


    public void printMenu(){            //기본 메뉴
        System.out.println("-------- Game menu --------");
        System.out.println("1. Select a map to play in (otherwise, default map is choosen)");
        System.out.println("2. Play the game");
        System.out.print("Select the menu : ");
    }

    public void printMapMenu(){         //맵 메뉴
        System.out.println("\n-------- Map menu --------");
        System.out.print("Type the map file name to play in (default if it doesn't exist) : ");
    }

    public void printMap(String[][] map){       //맵 UI
        System.out.println("\nMap Loaded >>> \n");
        for (int i = 0; i < model.getRow(); i++) {
            for (int j = 0; j < model.getCol(); j++) {
                System.out.printf("%6s",map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void askMoves(){
        System.out.print("Enter the path you want to move along : ");
    }

    public void askNumOfPlayer(){
        System.out.print("How many players to play? (2 ~ 4) : ");
    }

    public void askPlayerDecision(String id){
        System.out.println(">>> The Player" + id + "'s turn ");
        System.out.println("1. Roll Die     2. Stay\n");
        System.out.print("What to do? : ");
    }

    public void printDieNum(int num){
        System.out.println("The result of the die rolled : " + num);
    }

    public void printAllowedMoves(int num, int value, int cardNum){
        System.out.println("The result of the allowed moves : " + value + " (" + num + " - " + cardNum + " (# of bridge card), 0 if negative)\n");
    }

    public void printNumOfCards(){          //카드 현황
        System.out.println("======================================Playing======================================");
        for (int i = 0; i < model.getPlayerList().size(); i++){
            System.out.println("The Player " + model.getPlayerList().get(i).getId() + " has >>>");
            for (int j = 0; j < model.getPlayerList().get(i).getCardList().size(); j++){
                System.out.print(model.getPlayerList().get(i).getCardType(j) + " : " + model.getPlayerList().get(i).getNumOfCard(j) + "     ");
            }
            System.out.println();
        }
        for (int i = 0; i < model.getArrivedPlayerList().size(); i++){
            if(i == 0){
                System.out.println("======================================Arrived======================================");
            }
            System.out.println("The Player " + model.getArrivedPlayerList().get(i).getId() + " has >>>");
            for (int j = 0; j < model.getArrivedPlayerList().get(i).getCardList().size(); j++){
                System.out.print(model.getArrivedPlayerList().get(i).getCardType(j) + " : " + model.getArrivedPlayerList().get(i).getNumOfCard(j) + "     ");
            }
            System.out.println();
        }
        System.out.println("===================================================================================");
        System.out.println("");
    }

    public void printNotEnoughBridgeCard(int id){
        System.out.println("The player" + model.getPlayerList().get(id).getId() + " doesn't have any bridge card to return to stay (need at least one)");
    }
    public void printReturnBridgeCard(int id){
        System.out.println("The player" + model.getPlayerList().get(id).getId() + " has returned one bridge card to stay");
    }

    public void printPlayerArrived(String id){
        System.out.println(">>> The Player" + id + " has arrived <<<\n");
    }

    public void printPlayerMoved(int id){
        System.out.println("The Player" + model.getPlayerList().get(id).getId() + " is moving . . .\n");
    }

    public void printInvalidMovesDie(){
        System.out.println("You must enter the string of length same as the number of allowed moves !!!\n");
    }

    public void printInvalidPath(){
        System.out.println("The entered path is not available !!!\n");
    }

    public void printZeroMove(){
        System.out.println("Cannot move due to too many bridge cards owned ! (Choose Stay to return the card)");
    }

    public void printTerminateGame(){
        System.out.println("======================================Ranking======================================");
        for (int i = 0; i < model.getArrivedPlayerList().size(); i++){
            System.out.println("The Player" + model.getArrivedPlayerList().get(i).getId() + " has earned total " + model.getArrivedPlayerList().get(i).getPoint() + " points");
        }
        System.out.println("===================================================================================\n");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> The Player" + model.getArrivedPlayerList().get(0).getId() + " has won the game <<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        System.out.println("Terminating the game . . .");
    }

    public void printNoMoreBackMoves(){
        System.out.println("********** MOVING BACKWARDS IS NO LONGER ALLOWED **********");
    }

    public void printInvalid(){
        System.out.println("INVALID INPUT\n");
    }
}
