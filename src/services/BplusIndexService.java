package services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/*

Orientações para a criação do arquivo de índices usando Árvore B+:

○ O arquivo de índices deve usar a estrutura de Árvore B+, usando como chave o campo
id. Utilize Árvore B+ de ordem 05.

○ O arquivo  de  índices deve  conter o  id  (da  conta  bancária)  e  a  posição  do   registro
(referente a esse id) no arquivo de dados.

○ Sempre que acontecerem alterações no arquivo de dados, novas alterações devem ser
feitas no arquivo de índices, mantendo sempre a coerência entre esses arquivos.

○ Deve existir a possibilidade de realizar buscas por 1 ou vários ids usando a estrutura
de índices de Árvore B+.

 */



public class BplusIndexService {
    
	
        private int  order;                 
        private int  maxElements;          
        private int  maxChildren;           
        private RandomAccessFile arq;   
        private String arqName;

        private int  key1Aux;
        private int  key2Aux;
        private long pageAux;
        private boolean cresceu;
        private boolean diminuiu;


        private class Page {
                protected int order;
                protected int maxElements;
                protected int maxChildren;
                protected int n;
                protected ArrayList<Integer> keys1;
                protected ArrayList<Integer> keys2;
                protected long next;
                protected ArrayList<Long> childrenPtrs;
                protected int tamRegs;
                protected int tamPage;

                public Page(int a){
                        this.order = 5;
                        this.n = 5;
                        this.maxChildren = 5;
                        this.maxElements = 5-1;
                        this.keys1 = new ArrayList<Integer>();
                        this.keys2 = new ArrayList<Integer>();
                        this.childrenPtrs = new ArrayList<Long>();
                        this.next = -1;


                        for (int index = 0; index < maxElements; index++) {
                                keys1.add(0);
                                keys2.add(0);
                                childrenPtrs.add((long) 0);
                        }

                        this.tamRegs = 4;
                        this.tamPage = 4 + maxElements*tamRegs + maxChildren*8 + 16;

                }



                protected byte[] pageToByte() throws IOException {
                        ByteArrayOutputStream ba = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(ba);

                        dos.writeInt(this.n);

                        int aux = 0;

                        for(int i  = 0; i < n; i++){
                                dos.writeLong(childrenPtrs.get(i));
                                dos.writeInt(keys1.get(i));
                                dos.writeInt(keys1.get(i));
                                aux = i;
                        }

                        dos.writeLong(childrenPtrs.get(aux));

                        byte[] emptyRegs = new byte[this.tamRegs];

                        while(aux < maxElements){
                                dos.write(emptyRegs);
                                dos.writeLong(childrenPtrs.get(aux+1));
                                aux++;
                        }


                        dos.writeLong(next);

                        return ba.toByteArray();
                }


                public void byteToPage(byte[] bf) throws IOException {
                        ByteArrayInputStream ba = new ByteArrayInputStream(bf);
                        DataInputStream dis = new DataInputStream(ba);

                        this.n = dis.readInt();

                        int aux = 0;

                        for(int i  = 0; i < n; i++){
                                childrenPtrs.add(i,dis.readLong());
                                keys1.add(i,dis.readInt());
                                keys2.add(i,dis.readInt());
                                aux = i;
                        }

                        childrenPtrs.add(aux,dis.readLong());
                        this.next = dis.readLong();
                        
                }

                
        }

        public boolean empty() throws IOException {
                long raiz;
                arq.seek(0);
                
                raiz = arq.readLong();
                return raiz == -1;
        }

        public BplusIndexService() throws IOException {

                order = 5;
                maxElements = 4;
                maxChildren = 5;
                
               
                arq = new RandomAccessFile("bplusIndice.db","rw");
                if(arq.length()<8){ 
                    arq.writeLong(-1);
                } 
        }

        public ArrayList<Integer> read(int c1) throws IOException {
        
                long root;
                arq.seek(0);
                root = arq.readLong();
                
                if(root != -1){
                    return read(c1,root);
                } else{
                    return new ArrayList<Integer>();
               }
        }


