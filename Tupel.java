/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir_5_ss16;

public class Tupel<A, B> {

    private A a;
    private B b;

    public Tupel(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getFst() {
        return a;
    }

    public B getSnd() {
        return b;
    }

    public void setFst(A a) {
        this.a = a;
    }

    public void setSnd(B b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return ("(" + a.toString() + "," + b.toString() + ")");
    }
}
