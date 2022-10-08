package services;

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
    
	
        private int numNodes = 0; 
        private int M;
        private Node root;       
        private int height;      
        private RandomAccessFile output;
        private int keySize;
        private RandomAccessFile input;

        private class Entry {
                private String key;   
                private long addressFileValue;      
                private long next; 

                public Entry(String key, long addressFileValue, long next) {
                        this.key  = key;
                        this.addressFileValue  = addressFileValue;
                        this.next = next;
                }
                
                
                Node getNext() throws IOException {
                        if(next > 0) {
                        return new Node(next);
                        }
                        return null;
                }
        }

        private class Node {
                private int numChild;
                private Entry[] childs = new Entry[M];

                boolean intern;
                long offset;


                Node(int k) {
                        numChild = k;
                        numNodes++;
                        offset = numNodes * 1024;
                }


                private Node(int k, long offset){
                        numChild = k;
                        this.offset = offset;
                }

                private Node() {
                        int i;
                        this.offset = offset;
                        output.seek(offset);
                        internal = output.readBoolean();
                        numChildren = output.readInt();
                        for( i = 0 ; i < noOfChildren; i++){
                            byte[] b = new byte[keySize];
                            output.read(b, 0, keySize);
                            // Populate the children according to the flag.
                            children[i] = internal ? new Entry(new String(b), -1, output.readLong()) : new Entry(new String(b), output.readLong(), -1);
                        }
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