        private ArrayList<Integer> read( int key1, long page) throws IOException {
                if(page == -1){
                        return new ArrayList<Integer>();
                }

                this.arq.seek(page);
                Page p = new Page(this.order);
                byte[] bf = new byte[p.tamPage];
                arq.read(bf);
                p.byteToPage(bf);

                int aux = 0;
                
                while( aux < p.n && key1 > p.keys1.get(aux)){ aux++; }


                if(aux < p.n && p.childrenPtrs.get(0) == -1 && key1 == p.keys1.get(aux)) {
                
                        ArrayList<Integer> ls = new ArrayList<>();
                        
                        while(key1 <= p.keys1.get(aux)) {
                                if(key1 == p.keys1.get(aux)){
                                        ls.add(p.keys2.get(aux));
                                }
                                aux++;

                                if(aux == p.n) {
                                        if(p.next == -1){
                                                break;
                                        }
                                        arq.seek(p.next);
                                        arq.read(bf);
                                        p.byteToPage(bf);
                                        aux = 0;
                                }
                        }

                        return ls;

                } else if (aux == p.n && p.childrenPtrs.get(0) == -1) {
                        
                        if(p.next == -1){
                                return new ArrayList<>();
                        }

                        arq.seek(p.next);
                        arq.read(bf);
                        p.byteToPage(bf);


                        aux = 0;

                        if(key1 <= p.keys1.get(0)) {
                                ArrayList<Integer> ls = new ArrayList<>();


                                while(key1 <= p.keys1.get(aux)) {

                                        if(key1 == p.keys1.get(aux)){
                                                ls.add(p.keys2.get(aux));
                                        }

                                        aux++;


                                        if(aux == p.n) {
                                                if(p.next == -1){
                                                        break;
                                                        arq.seek(p.next);
                                                        arq.read(bf);
                                                        p.byteToPage(bf);
                                                        aux = 0;
                                                }
                                        }
                                }

                                return ls;

                        } else {

                                return new ArrayList<>();
                        
                        }

                        if(aux == p.n || key1 <= p.keys1.get(aux)){
                                return read(key1, p.childrenPtrs.get(aux));
                        
                        } else {
                                return read(key1, p.childrenPtrs.get(aux+1));
                        
                        }

                }

                



        }
        
        public boolean create(int c1, int c2) throws IOException {

                if(c1 < 0  || c2 < 0) {
                        System.out.println( "keys .get(ão podem ser negativas" );
                        return false;
                }

                arq.seek(0);       
                long pageAddress;
                pageAddress = arq.readLong();

                key1Aux = c1;
                key2Aux = c2;

                pageAux = -1;
                cresceu = false;

                boolean works = create(pageAddress);

                if(cresceu) {
            

                        Page newPage = new Page(order);
                        newPage.n = 1;
                        newPage.keys1.add(0, key1Aux);
                        newPage.keys2.add(0, key2Aux);
                        newPage.childrenPtrs.add(0, pageAddress);
                        newPage.childrenPtrs.add(1, pageAux);
                        
                        
                        arq.seek(arq.length());
                        long root = arq.getFilePointer();
                        arq.write(newPage.pageToByte());
                        arq.seek(0);
                        arq.writeLong(root);
                }
                    
                return works;
        }

