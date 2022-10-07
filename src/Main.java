import java.io.IOException;

import services.*;


public class Main {

    public static void main(String[] args) throws InterruptedException, IOException{

        //Jogador j1= new Jogador(25, "Conceição", 49.90F);
        //Jogador j2= new Jogador(37, "José Carlos", 62.50F);
        //Jogador j3= new Jogador(291, "Pedro", 53.45F);

        
        CRUDService crud =  new CRUDService();
        SortingService ss =  new SortingService();
        BankTransferService bt = new BankTransferService();

        Menu menu = new Menu(crud, ss, bt);

        menu.loop();

    }

    
}
