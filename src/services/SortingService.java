package services;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

/*
 *  a) Intercalação balanceada comum
    b) Intercalação balanceada com blocos de tamanho variável
    c) Intercalação balanceada com seleção por substituição
    d) Intercalação balanceada usando n+1 arquivos
    e) Intercalação Polifásica

 * */

public class SortingService{

    private int M; // Quantidades de registros a serem ordenados por segmento
    private int N; // Número de caminhos/arquivos temporários
    private HashMap<Integer,Registro> registros;
    private Scanner sc;

    public class Registro {
        public int id;
        public int len;
        public long address;

        public Registro(int id, int len, long address) {
            this.id = id;
            this.len = len;
            this.address = address;
        }

    }

    class RegistroComparator implements Comparator<Registro> {
        
        public int compare(Registro r1, Registro r2){
            if (r1.id == r2.id) {
                return 0;
            }
            else if (r1.id < r2.id) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }

    public SortingService() {
        this.M = 0;
        this.N = 0;
        this.registros = new HashMap<Integer,Registro>();
        this.sc = new Scanner(System.in);
    }


    public void readFromFile(int opcao) {
        
        byte[] result;

        long pos0 = 0;
        long skipPos0 = 0; long address = 0;

        int counter = 0;
        
        try  {
                RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
                RandomAccessFile skipPtr = new RandomAccessFile("contas.db", "rw");
                
                int aux = arq.readInt();
                aux = skipPtr.readInt();
                
                pos0 = arq.getFilePointer();
                skipPos0 = skipPtr.getFilePointer();

                
                while (pos0 != arq.length()) {

                    for(int i = 0; i < M; i++){

                        address = arq.getFilePointer();
                        int tam = arq.readInt();

                        int id = arq.readInt();
                        
                        Registro tmp = new Registro(id, tam, address);
                        this.registros.put(M,tmp);

                        
                        skipPos0 = skipPtr.skipBytes(tam);
                        pos0 = skipPos0;
                    
                    }

                    if( opcao == 1){
                        intercalacaoBalanceada(counter);
                    }

                    if( opcao == 2){
                        intercalacaoBalanceadaTamanhoVariavel();
                    }

                    if( opcao == 3){
                        intercalacaoBalanceadaSelecaoPorSubstituicao();
                    }

                    if( opcao == 4){
                        intercalacaoBalanceadaNmaisUm();
                    }

                    if( opcao == 5){
                        intercalacaoPolifasica();
                    }

                    counter++;
            
                }
                
                arq.close();
                skipPtr.close();

        } catch (IOException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
        }
    }

    
    public void ListarOpcoes() {
        System.out.println("Opcoes");
        int opcao = sc.nextInt();
        readFromFile(opcao);
    }
    
    public void intercalacaoBalanceada(int counter){
        Collection<Registro> tmp = registros.values();

        ArrayList<Registro> segmento = new ArrayList<>();
        segmento.addAll(tmp);

        RegistroComparator rc = new RegistroComparator();
        segmento.sort(rc);

        if(counter % 2 == 0){
            byte[] ba;

            try  {
                RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
                int aux = arq.readInt();

                RandomAccessFile tmpFile = new RandomAccessFile("tmp1.db", "rw");
                

                for (Registro reg : segmento) {
                    long addr = reg.address;
                    int tam = reg.len;

                    arq.seek(addr);
                    ba = new byte[tam];
                    arq.read(ba);

                    tmpFile.write(ba);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
            }

        } else {
            byte[] ba;

            try  {
                RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
                int aux = arq.readInt();

                RandomAccessFile tmpFile = new RandomAccessFile("tmp2.db", "rw");
                

                for (Registro reg : segmento) {
                    long addr = reg.address;
                    int tam = reg.len;

                    arq.seek(addr);
                    ba = new byte[tam];
                    arq.read(ba);

                    tmpFile.write(ba);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
            }

        }
        
    }
    
    public void intercalacaoBalanceadaTamanhoVariavel(){
        
    }

    private void intercalacaoBalanceadaSelecaoPorSubstituicao() {

    }

    public void intercalacaoBalanceadaNmaisUm(){
        
    }

    public void intercalacaoPolifasica(){
        
    }
   
}


















