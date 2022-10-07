package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Conta {
    private int idConta;
    private char lapide;
    private String nomePessoa ;
    private ArrayList<String> email;
    private String nomeUsuario;
    private String senha;
    private String cpf;
    private String cidade;
    private int transferenciasRealizadas;
    private float saldoConta;

    public Conta() {
        this.email = new ArrayList<String>();
    }
    

    public String toString(){
        DecimalFormat df= new DecimalFormat("0.00");
        
        return "\nIDConta: "+idConta +
                "\nNome da Pessoa: "+nomePessoa +
                "\nEmails: "+ email + 
                "\nNome do Usuário:"+nomeUsuario +
                "\nIDConta: "+idConta + // não mostrar senha
                "\nCPF: "+cpf +
                "\nCidade: "+cidade +
                "\nTransferencias Realizadas: "+transferenciasRealizadas +
                "\nSaldo da Conta: "+ df.format(saldoConta);
    }


    public byte[] toByteArray() throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idConta); 
        dos.writeChar(' ');

        dos.writeInt(nomePessoa.length());
        dos.writeUTF(nomePessoa);

        dos.writeInt(email.size());
        
        for (String cada_email : email) {
            dos.writeInt(cada_email.length());
            dos.writeUTF(cada_email);
        }

        dos.writeInt(nomeUsuario.length());
        dos.writeUTF(nomeUsuario);

        dos.writeInt(senha.length());
        dos.writeUTF(senha);

        dos.writeInt(cpf.length());
        dos.writeUTF(cpf);

        dos.writeInt(cidade.length());
        dos.writeUTF(cidade);

        dos.writeInt(transferenciasRealizadas);
        dos.writeFloat(saldoConta);


        return baos.toByteArray();
    }


    public void fromByteArray(byte ba[]) throws IOException{

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.idConta=dis.readInt(); 
        this.lapide= dis.readChar();

        int lenNome = dis.readInt();
        this.nomePessoa=dis.readUTF();

        for(int i = dis.readInt(); i > 0; i-- ) {
            int size = dis.readInt();
            String tmp = dis.readUTF();
            this.email.add(tmp);
            //System.out.println(email);

        }

        int lenNomeUsuario = dis.readInt();
        this.nomeUsuario=dis.readUTF();

        int lenSenha = dis.readInt();
        this.senha=dis.readUTF();

        int lenCpf = dis.readInt();
        this.cpf=dis.readUTF();

        int lenCidade = dis.readInt();
        this.cidade=dis.readUTF();

        
        this.transferenciasRealizadas=dis.readInt();
        this.saldoConta=dis.readFloat();

    }

    public int getIdConta() {
        return idConta;
    }




    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }




    public String getNomePessoa() {
        return nomePessoa;
    }




    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }




    public ArrayList<String> getEmail() {
        return email;
    }




    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }




    public String getNomeUsuario() {
        return nomeUsuario;
    }




    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }




    public String getSenha() {
        return senha;
    }




    public void setSenha(String senha) {
        this.senha = senha;
    }




    public String getCpf() {
        return cpf;
    }




    public void setCpf(String cpf) {
        this.cpf = cpf;
    }




    public String getCidade() {
        return cidade;
    }




    public void setCidade(String cidade) {
        this.cidade = cidade;
    }




    public int getTransferenciasRealizadas() {
        return transferenciasRealizadas;
    }




    public void setTransferenciasRealizadas(int transferenciasRealizadas) {
        this.transferenciasRealizadas = transferenciasRealizadas;
    }




    public float getSaldoConta() {
        return saldoConta;
    }




    public void setSaldoConta(float saldoConta) {
        this.saldoConta = saldoConta;
    }




    public char getLapide() {
        return lapide;
    }




    public void setLapide(char lapide) {
        this.lapide = lapide;
    }
}


