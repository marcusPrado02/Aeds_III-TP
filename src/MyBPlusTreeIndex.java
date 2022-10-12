import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IllegalFormatCodePointException;


public class MyBPlusTreeIndex {

    private RandomAccessFile indexFile;
    private int maxElements;
    private int counter;
    private ArrayList<Reg>  aux;
    private int numPages;

    public class Reg {
        private int key;
        private long address;

        
        public Reg(int key, long address) {
            this.key = key;
            this.address = address;
        }


        public Reg() {
        }


        public int getKey() {
            return key;
        }

        
        public long getAddress() {
            return address;
        }

        public void setKey(int key) {
            this.key = key;
        }
        
        public void setAddress(long address) {
            this.address = address;
        }
    }

        
    class RegComparator implements Comparator<Reg> { 
    
        
        public int compare(Reg r1, Reg r2) 
        { 
            if (r1.key == r2.key) 
                return 0; 
            else if (r1.key > r2.key) 
                return 1; 
            else
                return -1; 
        } 
    } 
    
    public class Page{
        public Reg[] elements;
        public long[] ptrs;
        public boolean isLeaf;

        public Page(int max) {

            this.elements = new Reg[max];
            this.ptrs = new long[max+1];
            this.isLeaf = true;
            
            
            for (int i = 0; i < max; i++) {
                this.elements[i] = new Reg(-1,-1);
            
            }
        }

        public Reg[] firstPage(Reg reg){
            ArrayList<Reg> tmp = new ArrayList<>();

            for (Reg r : elements) {
                tmp.add(r);    
            }

            tmp.add(reg);

            RegComparator rc = new RegComparator();
            tmp.sort(rc);
            for (Reg reg2 : tmp) {
                System.out.println("Esse é o reg no insertPage    key->"+ reg2.key+"    address->"+reg2.address);
            }

            //System.out.println("\n\n\n");
            

            for (int i = 0; i < elements.length; i++) {
                elements[i] = tmp.get(i);
                   
            }

            return elements;
        }

        public Reg[] insertPage(Reg reg){
            ArrayList<Reg> tmp = new ArrayList<>();

            for (Reg r : elements) {
                tmp.add(r);    
            }

            tmp.add(reg);


            //System.out.println("Isso é na funcao insertPAge");
            RegComparator rc = new RegComparator();
            tmp.sort(rc);
            /*for (Reg reg2 : tmp) {
                System.out.println("Esse é o reg no insertPage    key->"+ reg2.key+"    address->"+reg2.address);
            }*/

            //System.out.println("\n\n\n");
            

            for (int i = 0; i < elements.length; i++) {
                elements[i] = tmp.get(i);
                   
            }

            return elements;
        }

        public boolean isFull(){

            for(int i = 0; i < elements.length; i++){
                if(elements[i].key == -1){
                    return false;
                }
            }

            return true;
        }

        public Page[] split(Page ip,Reg reg) {
            //Page ip = new Page(maxElements);
            Page newPage1 = new Page(maxElements);
            Page newPage2 = new Page(maxElements);

            ArrayList<Reg> tmpAl = new ArrayList<>();

            for (Reg r : ip.elements) {
                tmpAl.add(r);
            }

            tmpAl.add(reg);

            RegComparator rc = new RegComparator();
            
            tmpAl.sort(rc);

            for (int i = 0; i < ip.elements.length; i++) {
                ip.elements[i].key = -1;
                ip.elements[i].address = -1;
            }

            int counter = 0;

            for (Reg r : tmpAl) {

                if( counter == (tmpAl.size()/2)+1)
                    counter = 0;

                if( counter < (tmpAl.size()/2)+1 )
                    newPage1.elements[counter] = r;
                
                if( counter >= (tmpAl.size()/2)+1 )
                    newPage1.elements[counter] = r;
                
                counter++;
                
            }


            ip.elements[0].key = newPage2.elements[0].key; 
            
            ip.isLeaf = false;

            Page[] tmp = new Page[3];

            tmp[0] = ip;    tmp[1] = newPage1;  tmp[2] = newPage2;

            return tmp;

        }

        
    }

    public Page readPage(long addr){
        Page p = new Page(maxElements);

        
        try {
            indexFile.seek(addr);
            p.ptrs[0] = indexFile.readLong();
            
            for(int i  = 0; i < p.elements.length; i++){
                
                p.elements[i].key = indexFile.readInt();
                p.elements[i].address = indexFile.readLong();
                p.ptrs[i+1] = indexFile.readLong();

            }

            //p.ptrs[p.ptrs.length-1] = indexFile.readLong();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        /*for (int i = 0; i < p.elements.length; i++) {
            System.out.println("\nPonteiro ->"+p.ptrs[i]+"  key->"+p.elements[i].key+"  address->"+p.elements[i].address +" Outro ptr"+p.ptrs[i+1]);
        }*/

        //System.out.println("\n\n");

        for (int i = 0; i < p.elements.length; i++) {
            if(p.elements[i].key == -1)
                p.isLeaf = false;
            
        }

        return p;


    }

