import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import services.CRUDService;
import model.Conta;


public class generator {
    public static void main(String[] args) throws IOException {
        CRUDService crud = new CRUDService();

        Random gerador = new Random();

        HashMap<Integer, String> cidadeSet = new HashMap<>();
        HashMap<Integer, String> email = new HashMap<>();
        HashMap<Integer, String> nomes = new HashMap<>();


        cidadeSet.put(1, "Rio de janeiro");
        cidadeSet.put(2, "Belo Horizonte");
        cidadeSet.put(3, "Formiga");
        cidadeSet.put(4, "Rio Negro");
        cidadeSet.put(5, "Sabara");
        cidadeSet.put(6, "Belo Vidal");
        cidadeSet.put(7, "Sabareira");
        cidadeSet.put(8, "Porto Goncalves");
        cidadeSet.put(9, "Tome das letras");
        cidadeSet.put(10, "Rio das perdas");
        cidadeSet.put(11, "Sabero");
        cidadeSet.put(12, "Bela Vista");
        cidadeSet.put(13, "Porto Triste");
        cidadeSet.put(14, "Rio de Maresia");
        cidadeSet.put(15, "Sebareio");
        cidadeSet.put(16, "Tome das Dores");
        cidadeSet.put(17, "Porto Alegre");
        cidadeSet.put(18, "Porto seguro");
        cidadeSet.put(19, "Tome das Alegrias");
        cidadeSet.put(20, "Porto Maria");
        cidadeSet.put(21, "Jose da lamentacoes");

        nomes.put(1, "Joao lima");
        nomes.put(2, "Tulio mendez");
        nomes.put(3, "Marcio teixeira");
        nomes.put(4, "Glaoria maria");
        nomes.put(5, "Nancy gomes");
        nomes.put(6, "Felipe mendez");
        nomes.put(7, "Leo dutra");
        nomes.put(8, "Sabrina torres");
        nomes.put(9, "Layla morais");
        nomes.put(10, "Marcus torres");
        nomes.put(11, "Gustavo lima");
        nomes.put(12, "Ferenanda morais");
        nomes.put(13, "Matheus tomé");
        nomes.put(14, "Lola morais");
        nomes.put(15, "julio basco");
        nomes.put(16, "francisco ferreira");
        nomes.put(17, "Pedro morais");
        nomes.put(18, "paulo nolasco");
        nomes.put(19, "Leila morais");
        nomes.put(20, "Larissa medeiros");
        nomes.put(21, "jorge basco");

        email.put(1, "Joaolima");
        email.put(2, "Tuliondez");
        email.put(3, "Marcioteixeira");
        email.put(4, "Glaorisdfgaria");
        email.put(5, "Nancygomes");
        email.put(6, "Feldfsadfgpemendez");
        email.put(7, "Leodutra");
        email.put(8, "Sabrinatorres");
        email.put(9, "Laylamorais");
        email.put(10, "Marcustorres");
        email.put(11, "Gustavima");
        email.put(12, "Ferenamorais");
        email.put(13, "Matheomé");
        email.put(14, "Lolaorais");
        email.put(15, "julbasco");
        email.put(16, "francierreira");
        email.put(17, "Pedrorais");
        email.put(18, "paulnolasco");
        email.put(19, "Leilarais");
        email.put(20, "Larimedeiros");
        email.put(21, "jorgeasco");
        
        for(int i = 0; i < 100; i++) {
            Conta tmp = new Conta();

            ArrayList<String>  tmp_emails = new ArrayList<>();
            tmp_emails.add(email.get(gerador.nextInt(19)+1)+"@gmail");
            tmp_emails.add(email.get(gerador.nextInt(19)+1)+"@gmail");
            tmp_emails.add(email.get(gerador.nextInt(19)+1)+"@gmail");

            tmp.setCidade(cidadeSet.get(gerador.nextInt(19)+1));
            tmp.setCpf((gerador.nextInt(19)+1)+"");
            tmp.setEmail(tmp_emails);
            tmp.setNomePessoa(nomes.get(gerador.nextInt(19)+1));
            tmp.setNomeUsuario(email.get(gerador.nextInt(19)+1));
            tmp.setSaldoConta(Float.parseFloat(gerador.nextInt(100000)+""));
            tmp.setSenha(gerador.nextInt(100000)+"");    
            
            crud.criar(tmp);
        }


        crud.ler(2 );
        crud.ler(4 );
        crud.ler(11);
        crud.ler(24 );
        crud.ler(53 );

        //crud.deletar(11);

        //crud.atualizar(4);

        System.out.println("Esse é o fim");


        
    }
    
}
