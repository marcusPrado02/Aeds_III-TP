package services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;



public class SortingService {

    private int tamBloco; 
    private int numBlocos; 
    private HashMap<Integer,Registro> registros;
    private ArrayList<TempRegistro> registrosAIntercalar;
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
    

    public SortingService() {
        this.tamBloco = 0;
        this.numBlocos = 0;
        this.registros = new HashMap<Integer,Registro>();
        this.registrosAIntercalar = new ArrayList<TempRegistro>();
        this.sc = new Scanner(System.in);
    }


    public void readFromFile() {
        
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

                    
                    distribuicaoInicial(numSegmento);


            
                }
                
                arq.close();
                skipPtr.close();

        } catch (IOException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
        }

        
        intercalacaoBalanceada(numSegmento);
        


    }

    

    
    public void distribuicaoInicial(int numSegmento){

        Collection<Registro> tmp = registros.values();

        ArrayList<Registro> segmento = new ArrayList<>();
        segmento.addAll(tmp);

        RegistroComparator rc = new RegistroComparator();
        segmento.sort(rc);

        byte[] ba;

        
    
        try {
        
            RandomAccessFile  tmpFile01 = new RandomAccessFile("tmpFile01.db", "rw");
            RandomAccessFile  tmpFile02 = new RandomAccessFile("tmpFile02.db", "rw"); 

            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            int aux = arq.readInt();

            tmpFile01.seek(tmpFile01.length());   
            tmpFile02.seek(tmpFile02.length());

            if(numSegmento % 2 != 0){
                tmpFile01.write(numSegmento);
                for (Registro reg : segmento) {
                    long addr = reg.address;
                    int tam = reg.len;

                    arq.seek(addr);
                    ba = new byte[tam];
                    arq.read(ba);

                    tmpFile01.writeInt(tam);
                    tmpFile01.writeInt(reg.id);
                    tmpFile01.write(ba);
                }

                segmento.clear();

            }

            if(numSegmento % 2 == 0){
                tmpFile02.write(numSegmento);
                for (Registro reg : segmento) {
                    long addr = reg.address;
                    int tam = reg.len;

                    arq.seek(addr);
                    ba = new byte[tam];
                    arq.read(ba);

                    tmpFile02.writeInt(tam);
                    tmpFile02.writeInt(reg.id);
                    tmpFile02.write(ba);
                }

                segmento.clear();
                numBlocos += 1;

            }
        


            tmpFile01.close();
            tmpFile02.close();
            arq.close();



        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }


        
    }

    public void intercalacaoBalanceada(int numSegmento){

        
        byte[] ba01;
        byte[] ba02;

        int controle = 0;

        try{

            RandomAccessFile  tmpFile01 = new RandomAccessFile("tmpFile01.db", "rw");
            RandomAccessFile  tmpFile02 = new RandomAccessFile("tmpFile02.db", "rw"); 

            RandomAccessFile  tmpFile03 = new RandomAccessFile("tmpFile03.db", "rw");
            RandomAccessFile  tmpFile04 = new RandomAccessFile("tmpFile04.db", "rw"); 

            RandomAccessFile  sortedFile = new RandomAccessFile("sortedFile.db", "rw"); 

            tmpFile01.seek(0);
            tmpFile02.seek(0);

            tmpFile03.seek(tmpFile03.length());
            tmpFile04.seek(tmpFile04.length());

            for(int j = 0; j < numSegmento; j++){

                if(controle % 2 == 0){

                    for(int i = 0 ; i < tamBloco; i++){
                        mergeBlocks(tmpFile01, tmpFile02, tmpFile03);
                    }
                    for (TempRegistro tr : registrosAIntercalar) {
                        tmpFile03.writeInt(tr.tam);
                        tmpFile03.writeInt(tr.id);
                        tmpFile03.write(tr.regBa);
                        
                    }
                    registrosAIntercalar.clear();

                    controle += 1;


                } else {
                    
                    for(int i = 0 ; i < tamBloco; i++){
                        mergeBlocks(tmpFile01, tmpFile02, tmpFile04);
                    }
                    for (TempRegistro tr : registrosAIntercalar) {
                        tmpFile03.writeInt(tr.tam);
                        tmpFile03.writeInt(tr.id);
                        tmpFile03.write(tr.regBa);
                        
                    }
                    registrosAIntercalar.clear();

                    controle += 1;
                
                
                }
            }

            numSegmento = (numSegmento/2);

            for(int i = 1; i < 1000000 && (numSegmento/2 < 1); i++){
                if(i % 2 != 0){
                    repetirIntercalacao( numSegmento ,tmpFile03, tmpFile04, tmpFile01, tmpFile02);
                    numSegmento = (numSegmento/2);

                    for (int j = 0; j <= tmpFile03.length(); j++) {
                        tmpFile03.writeUTF("");    
                    }

                    for (int j = 0; j <= tmpFile04.length(); j++) {
                        tmpFile04.writeUTF("");    
                    }

                } else {
                    repetirIntercalacao( numSegmento ,tmpFile01, tmpFile02, tmpFile03, tmpFile04);
                    numSegmento = (numSegmento/2);

                    for (int j = 0; j <= tmpFile01.length(); j++) {
                        tmpFile01.writeUTF("");    
                    }

                    for (int j = 0; j <= tmpFile02.length(); j++) {
                        tmpFile02.writeUTF("");    
                    }
                }
            }

            /*
             * Ultima passada para voltar pro arquivo original
             */
            if(tmpFile01.length() > 1 && tmpFile02.length() > 1 && tmpFile03.length() < 1 && tmpFile04.length() < 1){
                
                for(int i = 0 ; i < tamBloco; i++){
                    mergeBlocks(tmpFile01, tmpFile02, sortedFile);
                }
                for (TempRegistro tr : registrosAIntercalar) {
                    sortedFile.writeInt(tr.tam);
                    sortedFile.writeInt(tr.id);
                    sortedFile.write(tr.regBa);
                    
                }
                registrosAIntercalar.clear();

                for (int j = 0; j <= tmpFile01.length(); j++) {
                    tmpFile01.writeUTF("");    
                }

                for (int j = 0; j <= tmpFile02.length(); j++) {
                    tmpFile02.writeUTF("");    
                }

            }

            if(tmpFile03.length() > 1 && tmpFile04.length() > 1 && tmpFile01.length() < 1 && tmpFile02.length() < 1){
                for(int i = 0 ; i < tamBloco; i++){
                    mergeBlocks(tmpFile03, tmpFile04, sortedFile);
                }
                for (TempRegistro tr : registrosAIntercalar) {
                    sortedFile.writeInt(tr.tam);
                    sortedFile.writeInt(tr.id);
                    sortedFile.write(tr.regBa);
                    
                }
                registrosAIntercalar.clear();

                for (int j = 0; j <= tmpFile03.length(); j++) {
                    tmpFile01.writeUTF("");    
                }

                for (int j = 0; j <= tmpFile04.length(); j++) {
                    tmpFile02.writeUTF("");    
                }

            }




            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }


    }

    public void repetirIntercalacao(int numSegmento,RandomAccessFile tmpFile01, RandomAccessFile tmpFile02, RandomAccessFile tmpFile03,RandomAccessFile tmpFile04 ) {
        byte[] ba01;
        byte[] ba02;

        int controle = 0;

        if(numSegmento/2 < 1){
            return;
        }

        try{

            tmpFile01.seek(0);
            tmpFile02.seek(0);

            tmpFile03.seek(tmpFile03.length());
            tmpFile04.seek(tmpFile04.length());

            for(int j = 0; j < numSegmento; j++){

                if(controle % 2 == 0){

                    for(int i = 0 ; i < tamBloco; i++){
                        mergeBlocks(tmpFile01, tmpFile02, tmpFile03);
                    }
                    for (TempRegistro tr : registrosAIntercalar) {
                        tmpFile03.writeInt(tr.tam);
                        tmpFile03.writeInt(tr.id);
                        tmpFile03.write(tr.regBa);
                        
                    }
                    registrosAIntercalar.clear();

                    controle += 1;


                } else {
                    
                    for(int i = 0 ; i < tamBloco; i++){
                        mergeBlocks(tmpFile01, tmpFile02, tmpFile04);
                    }
                    for (TempRegistro tr : registrosAIntercalar) {
                        tmpFile03.writeInt(tr.tam);
                        tmpFile03.writeInt(tr.id);
                        tmpFile03.write(tr.regBa);
                        
                    }
                    registrosAIntercalar.clear();

                    controle += 1;
                
                
                }
            }


            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        
    }

    public void mergeBlocks(RandomAccessFile tmpFile01, RandomAccessFile tmpFile02, RandomAccessFile tmpFile03){

        byte[] ba01;
        byte[] ba02;

        int auxNumSegmento;
        try {


            auxNumSegmento = tmpFile01.readInt();
            auxNumSegmento = tmpFile02.readInt();
    
            int tam01 = tmpFile01.readInt();
            int id01 = tmpFile01.readInt();
    
            int tam02 = tmpFile02.readInt();
            int id02 = tmpFile02.readInt();
    
            ba01 = new byte[tam01];
            tmpFile01.read(ba01);
    
            ba02 = new byte[tam02];
            tmpFile02.read(ba02);
            
            if(id01 < id02 && registrosAIntercalar.size() == 0 ){
                tmpFile03.writeInt(tam01);
                tmpFile03.writeInt(id01);
                tmpFile03.write(ba01);

                TempRegistro tmp = new TempRegistro(tam02, id02, ba02);
                registrosAIntercalar.add(tmp);

                
            }else if(id01 > id02 && registrosAIntercalar.size() == 0 ){
                tmpFile03.writeInt(tam02);
                tmpFile03.writeInt(id02);
                tmpFile03.write(ba02);

                TempRegistro tmp = new TempRegistro(tam01, id01, ba01);
                registrosAIntercalar.add(tmp);

                
            }else if(registrosAIntercalar.size() > 0){
                registrosAIntercalar.sort(new TempRegistroComparator());

                if(registrosAIntercalar.get(0).id <= id01 && registrosAIntercalar.get(0).id <= id02){
                    TempRegistro tmp = registrosAIntercalar.get(0);
                    registrosAIntercalar.remove(0);

                    tmpFile03.writeInt(tmp.tam);
                    tmpFile03.writeInt(tmp.id);
                    tmpFile03.write(tmp.regBa);

                    TempRegistro tmp01 = new TempRegistro(tam01, id01, ba01);
                    registrosAIntercalar.add(tmp01);

                    TempRegistro tmp02 = new TempRegistro(tam02, id02, ba02);
                    registrosAIntercalar.add(tmp02);
                }

            }

            



        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

    }

   
}


