        private boolean create(long page) throws IOException {
                
                if(page == -1) {
                        cresceu = true;
                        pageAux = -1;
                        return false;
                }

                arq.seek(page);
                Page p = new Page(order);
                byte[] bf = new byte[p.tamPage];
                arq.read(bf);
                p.byteToPage(bf);


                int i = 0;
                while(i < p.n && (key1Aux > p.keys1.get(i) || (key1Aux == p.keys1.get(i) && key2Aux > p.keys2.get(i)))) {
                        i++;
                }


                if(i < p.n && p.childrenPtrs.get(0) == -1 && key1Aux == p.keys1.get(i) && key2Aux == p.keys2.get(i)) {
                        cresceu = false;
                        return false;
                }


                boolean inserido;

                if(i == p.n || key1Aux < p.keys1.get(i) || (key1Aux == p.keys1.get(i) && key2Aux < p.keys2.get(i))){

                        inserido = create(p.childrenPtrs.get(i));
                }else{

                        inserido = create(p.childrenPtrs.get(i+1));
                }


                if(!cresceu){
                        return inserido;
                }


                if(p.n < maxElements) {

                                
                        for(int j = p.n; j > i ; j--) {
                                p.keys1.add(j,  p.keys1.get(j-1));
                                p.keys2.add(j,  p.keys2.get(j-1));
                                p.childrenPtrs.add(j+1,  p.childrenPtrs.get(j));
                        }
                        
                        
                        p.keys1.add(i, key1Aux);
                        p.keys2.add(i, key2Aux);
                        p.childrenPtrs.add(i+1, pageAux);
                        p.n++;
                        
                        
                        arq.seek(page);
                        arq.write(p.pageToByte());
                        
                        
                        cresceu = false;
                        return true;
                }

                Page newp = new Page(order);
        
                
                int m = maxElements/2;

                for(int j=0; j < (maxElements - m ); j++) {    
                        
                        
                        newp.keys1.add(j , p.keys1.get(j+m));
                        newp.keys2.add(j , p.keys2.get(j+m));   
                        newp.childrenPtrs.add(j+1,  p.childrenPtrs.get(j+m+1));  
                        
                        
                        p.keys1.add(j+m,  0);
                        p.keys2.add(j+m,  0);
                        p.childrenPtrs.add(j+m+1,  (long) -1);
                }

                newp.childrenPtrs.add(0, p.childrenPtrs.get(m));
                newp.n = maxElements-m;
                p.n = m;


                
                if(i <= m) {   
                
                        
                        for(int j=m; j>0 && j>i; j--) {
                                p.keys1.add(j, p.keys1.get(j-1));
                                p.keys2.add(j, p.keys2.get(j-1));
                                p.childrenPtrs.add(j+1, p.childrenPtrs.get(j));
                        }
                        
                        
                        p.keys1.add(i, key1Aux);
                        p.keys2.add(i, key2Aux);
                        p.childrenPtrs.add(i+1, pageAux);
                        p.n++;
                        
                        
                        if(p.childrenPtrs.get(0) == -1) {
                                key1Aux = newp.keys1.get(0);
                                key2Aux = newp.keys2.get(0);

                        }else {
                                key1Aux = p.keys1.get(p.n-1);
                                key2Aux = p.keys2.get(p.n-1);
                                p.keys1.add(p.n-1 , 0);
                                p.keys2.add(p.n-1 , 0);
                                p.childrenPtrs.add(p.n , -1);
                                p.n--;
                        }

                } else {

                        int j;

                        for(j = maxElements - m; j > 0 && (key1Aux < newp.keys1.get(j-1) || (key1Aux == newp.keys1.get(j-1) && key2Aux < newp.keys2.get(j-1) ); j--) {
                                
                                newp.keys1.add(j, newp.keys1.get(j-1));
                                newp.keys2.add(j, newp.keys2.get(j-1));
                                newp.childrenPtrs.add(j+1, newp.childrenPtrs.get(j));
                        
                        }

                        newp.keys1.add(j, key1Aux);
                        newp.keys2.add(j, key2Aux);
                        newp.childrenPtrs.add(j+1, pageAux);
                        newp.n++;
        
                        
                        key1Aux = newp.keys1.get(0);
                        key2Aux = newp.keys2.get(0);
                        
                        
                        if(p.childrenPtrs.get(0) != -1) {

                                for(j = 0 ; j < newp.n-1 ; j++) {
                                
                                        newp.keys1.add(j, newp.keys1.get(j+1));
                                        newp.keys2.add(j, newp.keys2.get(j+1));
                                        newp.childrenPtrs.add(j, newp.childrenPtrs.get(j+1));
                                
                                }
                                
                                newp.childrenPtrs.add(j, newp.childrenPtrs.(j+1));
                                
                                
                                newp.keys1.add(j, 0);
                                newp.keys2.add(j, 0);
                                newp.childrenPtrs.add(j+1, (long) -1);
                                newp.n--;
                        }
        
                }
                
                // Se a página era uma folha e apontava para outra folha, 
                // então atualiza os ponteiros dessa página e da página nova
                if(p.childrenPtrs[0]==-1) {
                        newp.proxima=p.proxima;
                        p.proxima = arquivo.length();
                }
        
                // Grava as páginas no arquivos arquivo
                pageAux = arquivo.length();
                arquivo.seek(pageAux);
                arquivo.write(newp.getBytes());
        
                arquivo.seek(pagina);
                arquivo.write(p.getBytes());
                
                return true;
                }
        }
        

        


        public void gerarArquivoDeIndice() {
                
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

                                address = arq.getFilePointer();
                                int tam = arq.readInt();

                                int id = arq.readInt();
                                char lapide = arq.readChar();

                                if(lapide != '*'){
                                        put(id,address);
                                        
                                }

                                skipPos0 = skipPtr.skipBytes(tam);
                                pos0 = skipPos0;
    
                        

                        }
                        
                        arq.close();
                        skipPtr.close();

                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        //System.out.println(e);
                }

        }

        
        private void put(int id, long address) {
                
                if( id < 0 || address < 0)  throw new IllegalArgumentException("Argumento  id ou address inválido");

                Node newNode = insert(root, id , address, height);

                if (newNode == null) return;

                Node tmp = new Node(2, 1024);
                
                

        }

        private void delete(int id, long address) {
        }

        public void atualizarArquivoIndice(){

        }
        
        public void escreverPagina(){

        }

    


}