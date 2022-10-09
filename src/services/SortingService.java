package services;

import java.io.FileNotFoundException;
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

public class SortingService {

    private int tamBloco; // Quantidades de registros a serem ordenados por segmento
    private int caminhos; // Número de caminhos/arquivos temporários
    private HashMap<Integer,Registro> registros;
    ArrayList<RandomAccessFile> arrArquivosTmp;
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

    public class RegistroTemporario {
        public RegistroTemporario(int idBloco2, int tam2, int id2, byte[] ba) {
        }
        public int idBloco;
        public int id;
        public int tam;
        public byte[] content;

    }

    class RegistroTemporarioComparator implements Comparator<RegistroTemporario> {
        
        public int compare(RegistroTemporario r1, RegistroTemporario r2){
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
    

    public class BlocoDeRegistroTemporario{
        public ArrayList<RegistroTemporario> regsTmp;

        BlocoDeRegistroTemporario(){
            regsTmp = new ArrayList<RegistroTemporario>();
        }

        public RegistroTemporario getMin() {

            RegistroTemporarioComparator rtc = new RegistroTemporarioComparator();
            regsTmp.sort(rtc);
            RegistroTemporario result = regsTmp.get(0);

            return result;

        }
    }

    public SortingService() {
        this.tamBloco = 0;
        this.caminhos = 0;
        this.registros = new HashMap<Integer,Registro>();
        this.arrArquivosTmp = new ArrayList<RandomAccessFile>();
        this.sc = new Scanner(System.in);
    }


    public void readFromFile(int opcao) {
        
        byte[] result;

        long pos0 = 0;
        long skipPos0 = 0; long address = 0;

        int numSegmento = 1;
        
        try  {
                RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
                RandomAccessFile skipPtr = new RandomAccessFile("contas.db", "rw");
                
                int aux = arq.readInt();
                aux = skipPtr.readInt();
                
                pos0 = arq.getFilePointer();
                skipPos0 = skipPtr.getFilePointer();

                
                while (pos0 != arq.length()) {

                    for(int i = 1; i <= tamBloco; i++){

                        address = arq.getFilePointer();
                        int tam = arq.readInt();

                        int id = arq.readInt();
                        
                        Registro tmp = new Registro(id, tam, address);
                        this.registros.put(i*numSegmento,tmp);

                        
                        skipPos0 = skipPtr.skipBytes(tam);
                        pos0 = skipPos0;
                    
                    }

                    if( opcao == 1 || opcao == 2 || opcao == 4){
                        distribuicaoInicial(numSegmento);

                    }

                    if( opcao == 3){
                        intercalacaoBalanceadaSelecaoPorSubstituicao();
                    }

                    if( opcao == 5){
                        intercalacaoPolifasica();
                    }

                    numSegmento++;
            
                }
                
                arq.close();
                skipPtr.close();

        } catch (IOException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
        }

        /* Aqui comecaremos a chamar os metodos de ordenacao de cada um */

        if(opcao == 1 ){
            intercalacaoBalanceada(numSegmento);
        }


    }

    
    public void ListarOpcoes() {
        System.out.println("Opcoes");
        int opcao = sc.nextInt();
        readFromFile(opcao);
    }
    
    public void distribuicaoInicial(int numSegmento){

        Collection<Registro> tmp = registros.values();

        ArrayList<Registro> segmento = new ArrayList<>();
        segmento.addAll(tmp);

        RegistroComparator rc = new RegistroComparator();
        segmento.sort(rc);

        byte[] ba;

        for(int i = 0; i < this.caminhos; i++){
            String fileName = new String("tempfile"+i+".db");
            RandomAccessFile arqTemp;

            try {
                arqTemp = new RandomAccessFile(fileName, "rw");
                this.arrArquivosTmp.add(arqTemp);

                RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
                int aux = arq.readInt();

                RandomAccessFile tmpFileOne = arrArquivosTmp.get(i);
                tmpFileOne.seek(tmpFileOne.length());   

                tmpFileOne.write(numSegmento);
            

                for (Registro reg : segmento) {
                    long addr = reg.address;
                    int tam = reg.len;

                    arq.seek(addr);
                    ba = new byte[tam];
                    arq.read(ba);

                    tmpFileOne.writeInt(tam);
                    tmpFileOne.writeInt(reg.id);
                    tmpFileOne.write(ba);
                }

                tmpFileOne.close();
                arq.close();



            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }

        }

    



        

        

        
    }

    public void intercalacaoBalanceada(int numSegmento){

        ArrayList<RandomAccessFile> acessos = new ArrayList<>();
        BlocoDeRegistroTemporario blocoRegs = new BlocoDeRegistroTemporario();

        byte[] ba;

        for(int i = 1; i <= caminhos; i++){

            String fileName = new String("tempfileinteracalacao"+i+".db");
            RandomAccessFile arqTemp;

            try {
                arqTemp = new RandomAccessFile(fileName, "rw");
                acessos.add(arqTemp);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }

        }
        
        try {

            
            int idBloco = 1;
            
            for(int i = 1; i <=  caminhos; i++){



                for(int j = 0; j < tamBloco; j++ ){
                    
                    //Esse foreach vai ler de um em um registro
                    //nos N arquivos
                    for (RandomAccessFile a : arrArquivosTmp) {
                        idBloco = a.readInt();
                        int tam = a.readInt();
                        int id = a.readInt();

                        ba = new byte[tam];
                        a.read(ba);

                        RegistroTemporario tmp = new RegistroTemporario(idBloco, tam, id, ba);
                        blocoRegs.regsTmp.add(tmp);

                    }


                    // Pega o registro com menor id que foi lido
                    // no bloco e escrever no proximo arquivo temporario
                    RegistroTemporario minRegsTmp = blocoRegs.getMin(); 
                    escreverRegistroTemporario(acessos.get(i), minRegsTmp);

                    
                    // Quando chegar nos ultimos registro de cada bloco
                    // ele vai intercalar os que sobraram
                    if (j == tamBloco - 1){
                        RegistroTemporarioComparator  rtc = new RegistroTemporarioComparator();
                        blocoRegs.regsTmp.sort(rtc);

                        for (RegistroTemporario a : blocoRegs.regsTmp) {
                            escreverRegistroTemporario(acessos.get(i), a);
                        }
                    }
                }
            }

            



            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public void escreverRegistroTemporario(RandomAccessFile raf, RegistroTemporario rt){
        try {
            raf.writeInt(rt.tam);
            raf.writeInt(rt.id);
            raf.write(rt.content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }
   
}


















