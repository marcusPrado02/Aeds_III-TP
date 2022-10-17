import java.io.IOException;

/*
 * Importamos todos as classes do pacote services
 */
import services.*;

/*
 *  Está é a classe principal do programa:
 *      
 *      - Ela instacia os Services a serem usados no
 *      enquanto o programa estiver rodando
 *      
 *      - Instanciamos a classe Menu passando os services 
 *      como parametros
 *
 *      - Rodamos a funcao loop que executará todos os 
 *      servicos quando solicitada
 *       
*/
public class Main {

    public static void main(String[] args) throws InterruptedException, IOException{

        
        CRUDService crud =  new CRUDService();
        SortingService ss =  new SortingService();
        BankTransferService bt = new BankTransferService();

        Menu menu = new Menu(crud, ss, bt);

        menu.loop();

    }

    
}