    public long getAddressOriginalFile(int id,long begin ){
        long addr = 0;
        int auxCounter = 0;
        boolean founded = false;
        
            
        Page root = readPage(begin);
        root.isLeaf = false;


        for (int i = 0; i < root.elements.length; i++) {

            if(root.isLeaf == true && root.elements[0].key <= id && root.elements[3].key >= id){
                break;
            }

            if(root.elements[i].key == id && root.isLeaf == false){
                root = readPage(root.ptrs[i+1]);
                i = 0;
                auxCounter = 0;
            }

            if(auxCounter > 0 && root.elements[i].key > id && root.elements[i-1].key  < id && root.isLeaf == false){
                root = readPage(root.ptrs[i-1]);
                i = 0;
                auxCounter = 0;
            }

            auxCounter++;
            
        }

        for (int i = 0; i < root.elements.length; i++) {
            if(root.elements[i].key == id){
                founded = true;
                addr =  root.elements[i].address;
            }
            
        }

        if(founded == false){
            System.out.println("Não foi possivel encontrar na arvore");
        }

       return addr;

        
    }

    public void listAll(){
        Page root = readPage(8);
        long ptr = 0;

        while(root.isLeaf != true){
            ptr = root.ptrs[0];
            if(ptr == -1)
                break;
            root = readPage(ptr);
        }

        for (int i = 0; i < numPages; i++) {
            printPage(root);
            if(root.ptrs[4] == -1)
                break;
            root = readPage(root.ptrs[4]);
        }

    }

    public void listRange(int begin, int end){

        ArrayList<Reg[]> tmpAuxAl = new ArrayList<>();

        Page root = readPage(8);
        long ptr = 0;

        while(root.isLeaf != true){
            ptr = root.ptrs[0];
            if(ptr == -1)
                break;
            root = readPage(ptr);
        }

        for (int i = 0; i < numPages; i++) {
            tmpAuxAl.add(root.elements);
            if(root.ptrs[4] == -1)
                break;
            root = readPage(root.ptrs[4]);
        }

        for (Reg[] r : tmpAuxAl) {

            for (int i = 0; i < r.length; i++) {

                if(r[i].key >= begin && r[i].key <= end){
                    System.out.println(r[i].key+" - "+r[i].address);

                }
            }
            
        }

    }

    public void printPage(Page p){
        for (int i= 0; i < p.elements.length; i++) {
            System.out.println(p.elements[i].key+" - "+p.elements[i].address);
            
        }
    }

    //  This one is the most important
    //
    public void addKeyOnBTree(int idInsert,long address,long begin) throws IOException {

        long addr = 0;
        int auxCounter = 0;
        int id = 0; // isso é so pra parar os erros
        boolean founded = false;
        
            
        Page root = readPage(begin);
        root.isLeaf = false;


        for (int i = 0; i < root.elements.length; i++) {

            if(root.isLeaf == true && root.elements[0].key <= id && root.elements[3].key >= id){
                break;
            }

            if(root.elements[i].key == id && root.isLeaf == false){
                root = readPage(root.ptrs[i+1]);
                i = 0;
                auxCounter = 0;
            }

            if(auxCounter > 0 && root.elements[i].key > id && root.elements[i-1].key  < id && root.isLeaf == false){
                root = readPage(root.ptrs[i-1]);
                i = 0;
                auxCounter = 0;
            }

            auxCounter++;
            
        }

        for (int i = 0; i < root.elements.length; i++) {
            if(root.elements[i].key == id){
                founded = true;
                addr =  root.elements[i].address;
            }
            
        }

        if(founded == false){
            System.out.println("Não foi possivel encontrar na arvore");
        }
        


    }

    public void deleteKey(int id,long begin){
       
      
    }

    public MyBPlusTreeIndex() {
        try {
            this.indexFile = new RandomAccessFile("bplusIndexFile.db", "rw");
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        };

        this.maxElements = 4;
        this.counter = 0;
        this.aux = new ArrayList<>(); 
        this.numPages = 0;

        
    }



