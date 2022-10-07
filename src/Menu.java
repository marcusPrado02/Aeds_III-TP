import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import model.Conta;
import services.*;


public class Menu{

    private CRUDService crud;
    private SortingService sorting;
    private BankTransferService transfer;
    private Scanner sc;


    Menu(CRUDService cruds, SortingService ss, BankTransferService bt){
        crud = cruds;
        sorting = ss;
        transfer = bt;
        sc =  new Scanner(System.in); 

    }

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
        System.out.println("                        (6) Ordenação do Arquivo\n\n");
        System.out.println("                        _____________________________________\n");
        System.out.println("\n");
       
    }

    void loop() throws InterruptedException, IOException {
        boolean continuar = true;
        int operacao = 0;

        do {

            MostrarOpcoes();

            
            System.out.println("\nEscolha uma das operações:\n ");
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

    private void ordenar() {
        sorting.ListarOpcoes();
    }

    private void transferenciaBancaria() {
        transfer.EfetuarTransferencia();
    }

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

    private void criarNoArquivo(Conta tmp) throws IOException {
        crud.criar(tmp);
    }

    private void ler() throws IOException {
        System.out.println("Digite o ID da conta: ");
        int id = sc.nextInt();
        crud.ler(id);
       
    }

    private void deletar() throws IOException {
        System.out.println("Digite o ID da conta: ");
        int id = sc.nextInt();
        crud.deletar(id);
    }

    private void atualizar() throws IOException, InterruptedException {
        System.out.println("Digite o ID da conta: ");
        int id = sc.nextInt();
        crud.atualizar(id);
    }
}