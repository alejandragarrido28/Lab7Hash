/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7hash;

/**
 *
 * @author Gabriel
 */
import java.io.RandomAccessFile;

public class PSNUsers {
    
    private RandomAccessFile file;
    private HashTable users;

    public PSNUsers(String nombreArchivo) {
        try {
            file = new RandomAccessFile(nombreArchivo, "rw");
            users = new HashTable();
            reloadHashTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadHashTable() {
        try {
        file.seek(0);
        users = new HashTable();

            while (file.getFilePointer() < file.length()) {

                long pos = file.getFilePointer();

                String username = file.readUTF();
                int puntos = file.readInt();
                int trofeos = file.readInt();
                boolean activo = file.readBoolean();

                if (activo) {
                    users.add(username, pos);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