    public void generateIndexFile(){
        Reg register = new Reg();
        Reg aux = new Reg();

        char lapideBusca = ' ';
        
        byte[] ba;
        int len;int auxInt = 0;
        long pos0 = 0; long pos1 = 0;long pos2 = 0;

        try  {
            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            auxInt = arq.readInt();

            //System.out.println("Chegou aqui ");
            pos0 = arq.getFilePointer();
            
            while (pos0 != arq.length()) {

                pos2 = arq.getFilePointer();
                int tam = arq.readInt();
                pos1 = arq.getFilePointer();
                int idConta = arq.readInt();
                char lapide = arq.readChar();
                arq.seek(pos1);
                ba = new byte[tam];
                arq.read(ba);
                pos0 += ba.length;

                register.setKey(idConta);
                register.setAddress(pos2);
                //conta.fromByteArray(ba);
                //System.out.println(conta.toString());
                
                
               // System.out.println(idConta);
                
                //if(conta.getLapide() != '*') {
                if(lapide != '*'){
                    //System.out.println("idConta ->"+register.key+"  address ->"+register.address);
                    insertBTree(register.key, register.address);
                }

                
            }

            
            
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //System.out.println(e);
        }

    }


    
    private void insertBTree(int id, long address) throws IOException {
        
  

            //System.out.println("Chegou aqui insertBtree");
            indexFile.seek(0);
            long pos0 = indexFile.getFilePointer();
            long pos1 = 0,pos2 = 0,pos3 = 0,pos4 = 0,pos5 = 0; 
            long pageAddr;

            long posAfterRoot = 8;

            //System.out.println("Esse é o tamanho do arquivo ->"+indexFile.length());
            //System.out.println("\nRegistro    id->"+id+"  address original->"+address);
            
            if( counter < maxElements){
                if(counter == 0){
                    indexFile.writeLong(16);
                    posAfterRoot = 16;
                }

                aux.add(new Reg(id,address) );

                indexFile.seek(indexFile.length());

                if(aux.size() == 4){
                    aux.sort(new RegComparator());

                    indexFile.writeLong(-1);
                    for (Reg r : aux) {
                        indexFile.writeInt(r.key);
                        indexFile.writeLong(r.address);
                        indexFile.writeLong(-1);
                    }
                    
                    //aux.clear();
                }
                counter++;

            } else if(counter == maxElements){

                

                indexFile.seek(indexFile.length());

                pos2 = indexFile.getFilePointer();
                

                

                aux.add(new Reg(id ,address));

                aux.sort(new RegComparator());

                numPages = 2;



                /*
                 * Escrevemos o primeiro ponteiro da nova pagina apontado 
                 * pra nulo
                 */
                indexFile.writeLong(-1);
                pos5 = indexFile.getFilePointer();
                
                

                /*
                 * Para cada chave (na pagina antes de ser partida) 
                 * que estiver ate a metade da pagina, escrevemos
                 * a seu valor de key e address, e também um ponteiro
                 * para nulo
                 */
                for (int i = 0; i < maxElements; i++) {

                    if(i < (maxElements/2)){
                        indexFile.writeInt(aux.get(i).key);
                        indexFile.writeLong(aux.get(i).address);
                        indexFile.writeLong(-1);

                    } else {
                        indexFile.writeInt(-1);
                        indexFile.writeLong(-1);
                        pos5 = indexFile.getFilePointer();
                        indexFile.writeLong(-1);
                    }
                }


                /*
                 * Armazenamos a posicao da pagina a direita da que foi partida
                 * adicionamos um ponteiro no fim da pagina da esquerda que aponta
                 * pra pagina da direita e voltamos para pos4
                 */
                pos4 = indexFile.getFilePointer();
                indexFile.seek(pos5);
                indexFile.writeLong(pos4);
                indexFile.seek(pos4);
                


                /*
                 * Para todo o numero com posicao maior ou igual a metade+1,
                 * escrevemos seu valor de key, seu address e um ponteiro nulo 
                 */
                indexFile.writeLong(-1);
                for (int i = (maxElements/2); i < maxElements; i++) {

                
                    indexFile.writeInt(aux.get(i).key);
                    indexFile.writeLong(aux.get(i).address);
                    indexFile.writeLong(-1);

                }


                /*
                 * Completamos os espacos em branco na pagina
                 */
                for (int i = 0; i < (maxElements/2); i++) {
                    indexFile.writeInt(-1);
                    indexFile.writeLong(-1);
                    indexFile.writeLong(-1);
                    
                }



                /*
                 * Aqui voltamos para depois do root para reescrever 
                 * a pagina que foi partida 
                 */
                indexFile.seek((posAfterRoot));
                

                /*
                 * Escrevemos o endereco da nova pagina a esquerda
                 */
                indexFile.writeLong(pos2);


                /*
                 * Escrevemos o valor chave do meio da antiga
                 * pagina e mas nao escrevemos o seu endereco
                 * no arquivo de dados,já que não é uma folha
                 */
                indexFile.writeInt(aux.get((maxElements/2)).key);
                indexFile.writeLong(-1);


                /*
                 * Escrevemos o endereco da nova pagina a direita
                 */
                indexFile.writeLong(pos4);
                    
                /*
                 * Completamos os espacos em branco na pagina
                 */
                for (int i = 0; i < maxElements-1; i++) {
                    indexFile.writeInt(-1);
                    indexFile.writeLong(-1);
                    indexFile.writeLong(-1);
                    
                }

                /*
                 * Escecrevemos o ultimo ponteiro nulo da pagina 
                 */
                indexFile.writeLong(-1);
                counter++;
                
                //indexFile.seek(posAfterRoot);
                /*System.out.println("Page root");
                readPage(posAfterRoot);

                System.out.println("\n\nPage left");
                long tmp_addrToRead = pos2;
                readPage(tmp_addrToRead);

                
                System.out.println("\n\nPage right");
                tmp_addrToRead = pos4;
                readPage(tmp_addrToRead);*/

                //System.out.println("\n\nProcurando 3");
                //System.out.println("Retorno da funcao get ->"+getAddressOriginalFile(3,8));

                //System.out.println("Listar todos na folha");
                //listAll();
                

            } else if(counter > maxElements){                
                
                addKeyOnBTree(id,address,8);

            }
            

        

        
    }

   
    
}
