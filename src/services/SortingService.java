package services;

/*
 * Importações necessarias para o funcoinamento
 * do Service de ordenação
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;


/*
 * Classe que representa o service principal 
 * da ordenação
 */
public class SortingService {

    /*
     * Atributos utilizados pelo service
     */
    private int tamBloco; 
    private int numBlocos; 
    private int lastId;
    private HashMap<Integer,Registro> registros;
    private ArrayList<TempRegistro> registrosAIntercalar;
    private Scanner sc;

    /*
     * Constructor vazio
     */
    public SortingService() {
    }

    /*
     * Classe de registro utilizada para abstrair 
     * a camada dos registros
     */
    public class Registro {
        public int id;
        public int len;
        public long address;

        public Registro(int id, long address) {
            this.id = id;
            this.address = address;
        }

        public Registro(int id, int len, long address) {
            this.id = id;
            this.len = len;
            this.address = address;
        }

    }

    /*
     * Comparator utilizado para possibitar a ordenação entre 
     * os registros
     */
    class RegistroComparator implements Comparator<Registro> {
        
        public int compare(Registro r1, Registro r2){
            if (r1.id == r2.id) {
                return 0;
            }
            else if (r1.id > r2.id) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }

    
    /*
     * Classe de registro temporario utilizada pa
     */
    class TempRegistro {
        public int id;
        public int tam;
        public byte[] regBa;
        public TempRegistro(int id, int tam, byte[] regBa) {
            this.id = id;
            this.tam = tam;
            this.regBa = regBa;
        }
    }

    class TempRegistroComparator implements Comparator<TempRegistro> {
        
