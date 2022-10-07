package services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import model.Conta;


public class CRUDService {

    private static int numero_registros;

    public CRUDService(){
        numero_registros = 0;
    }

    // Not Finished
    public void ler(int id) {
        Conta conta = new Conta();
        
        byte[] ba;
        int len;
        long pos0 = 0; long pos1 = 0;
        boolean encontrado = false;

        System.out.println("\n\n\n\n\n_________________________________________");
                        System.out.println(" Procurando registro com ID "+id+" \n");
        
        
        try  {
            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            int aux = arq.readInt();
            //System.out.println("Chegou aqui ");
            pos0 = arq.getFilePointer();
            
            while (pos0 != arq.length()) {

                int tam = arq.readInt();
                ba = new byte[tam];
                arq.read(ba);
                pos0 += ba.length;
                conta.fromByteArray(ba);
                
                
                //System.out.println(conta.getLapide());
                
                if(conta.getLapide() == ' ') {
                    

                    if(conta.getIdConta() == id) {
                        encontrado = true;
                        System.out.println("\n\n_________________________________________");
                        System.out.println(" O Registro pesquisado com sucesso \n");
                        System.out.println("_________________________________________\n\n");
                        System.out.println(conta.toString());
                        break;
                    }

                }

            }

            if(!encontrado) {
                System.out.println("\n\n\n\n\n_________________________________________");
                System.out.println(" O Registro pesquisado não foi encontrado \n");
                System.out.println("_________________________________________\n\n");
            }
            
            
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //System.out.println(e);
        }

        
        
    }

    //Need testing
    public void criar(Conta conta) throws IOException {

        byte[] ba;
        long pos0;
        
        try  {
            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            //RandomAccessFile arq = new RandomAccessFile("../dados/jogadores.db", "rw");

            pos0=arq.getFilePointer();

            if(arq.length() > 10){
                numero_registros = arq.readInt();   
            }

            conta.setIdConta(numero_registros+1);

            arq.seek(pos0);
            arq.writeInt(conta.getIdConta());

            arq.seek(arq.length());

            ba = conta.toByteArray();
            arq.writeInt(ba.length); //Tamano do registro em bytes
            arq.write(ba);

            
            
            arq.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //System.out.println(e.getMessage());
        }

        
        
    }

 
    public void deletar(int id) {
        Conta conta = new Conta();

        byte[] ba;
        int len;
        long pos0 = 0;
        long  pos1;

        
        try  {
            RandomAccessFile arq = new RandomAccessFile("dados/contas.db", "rw");
            int aux = arq.readInt();
            //System.out.println("Chegou aqui ");
            pos0 = arq.getFilePointer();
            
            while (pos0 != arq.length()) {

                int tam = arq.readInt();
                ba = new byte[tam];
                pos1 = arq.getFilePointer();
                arq.read(ba);
                pos0 += ba.length;
                conta.fromByteArray(ba);
                
                
                //System.out.println(conta.getLapide());
                
                if(conta.getLapide() == ' ') {
                    

                    if(conta.getIdConta() == id) {
                        conta.setLapide('*');
                        arq.seek(pos1);
                        ba = conta.toByteArray();
                        arq.write(ba);

                        System.out.println("\n\n\n\n\n_________________________________________");
                        System.out.println(" Registro deletado com sucesso\n");
                        System.out.println("_________________________________________");
                        System.out.println("\n Esse foi o registro deletado:\n");
                        System.out.println("_________________________________________");

                        System.out.println(conta.toString());
                    }

                }

                

            }
            
            
            arq.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            
        }

        

        

    }

    
    public void atualizar(int id)  { 
        Conta conta = new Conta();

        byte[] ba;

        byte[] baAux01,baAux02;

        int len;
        long pos0 = 0;
        long  pos1 = 0;

        Conta contaAux = new Conta();

        
    
        try {

            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            int aux = arq.readInt();
            //System.out.println("Chegou aqui ");
            pos0 = arq.getFilePointer();
            
            while (pos0 != arq.length()) {

                int tam = arq.readInt();

                pos1 = arq.getFilePointer();

                ba = new byte[tam];
                arq.read(ba);
                pos0 += ba.length;
                conta.fromByteArray(ba);
                
                
                //System.out.println(conta.getLapide());
                
                if(conta.getLapide() == ' ') {
                    

                    if(conta.getIdConta() == id) {
                        contaAux = alterarRegistro(conta);

                        baAux01 = conta.toByteArray();
                        baAux02 = contaAux.toByteArray();

                        if(baAux01.length == baAux02.length){
                            arq.seek(pos1);
                            arq.write(baAux02);

                        } else {
                            arq.seek(pos1);
                            conta.setLapide('*');
                            baAux01 = conta.toByteArray();

                            arq.write(baAux01);

                            criar(contaAux);

                        }

                        System.out.println("\n\n\n\n\n_________________________________________");
                        System.out.println(" Registro atualizado com sucesso");

                        System.out.println("_________________________________________");

                        System.out.println("\nVersão Atual do Registro\n");
                        System.out.println("_________________________________________\n\n");

                        System.out.println(contaAux.toString());
                    }

                }

            }
            
            
            arq.close();
           
        } catch(Exception e) {
            /* */
        }

        

    }

    public Conta alterarRegistro(Conta conta)  {
        Scanner sc = new Scanner(System.in);

        System.out.println("Qual informação deseja atualizar:");
        System.out.println("(1) Emails ");
        System.out.println("(2) Nome de Usuário");
        System.out.println("(3) Senha");
        System.out.println("(4) Cidade");

        int escolha = Integer.parseInt(sc.nextLine());

        switch (escolha) {
            case 1:
                System.out.println("Digite seu emails separados por espaço: ");
                String[] tmp_emails = sc.nextLine().split(" ", 0);

                ArrayList<String> conta_emails = new ArrayList<>();

                for (String email: tmp_emails) {
                    if(email.length() > 1){
                        conta_emails.add(email);

                    }
                }
                
                conta.setEmail(conta_emails);

                break;
            case 2:
                System.out.println("Digite um nome de usuário: ");
                String conta_nome_usuario = sc.nextLine();
                conta.setNomeUsuario(conta_nome_usuario);    
                break;
            case 3:
                System.out.println("Digite uma senha: ");
                String conta_senha = sc.nextLine();
                conta.setSenha(conta_senha);
                break;
            case 4:
                System.out.println("Digite sua cidade: ");
                String conta_cidade = sc.nextLine();
                conta.setCidade(conta_cidade);
                break;
        
            default:
                System.out.println("Ocorreu algum erro ao tentar realizar a atualização, tente novamente ");
                alterarRegistro(conta);
                break;
        }


        return conta;
    }
    
}