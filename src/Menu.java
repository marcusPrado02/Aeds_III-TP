import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Importamos todos as classes do pacote services
 */
import model.Conta;
import services.*;

/*
 * A class Menu faz o intermaedio entre a interface 
 * via terminal e os services Internos da aplicação
 */
public class Menu{

    /*
     * Atributos que representam os services e utilitarios 
     * necessarios para a funcao principal ( loop ) da classe
     */
    private CRUDService crud;
    private SortingService sorting;
    private BankTransferService transfer;
    private Scanner sc;


    /*
     * Construtor da classe menu que é utilizado em sua instanciação
     * na classe Main
     */
    Menu(CRUDService cruds, SortingService ss, BankTransferService bt){
        crud = cruds;
        sorting = ss;
        transfer = bt;
        sc =  new Scanner(System.in); 

    }


    /*
     * Método responsável por Mostrar na terminal as opções de operações
     * disponiveis para o usuario do sistema
     */

    private void MostrarOpcoes(){
        System.out.println("                        _____________________________________\n");
        System.out.println("                            Sistema de gerenciamento de ");
        System.out.println("                                contas bancárias    ");
        System.out.println("                        _____________________________________\n");
        System.out.println("                        (1) Criar uma Conta\n");
        System.out.println("                        (2) Realizar Tranferência Bancária\n");
        System.out.println("                        (3) Ler um Registro\n");
        System.out.println("                        (4) Atualizar um Registro\n");
        System.out.println("                        (5) Deletar um Registro\n");
        System.out.println("                        (6) Ordenação do Arquivo\n");
        System.out.println("                        (7) Consultar Id por meio de Indice  (hash) \n");
        System.out.println("                        (8) Consultar Ids Por Cidade\n");
        System.out.println("                        (9) Consultar Ids Por Nome\n");
        System.out.println("                        _____________________________________\n");
        System.out.println("\n");
       
    }


    /*
     * Essa é a funcãp principal do menu que coleta qual a operação
     * que o usuario desejava fazer e em seguida faz a chamada do 
     * respectivo service associado a tal operação no sistema
     */
    void loop() throws InterruptedException, IOException {
        boolean continuar = true;
        int operacao = 0;

        do {

            MostrarOpcoes();

            
            System.out.println("\nEscolha uma das operações:");
            operacao = Integer.parseInt(sc.nextLine());

            switch (operacao) {
                case 1:
                    criarConta();
                    break;
                case 2:
                    transferenciaBancaria();
                    break;
                case 3:
                    ler();
                    break;
                case 4:
                    atualizar();
                    break;
                case 5:
                    deletar();
                    break;
                case 6:
                    ordenar();
                    break;
                case 7:
                    ordenar();
                    break;
                case 8:
                    ordenar();
                    break;
            
                default:
                    break;
                    
            }

            System.out.println("Deseja realizar outra operação? [S/N]");
            String tmp = sc.nextLine();


            if(tmp.length() == 1){
                continuar = (tmp.toUpperCase().charAt(0) == 'S')? true : false;


            }else{
                
                System.out.println("\n Algo deu errado ao tentar concluir a operação,tente novamente.\n");
                break;
            

            }
            
        } while (continuar == true);

        
    }

    /*
     * Essa função é acionada quando o usuário deseja ordenar o 
     * arquivo de dados,ela lê o tamanho dos blcocos desejados
     * na ordenação do arquivo de dados e em seguida chama
     * o service reponsavel por ordenação
    */
    private void ordenar() {
        System.out.println("\nDigite o tamanho do bloco para a intercalação:\n ");
        String tmp = sc.nextLine();
        sorting.sortOriginalFile(Integer.valueOf(tmp));
    }

    /*
     * Essa função aciona o o service responsável pela realização
     * das tranferencias bancarias entre as contas registradas 
     * anteriormente no arquivo de dados
     */
    private void transferenciaBancaria() {
        transfer.EfetuarTransferencia();
    }


