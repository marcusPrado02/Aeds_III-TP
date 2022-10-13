import java.io.IOException;
import java.io.RandomAccessFile;

public class testHash {
    
    public static void main(String[] args) throws Exception {
        HashEstendido he = new HashEstendido(4, "diretorioHash.db", "cestoHash.db");
        
        generateIndexFile(he);

        System.out.println("Read 1  "+he.read(1));
        System.out.println("Read 2  "+he.read(24));
        System.out.println("Read 3  "+he.read(12));

        System.out.println("Fim do programa.\nSucesso adquirido");
    }    

    public static void generateIndexFile(HashEstendido he) throws Exception{
        char lapide = ' ';
        
        byte[] ba;
        int len;
        long pos0 = 0; long pos1 = 0;
       
        
        try  {
            RandomAccessFile arq = new RandomAccessFile("contas.db", "rw");
            int aux = arq.readInt();

            pos0 = arq.getFilePointer();
            
            
            while (pos0 != arq.length()) {
                

                int tam = arq.readInt();
                pos1 = arq.getFilePointer();

                /*
                 * Lemos o id da Conta e lemos a lapide,que sera
                 * utilizada para adicionar ou nao um indice na 
                 *  arvore B+
                 */
                int idConta = arq.readInt();
                lapide = arq.readChar();
                //System.out.println("ID->"+idConta+" pos0->"+pos0);


                /*
                 * Se o registro não tiver sido deletado
                 * inserimos o seu id e endereco na 
                 * árvore B+
                 */
                if(lapide != '*' ) {
                  
                  he.create(idConta, pos0);

                }
                
                /*
                 * Voltamos para a posicao antes de ler o Id
                 */
                arq.seek(pos1);


                
                /*
                 * Pulamos para a posição do próximo registro e 
                 * atualizamos o valor da posição atual dentro do 
                 * arquivo de dados marcada por pos0
                 */
                arq.skipBytes(tam);
                pos0 = arq.getFilePointer();

                
            }

            
            
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //System.out.println(e);
        }

    }
}
