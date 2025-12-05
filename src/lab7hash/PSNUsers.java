/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7hash;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class PSNUsers {
    private RandomAccessFile trophiesFile;
    private RandomAccessFile file;
    private HashTable users;

    public PSNUsers(String nombreArchivo) {
        try {
            file = new RandomAccessFile(nombreArchivo, "rw");
            trophiesFile = new RandomAccessFile("trophies.psn", "rw");
            users = new HashTable();
            reloadHashTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class TrophyInfo {
        public String type, game, desc, date;
        public byte[] image;
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
    
    public boolean addUser(String username) {
        try {
            long p = users.search(username);
            if (p != -1) {
                return false;
            }

            file.seek(file.length());
            long pos = file.getFilePointer();

            file.writeUTF(username);
            file.writeInt(0);
            file.writeInt(0);
            file.writeBoolean(true);

            users.add(username, pos);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deactivateUser(String username) {
        try {
            long pos = users.search(username);

            if (pos == -1) {
                return false;
            }

            file.seek(pos);

            String u = file.readUTF();
            int puntos = file.readInt();
            int t = file.readInt();

            file.writeBoolean(false);

            users.remove(username);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addTrophieTo(String username, String trophyGame, String trophyName, Trophy type, byte[] imageBytes) {
        try {
            long pos = users.search(username);
            if (pos == -1) {
                return false;
            }

            trophiesFile.seek(trophiesFile.length());

            trophiesFile.writeUTF(username);
            trophiesFile.writeUTF(type.name());
            trophiesFile.writeUTF(trophyGame);
            trophiesFile.writeUTF(trophyName);

            String fecha = new java.util.Date().toString();
            trophiesFile.writeUTF(fecha);

            trophiesFile.writeInt(imageBytes.length);
            trophiesFile.write(imageBytes);

            file.seek(pos);

            file.readUTF();

            int puntos = file.readInt();
            int trofeos = file.readInt();

            long p2 = file.getFilePointer() - 8;

            file.seek(p2);
            file.writeInt(puntos + type.points);
            file.writeInt(trofeos + 1);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String playerInfo(String username) {
        StringBuilder info = new StringBuilder();
        long userPos = users.search(username);

        if (userPos == -1) {
            return "Error: Usuario '" + username + "' no encontrado en el sistema o está desactivado.";
        }

        try {
            file.seek(userPos);
            
            String uName = file.readUTF();
            int points = file.readInt();
            int count = file.readInt();
            boolean active = file.readBoolean();
            
            info.append("--- Información del Jugador ").append(uName).append(" ---\n");
            info.append(String.format("Puntos Totales: %d\n", points));
            info.append(String.format("Trofeos Totales: %d\n", count));
            info.append(String.format("Estado Activo: %s\n", active ? "Sí" : "No (BORRADO)"));
            info.append("\n");
            
            info.append("--- Lista de Trofeos ---\n");
            trophiesFile.seek(0);
            boolean foundTrophy = false;

            while (trophiesFile.getFilePointer() < trophiesFile.length()) {
                
                String tUsername = trophiesFile.readUTF();
                
                int imageLength = trophiesFile.readInt();
                
                if (tUsername.equals(username)) {
                    foundTrophy = true;
                    trophiesFile.skipBytes(imageLength); 
                } else {
                    trophiesFile.skipBytes(imageLength);
                }
            }
            
            if (!foundTrophy) {
                info.append("El usuario aún no tiene trofeos registrados.\n");
            }
            
        } catch (Exception e) {
            info.append("\nError al procesar la información del archivo: ").append(e.getMessage());
            e.printStackTrace();
        }
        return info.toString();
    }
    

    public void closeFiles() {
        try {
            if (file != null) {
                file.close();
            }
            if (trophiesFile != null) {
                trophiesFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<TrophyInfo> getTrophiesOf(String username) {
        ArrayList<TrophyInfo> list = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile("trophies.psn", "r")) {
            while (raf.getFilePointer() < raf.length()) {

                String u = raf.readUTF();
                String type = raf.readUTF();
                String game = raf.readUTF();
                String desc = raf.readUTF();
                String date = raf.readUTF();

                int size = raf.readInt();
                byte[] img = new byte[size];
                raf.readFully(img);

                if (u.equals(username)) {
                    TrophyInfo t = new TrophyInfo();
                    t.type = type;
                    t.game = game;
                    t.desc = desc;
                    t.date = date;
                    t.image = img;
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}