    /*
     * Essa função cria uma conta nova:
     *      -Primeiro coleta as informações necessárias,como por
     *      exemplo nome Completo,emails,nome de usuario,senha 
     *      entre outras informações.
     * 
     *      -Ele faz uma pequena validadção sobre o CPF coletado
     * 
     *      -E passa como parametro um objeto conta para a função 
     *      que propriamente cria o novo registro no arquivo
     */
    private void criarConta() throws InterruptedException {
        try {

            Conta tmp = new Conta();

            System.out.println("\nDigite o nome completo:\n ");
            String tmp_nome = sc.nextLine();
            tmp.setNomePessoa(tmp_nome);

            System.out.println("\nDigite seu emails deparados por espaço:\n ");
            String[] tmp_emails = sc.nextLine().split(" ", 0);

            ArrayList<String> aux = new ArrayList<>();

            for (String email: tmp_emails) {
                if(email.length() > 1){
                    aux.add(email);

                }
            }
            
            tmp.setEmail(aux);

            System.out.println("\nDigite um nome de usuário:\n ");
            String tmp_nome_usuario = sc.nextLine();
            tmp.setNomeUsuario(tmp_nome_usuario);

            System.out.println("\nDigite uma senha: \n");
            String tmp_senha = sc.nextLine();
            tmp.setSenha(tmp_senha);

            System.out.println("\nDigite o seu CPF: \n");
            String tmp_cpf = sc.nextLine();
            
            while(tmp_cpf.length() != 11){

                if(tmp_cpf.length() < 11){
                    System.out.println("Muito pequeno para um CPF");
                }
                if(tmp_cpf.length() > 11){
                    System.out.println("Muito grande para um CPF");
                }
                
                System.out.println("\nDigite o seu CPF: \n");
                tmp_cpf = sc.nextLine();
            }

            tmp.setCpf(tmp_cpf);

            System.out.println("\nDigite sua cidade: \n");
            String tmp_cidade = sc.nextLine();
            tmp.setCidade(tmp_cidade);

            System.out.println("\nDepósito inicial:");
            float tmp_di = Float.parseFloat(sc.nextLine());
            tmp.setSaldoConta(tmp_di);

            tmp.setTransferenciasRealizadas(0);
            criarNoArquivo(tmp);



        } catch (Exception e) {
            // TODO: handle exception
            
        }

        
        System.out.println("Conta criada com Sucesso.");
        
    }


    /*
     * Essa função vai acionar o Service responsável pela
     * Criação do do registro no arquivo de dados,essa função
     * recebe @(Conta tmp) como um objeto conta que terá suas 
     * informações inseridas no arquivo de dados no formato de 
     * array de bytes  
     */
    private void criarNoArquivo(Conta tmp) throws IOException {
        crud.criar(tmp);
    }

    /*
     * Essa função vai acionar o Service responsável pela
     * Leitura de um registro arqmazenado previamente no arquivo
     * de dados,ela coleta o seu ID e faz a  chamada do metodo 
     * ler do Objeto CRUDService  
     */
    private void ler() throws IOException {
        System.out.println("Digite o ID da conta: ");
        String tmp = sc.nextLine();
        crud.ler(Integer.valueOf(tmp));
       
    }

    /*
     * Essa função vai acionar o Service responsável pela
     * deleção de um registro arqmazenado previamente no arquivo
     * de dados,ela coleta o seu ID e faz a  chamada do metodo 
     * deletar do Objeto CRUDService
     */
    private void deletar() throws IOException {
        System.out.println("Digite o ID da conta: ");
        String tmp = sc.nextLine();
        crud.deletar(Integer.valueOf(tmp));
    }

    /*
     * Essa função vai acionar o Service responsável pela
     * Atualização de um registro arqmazenado previamente no arquivo
     * de dados,ela coleta o seu ID e faz a  chamada do metodo 
     * atualizar do Objeto CRUDService
     * 
     */
    private void atualizar() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta: ");
        String tmp = sc.nextLine();
        crud.atualizar(Integer.valueOf(tmp));
    }
}