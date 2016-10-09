/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir_5_ss16;

/**
 * Word stemming with Porter
 *
 * @author Fabian Kopp
 */
public class PorterStemmer {

    protected String s;

    public PorterStemmer() {
        this.s = "";
    }

    /**
     * Stem everything in the buffer
     */
    public void stem() {
        step1();
        step2();
        step3();
        step4();
        step5();
    }

    @Override
    public String toString() {
        return this.s;
    }

    /**
     * stemm's words
     *
     * @param input String
     * @return stemmed string
     */
    public String stemThis(String input) {
        StringBuilder sb = new StringBuilder();
        PorterStemmer ps = new PorterStemmer();
        //get every word in a text
        for (String word : input.toLowerCase().split(" ")) {
            //add every char in a word into the buffer

            ps.add(word);

            //stem every word
            ps.stem();
            sb.append(ps.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * add char to buffer
     *
     * @param inString Input String
     */
    public void add(String inString) {
        if (!inString.isEmpty()) {
            this.s = inString;
        }
    }

    /**
     *
     * @return number of repeats of vc in a word
     */
    protected int getM() {
        int i = 0;
        int n = 0;
        while (i < s.length()) {
            for (; i < s.length(); i++) {
                if (i > 0) {
                    if (isVowel(s.charAt(i), i)) {
                        break;
                    }
                } else if (isVowel(s.charAt(i), i + 1)) {
                    break;
                }
            }
            for (i++; i < s.length(); i++) {
                if (i > 0) {
                    if (isCons(s.charAt(i), i - 1)) {
                        break;
                    }
                } else if (isCons(s.charAt(i), i)) {
                    break;
                }
            }
            if (i < s.length()) {
                n++;
                i++;
            }
        }
        return n;
    }

    /**
     * isCons
     *
     * @param ch
     * @param i
     * @return
     */
    protected boolean isCons(char ch, int i) {
        switch (ch) {
            case 'a':
                return false;
            case 'e':
                return false;
            case 'u':
                return false;
            case 'o':
                return false;
            case 'i':
                return false;
            case 'y':
                if (i == 0) {
                    return false;
                } else if (i < this.s.length()-1) {
                    return !isCons(this.s.charAt(i+1), i+1);
                } else {
                    return false;
                }
            default:
                return true;
        }
    }

    /**
     *
     * @return
     */
    protected boolean doubleCons() {
        if (s.length() > 3) {
            char last = s.charAt(s.length() - 1);
            char prev = s.charAt(s.length() - 2);
            if (last == prev) {
                if (isCons(s.charAt(s.length() - 1), s.length())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    protected boolean cvcForm() {
        if (s.length() > 2) {
            if (s.substring(s.length() - 3).contains("wxy")) {
                return false;
            } else if (isCons(s.charAt(s.length() - 1), 1) && isCons(s.charAt(s.length() - 2), 3) && isCons(s.charAt(s.length()
                    - 2), 3)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isVowel(char ch, int i) {
        return !isCons(ch, i);
    }

    /**
     *
     * @return checks if vowel in stem
     */
    protected boolean hasVowelInStem() {
        for (char ch : s.toCharArray()) {
            for (int i = 0; i < s.length(); i++) {
                if (isVowel(ch, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param s input string
     * @return buffer ends with s
     */
    protected final boolean endsWith(String s) {
        return this.s.endsWith(s);
    }

    /**
     * Set buffer to s
     *
     * @param s
     */
    protected void setTo(String s) {
        this.s += s;
    }

    protected void delNChars(int i) {
        if (i < s.length() - 1) {
            this.s = this.s.substring(0, this.s.length() - i);
        }
    }

    /**
     *
     * SSES -> SS caresses -> caress
     * IES -> I ponies -> poni
     * ties -> ti
     * SS -> SS caress -> caress
     * S -> cats -> cat
     *
     * (m>0) EED -> EE feed -> feed
     * agreed -> agree
     * (*v*) ED -> plastered -> plaster
     * bled -> bled
     * (*v*) ING -> motoring -> motor
     * sing -> sing *
     * AT -> ATE conflat(ed) -> conflate
     * BL -> BLE troubl(ed) -> trouble
     * IZ -> IZE siz(ed) -> size
     * (*d and not (*L or *S or *Z))
     * -> single letter
     * hopp(ing) -> hop
     * tann(ed) -> tan
     * fall(ing) -> fall
     * hiss(ing) -> hiss
     * fizz(ed) -> fizz
     * (m=1 and *o) -> E fail(ing) -> fail
     * fil(ing) -> file
     * (*v*) Y -> I happy -> happi
     * sky -> sky
     */
    protected void step1() {
        boolean b1 = false;
        if (endsWith("sses")) {
            delNChars(2);
        } else if (endsWith("ies")) {
            delNChars(2);
        } else if (endsWith("ss")) {
            delNChars(0);
        } else if (endsWith("s")) {
            delNChars(1);
        } //1b
        else if (getM() > 0 && endsWith("eed")) {
            delNChars(1);
        } else if (hasVowelInStem() && endsWith("ed")) {
            delNChars(2);
            b1 = true;
        } else if (hasVowelInStem() && endsWith("ing")) {
            delNChars(3);
            b1 = true;
        } else if (b1) {
            if (endsWith("at")) {
                setTo("e ");
            } else if (endsWith("bl")) {
                setTo("e ");
            } else if (endsWith("iz")) {
                setTo("e ");
            } else if (doubleCons() && (!(endsWith("l") || endsWith("s") || endsWith("z")))) {
                delNChars(1);
            } else if (getM() == 1 && cvcForm()) {
                setTo("e ");
            }
        } //1c
        else if (hasVowelInStem() && endsWith("y")) {
            delNChars(1);
            setTo("i ");
        }
    }

    /**
     * (m>0) ATIONAL -> ATE relational -> relate
     * (m>0) TIONAL -> TION conditional -> condition
     * rational -> rational
     * (m>0) ENCI -> ENCE valenci -> valence
     * (m>0) ANCI -> ANCE hesitanci -> hesitance
     * (m>0) IZER -> IZE digitizer -> digitize
     * (m>0) ABLI -> ABLE conformabli -> conformable
     * (m>0) ALLI -> AL radicalli -> radical
     * (m>0) ENTLI -> ENT differentli -> different
     * (m>0) ELI -> E vileli - > vile
     * (m>0) OUSLI -> OUS analogousli -> analogous
     * (m>0) IZATION -> IZE vietnamization -> vietnamize
     * (m>0) ATION -> ATE predication -> predicate
     * (m>0) ATOR -> ATE operator -> operate
     * (m>0) ALISM -> AL feudalism -> feudal
     * (m>0) IVENESS -> IVE decisiveness -> decisive
     * (m>0) FULNESS -> FUL hopefulness -> hopeful
     * (m>0) OUSNESS -> OUS callousness -> callous
     * (m>0) ALITI -> AL formaliti -> formal
     * (m>0) IVITI -> IVE sensitiviti -> sensitive
     * (m>0) BILITI -> BLE sensibiliti -> sensible
     */
    protected void step2() {
        if (getM() > 0) {
            if (endsWith("ational")) {
                delNChars(5);
                setTo("e ");
            } else if (endsWith("tional")) {
                delNChars(2);
            } else if (endsWith("enci")) {
                delNChars(1);
                setTo("e ");
            } else if (endsWith("anci")) {
                delNChars(1);
                setTo("e ");
            } else if (endsWith("izer")) {
                delNChars(1);
            } else if (endsWith("abli")) {
                delNChars(1);
                setTo("e ");
            } else if (endsWith("alli")) {
                delNChars(4);
                setTo("el ");
            } else if (endsWith("entli")) {
                delNChars(2);
                setTo("ent ");
            } else if (endsWith("eli")) {
                delNChars(2);
                setTo("e ");
            } else if (endsWith("ousli")) {
                delNChars(2);
            } else if (endsWith("ization")) {
                delNChars(5);
                setTo("e ");
            } else if (endsWith("ation")) {
                delNChars(3);
                setTo("e ");
            } else if (endsWith("ator")) {
                delNChars(2);
                setTo("e ");
            } else if (endsWith("alism")) {
                delNChars(3);
            } else if (endsWith("iveness")) {
                delNChars(4);
            } else if (endsWith("fulness")) {
                delNChars(4);
            } else if (endsWith("ousness")) {
                delNChars(4);
            } else if (endsWith("aliti")) {
                delNChars(4);
            } else if (endsWith("iviti")) {
                delNChars(3);
                setTo("e ");
            } else if (endsWith("biliti")) {
                delNChars(4);
                setTo("e ");
            }

        }

    }

    /**
     * (m>0) ICATE -> IC triplicate -> triplic
     * (m>0) ATIVE -> formative -> form
     * (m>0) ALIZE -> AL formalize -> formal
     * (m>0) ICITI -> IC electriciti -> electric
     * (m>0) ICAL -> IC electrical -> electric
     * (m>0) FUL -> hopeful -> hope
     * (m>0) NESS -> goodness -> good
     */
    protected void step3() {
        if (getM() > 0) {
            if (endsWith("icate")) {
                delNChars(3);
            } else if (endsWith("ative")) {
                delNChars(5);
            } else if (endsWith("alize")) {
                delNChars(3);
            } else if (endsWith("iciti")) {
                delNChars(3);
            } else if (endsWith("ical")) {
                delNChars(2);
            } else if (endsWith("ful")) {
                delNChars(3);
            } else if (endsWith("ness")) {
                delNChars(4);
            }

        }
    }

    /**
     * (m>1) AL -> revival -> reviv
     * (m>1) ANCE -> allowance -> allow
     * (m>1) ENCE -> inference -> infer
     * (m>1) ER -> airliner -> airlin
     * (m>1) IC -> gyroscopic -> gyroscop
     * (m>1) ABLE -> adjustable -> adjust
     * (m>1) IBLE -> defensible -> defens
     * (m>1) ANT -> irritant -> irrit
     * (m>1) EMENT -> replacement -> replac
     * (m>1) MENT -> adjustment -> adjust
     * (m>1) ENT -> dependent -> depend
     * (m>1 and (*S or *T)) ION -> adoption -> adopt
     * (m>1) OU -> homologou -> homolog
     * (m>1) ISM -> communism -> commun
     * (m>1) ATE -> activate -> activ
     * (m>1) ITI -> angulariti -> angular
     * (m>1) OUS -> homologous -> homolog
     * (m>1) IVE -> effective -> effect
     * (m>1) IZE -> bowdlerize -> bowdler
     */
    protected void step4() {
        if (getM() > 1) {
            if (endsWith("al")) {
                delNChars(2);
            } else if (endsWith("ance")) {
                delNChars(4);
            } else if (endsWith("ence")) {
                delNChars(4);
            } else if (endsWith("er")) {
                delNChars(2);
            } else if (endsWith("ic")) {
                delNChars(2);
            } else if (endsWith("able")) {
                delNChars(4);
            } else if (endsWith("ible")) {
                delNChars(4);
            } else if (endsWith("ant")) {
                delNChars(3);
            } else if (endsWith("ement")) {
                delNChars(5);
            } else if (endsWith("ment")) {
                delNChars(4);
            } else if (endsWith("ent")) {
                delNChars(3);
            } else if (endsWith("sion")) {
                delNChars(4);
            } else if (endsWith("tion")) {
                delNChars(4);
            } else if (endsWith("ou")) {
                delNChars(2);
            } else if (endsWith("ism")) {
                delNChars(3);
            } else if (endsWith("ate")) {
                delNChars(3);
            } else if (endsWith("iti")) {
                delNChars(3);
            } else if (endsWith("ous")) {
                delNChars(3);
            } else if (endsWith("ive")) {
                delNChars(3);
            } else if (endsWith("ize")) {
                delNChars(3);
            }
        }
    }

    /**
     *
     * (m>1) E -> probate -> probat
     * rate -> rate
     * (m=1 and not *o) E -> cease -> ceas
     *
     * (m > 1 and *d and *L) -> single letter
     * controll -> control
     * roll -> roll
     *
     */
    protected void step5() {
        if (getM() > 1) {
            if (doubleCons()) {
                if (endsWith("l")) {
                    delNChars(1);
                }
            } else if (endsWith("e")) {
                delNChars(1);
            }
        } else if (getM() == 1 && !(cvcForm())) {
            if (endsWith("e")) {
                delNChars(1);
            }
        }
    }

}
