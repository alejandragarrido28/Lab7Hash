/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7hash;

public class HashTable {
    
    private Entry head;

    public HashTable() {
        head = null;
    }

    public void add(String username, long pos) {
        Entry nuevo = new Entry(username, pos);

        if (head == null) {
            head = nuevo;
        } else {
            Entry aux = head;
            while (aux.next != null) {
                aux = aux.next;
            }
            aux.next = nuevo;
        }
    }

    public long search(String username) {
        Entry aux = head;

        while (aux != null) {
            if (aux.username.equals(username)) {
                return aux.position;
            }
            aux = aux.next;
        }

        return -1;
    }

    public void remove(String username) {
        if (head == null) {
            return;
        }

        if (head.username.equals(username)) {
            head = head.next;
            return;
        }

        Entry prev = head;
        Entry aux = head.next;

        while (aux != null) {
            if (aux.username.equals(username)) {
                prev.next = aux.next;
                return;
            }
            prev = aux;
            aux = aux.next;
        }
    }
}
