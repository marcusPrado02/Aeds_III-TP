package services;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

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

    public SortingService() {
        this.M = 0;
        this.N = 0;
        this.registros = new HashMap<Integer,Registro>();
    }

    public byte[] intercalar(){

    }

    public void readFromFile(int opcao) {
        
        byte[] result;

        long pos0 = 0;
        long skipPos0 = 0; long address = 0;
        
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
                        intercalacaoBalanceada();
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


                

                }
                
                arq.close();
                skipPtr.close();

        } catch (IOException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
        }
    }

    private void intercalacaoBalanceadaSelecaoPorSubstituicao() {
    }

    public void ListarOpcoes() {
        System.out.println("Opcoes");
    }

    public void intercalacaoBalanceada(){

    }

    public void intercalacaoBalanceadaTamanhoVariavel(){
        
    }

    public void intercalacaoBalanceadaNmaisUm(){
        
    }

    public void intercalacaoPolifasica(){
        
    }
   
}


