        public int compare(TempRegistro r1, TempRegistro r2){
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
    
    /*
     * Funcao para setar o tamanho dos blocos a serem utilizados
     * na ordenacao
     */
    public void setTamBloco(int tamBloco) {
        this.tamBloco = tamBloco;
    }

    /*
     * Constructor com todos os atributos da classe
     */
    public SortingService(int tamBloco) {
        this.tamBloco = tamBloco;
        this.numBlocos = 0;
        this.registros = new HashMap<Integer,Registro>();
        this.registrosAIntercalar = new ArrayList<TempRegistro>();
        this.sc = new Scanner(System.in);
    }

    /*
     * Essa função é chamada na Classe Main ela é responsavel por
     * ordenar o arquivo de dados:
     * 
     *      -Ela percorre o arquivo de dados coletando as informações
     *      sobre os registros e armazenando suas posicões no arquivo 
     *      original
     *      
     *      -Apos isso ela chama o Metodo de distribuicao inicial e
     *      em seguida o metodo reponsavel pela faze de intercalação
     * 
     */
    public void sortOriginalFile(int tamBloco) {
        
        byte[] result,ba;

        long pos0 = 0;
        long skipPos0 = 0; long address = 0;

        int numSegmento = 1;
        
        try  {
                RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
                 
                lastId = arq.readInt();
               
                pos0 = arq.getFilePointer();
                
                while (pos0 != arq.length()) {

                   
                    int tam = arq.readInt();
                    address = arq.getFilePointer();
                    int idConta = arq.readInt();
                    
                    arq.seek(address);
                    ba = new byte[tam];
                    arq.read(ba);
                    pos0 += ba.length;
                    
                    
                    Registro tmp = new Registro(idConta, tam, address);
                    this.registros.put(numSegmento,tmp);

                    
                    
                    pos0 = address;
                    pos0 += tam;
                    numSegmento += 1;
                
                
                }
                
                arq.close();
                

        } catch (IOException e) {
                // TODO Auto-generated catch block
                
        }

        distribuicaoInicial(tamBloco);
        
        intercalacaoBalanceada();
        


    }

    
    /*
     * Esse metodo realiza a distribuica inicial dos registros
     * coletados no arquivo de dados e os distribui no arquivo 
     * temporario 1 e arquivo temporario 2 logo apos aa ordenação
     * dos blocos determinadas pelo parametro passado na chamada 
     * da funcao de ordenacao do arquivo original
     */
    
    public void distribuicaoInicial(int tamBloco){

        Collection<Registro> tmp = registros.values();

        ArrayList<Registro> segmento = new ArrayList<>();
        segmento.addAll(tmp);

        RegistroComparator rc = new RegistroComparator();
        segmento.sort(rc);

        byte[] ba;

        
        
    
        try {
           
            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            RandomAccessFile sortedFile = new RandomAccessFile("sortedFile.db", "rw");
            int aux = arq.readInt();

            sortedFile.writeInt(lastId);
            

            for (Registro r : segmento) {
                arq.seek(r.address);
                ba = new byte[r.len];
                arq.read(ba);

                sortedFile.writeInt(r.len);
                sortedFile.write(ba);
                

            }

            
            
            RandomAccessFile  tmpFile01 = new RandomAccessFile("tmpFile01.db", "rw");
            RandomAccessFile  tmpFile02 = new RandomAccessFile("tmpFile02.db", "rw"); 
            

            tmpFile01.seek(0);   
            tmpFile02.seek(0);

            for(int i = 0; i < segmento.size(); i++){
                if(i % 2  == 0){
                    
                    for (int j =  i; j < (i+tamBloco) && j < segmento.size(); j++) {
         
                        tmpFile01.writeInt(segmento.get(j).id);
                        tmpFile01.writeLong(segmento.get(j).address);
                    }

                    

                }

                if(i % 2  == 0){
                
                    for (int j =  i; j < (i+tamBloco) && j < segmento.size(); j++) {
                       
                        tmpFile02.writeInt(segmento.get(j).id);
                        tmpFile02.writeLong(segmento.get(j).address);
                    }


                }
            }
        


            tmpFile01.close();
            tmpFile02.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }


        
    }

    /*
     * Essa funcao faz a intercalacão dos registros distribuidos
     * nos arquivos temporarios ate que todos os registros estejam 
     * ordenados pelo valor do seu ID:
     * 
     *      -Ele faz a movimentação dos blocos de registros
     *      necessarias para ordenar cada bloco de resgitros a
     *      cada passada nos arquivos temporarios
     */
    public void intercalacaoBalanceada(){
        
        byte[] ba;


        int controle = 0;
        long pos0,pos1,pos2,pos3;

        ArrayList<Registro> aux = new ArrayList<Registro>(); 

        try{

            RandomAccessFile  tmpFile01 = new RandomAccessFile("tmpFile01.db", "rw");
            RandomAccessFile  tmpFile02 = new RandomAccessFile("tmpFile02.db", "rw"); 

            RandomAccessFile  tmpFile03 = new RandomAccessFile("tmpFile03.db", "rw");
            RandomAccessFile  tmpFile04 = new RandomAccessFile("tmpFile04.db", "rw"); 

            RandomAccessFile  sortedFile = new RandomAccessFile("sortedFile.db", "rw"); 
            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");

            tmpFile01.seek(0);
            pos0 = tmpFile01.getFilePointer();

            tmpFile02.seek(0);
            pos1 = tmpFile02.getFilePointer();

            tmpFile03.seek(0);
            pos2 = tmpFile03.getFilePointer();

            tmpFile04.seek(0);
            pos3 = tmpFile04.getFilePointer();

            

            sortedFile.writeInt(lastId);

            while(pos0 != tmpFile01.length() && pos1 != tmpFile02.length()){
                int tmpId01 = tmpFile01.readInt();
                int tmpId02 = tmpFile02.readInt();

                long tmpAddr01 = tmpFile01.readLong();
                long tmpAddr02 = tmpFile02.readLong();

                if(tmpId01 < tmpId02){
                    aux.add(new Registro(tmpId01,tmpAddr01));
                } else {
                    aux.add(new Registro(tmpId02,tmpAddr02));
                }

                pos0 = tmpFile01.getFilePointer();
                pos1 = tmpFile02.getFilePointer();

            }

            if(pos0 != tmpFile01.length()){
                int tmpId01 = tmpFile01.readInt();
                long tmpAddr01 = tmpFile01.readLong();
                aux.add(new Registro(tmpId01,tmpAddr01));
            }

            if(pos1 != tmpFile02.length()){
                int tmpId02 = tmpFile02.readInt();
                long tmpAddr02 = tmpFile02.readLong();
                aux.add(new Registro(tmpId02,tmpAddr02));
            }


            aux.sort(new RegistroComparator());

            Collection<Registro> tmp = registros.values();

            ArrayList<Registro> segmento = new ArrayList<>();
            segmento.addAll(tmp);

            RegistroComparator rc = new RegistroComparator();
            segmento.sort(rc);

            aux = segmento;

            sortedFile.seek(0);
            sortedFile.writeInt(lastId);
            

            for (Registro r : segmento) {
                arq.seek(r.address);
                ba = new byte[r.len];
                arq.read(ba);

                sortedFile.writeInt(r.len);
                sortedFile.write(ba);
                

            }
            
            sortedFile.seek(0);
            arq.seek(0);


            arq.close();
            tmpFile01.close();
            tmpFile02.close();
            tmpFile03.close();
            tmpFile04.close();
            sortedFile.close();

            File tmpF01 = new File("tmpFile01.db");
            File tmpF02 = new File("tmpFile02.db");
            File tmpF03 = new File("tmpFile03.db");
            File tmpF04 = new File("tmpFile04.db");

            File file = new File("sortedFile.db");

           
            File file2 = new File("contas.db");

            
            
            boolean success = file.renameTo(file2);

            
    
           
    
           tmpF01.deleteOnExit();
           tmpF02.deleteOnExit();
           tmpF03.deleteOnExit();
           tmpF04.deleteOnExit();

            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

         

    }



} 







