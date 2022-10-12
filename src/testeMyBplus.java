public class testeMyBplus {
    public static void main(String[] args) {
        MyBPlusTreeIndex bp = new MyBPlusTreeIndex();


        bp.generateIndexFile();

        //bp.listAll();
        bp.getAddressOriginalFile(23, 8);

    }
}
    
