/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir_5_ss16;

import java.math.BigInteger;
import java.util.BitSet;

/**
 *
 * @author Fabian Kopp
 */
public final class Signature {

    protected BitSet blockSig = new BitSet(64);
    protected int docId;
    //primzahlehn
    private static int[] p = {9871, 9883, 9887, 9901, 9907, 9923, 9929, 9931,
        9941, 9949, 9967, 9973};
    //{997,991,983,977,971,967,953,947,941,937,929,919};

    /**
     * Konsturktor für eine Signatur
     *
     * @param input Inhalt
     * @param id Dokument ID
     *
     */
    public Signature(String input, int id) {
        this.docId = id;
        BitSet bs = doubleHash(input);
        if (bs.cardinality() == 12) {
            this.blockSig.or(bs);
        } else {
            System.out.println("ERROR: " + bs.cardinality());
            System.out.println(input);
        }
    }

    /**
     * Konsturktor für eine Signatur
     *
     * @param blocksig hashed n strings
     * @param id Dokument ID
     *
     */
    public Signature(BitSet blocksig, int id) {
        this.blockSig = blocksig;
        this.docId = id;
    }

    /**
     *
     * @param inString String, der auf eine signatur abgebildet werden soll
     *
     * @return ArrayList mit vermutlich relevanten Dokumenten
     *
     */
    public BitSet doubleHash(String inString) {
        BitSet s = new BitSet(64);
        BigInteger mod = BigInteger.valueOf(64);
        for (int k = 0; k < p.length; k++) {
            BigInteger hash1 = BigInteger.ZERO;
            for (int i = 0; i < inString.length(); i++) {
                byte[] b = String.valueOf(inString.charAt(i)).getBytes();
                hash1 = hash1.add(new BigInteger(b));
                hash1 = hash1.multiply(BigInteger.valueOf(p[k]));
                hash1 = hash1.mod(mod);
            }
            s.or(setBit(s, hash1.intValue()));
        }
        if (s.cardinality() != 12) {
            System.out.println("card != 12");
        }
        return s;
    }

    /**
     *
     * @param s BitSet der verändert weredn soll
     * @param pos offset im set das gesetzt werden soll
     *
     * @return BitSet mit gesetzten Bit
     */
    private BitSet setBit(BitSet s, int pos) {
        if (!s.get(pos)) {
            s.set(pos);
        } else {
            //rehash
            for (int n = 1; n < 63; n++) {
                int newPos = (pos + n) % 64;
                //wenn s an der stelle newPos false ist dann setzt das bit
                if (!s.get(newPos)) {
                    s.set(newPos);
                    break;
                }
            }
        }
        return s;
    }

    public BitSet getBlockSig() {
        return blockSig;
    }

    public void setBlockSig(BitSet blockSig) {
        this.blockSig = blockSig;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

}
