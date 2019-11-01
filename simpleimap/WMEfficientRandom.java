/*
 * WMEfficientRandom.java
 *
 * Created on February 24, 2003, 4:02 PM
 */

package simpleimap;

import java.util.*;

/**
 *
 * @author  David Jeske
 */
public class WMEfficientRandom extends java.security.SecureRandomSpi {
    // org.logi.crypto.random.PureSpinner spinner;
    /** Creates a new instance of WMEfficientRandom */
    java.util.Random gen;
    public WMEfficientRandom() {
        // spinner = new org.logi.crypto.random.PureSpinner();
        Date now = new Date();
        int seed = (int) now.getTime();
        gen = new java.util.Random(seed);
        // Debug.debug("generated seed: " + seed);
    }
    
    /** Returns the given number of seed bytes.  This call may be used to
     * seed other random number generators.
     *
     * @param numBytes the number of seed bytes to generate.
     *
     * @return the seed bytes.
     *
     */
    public byte[] engineGenerateSeed(int numBytes) {
        byte[] retVal = new byte[numBytes];
        // spinner.nextBytes(retVal);
        gen.nextBytes(retVal);
        // Debug.debug("engineNextBytes: " + numBytes);
        return retVal;
    }

    /** Generates a user-specified number of random bytes.
     *
     * @param bytes the array to be filled in with random bytes.
     *
     */
    public void engineNextBytes(byte[] bytes) {
        // spinner.nextBytes(bytes);
        gen.nextBytes(bytes);
        // Debug.debug("engineNextBytes: " + bytes.length);
    }
    
    /** Reseeds this random object. The given seed supplements, rather than
     * replaces, the existing seed. Thus, repeated calls are guaranteed
     * never to reduce randomness.
     *
     * @param seed the seed.
     *
     */
    public void engineSetSeed(byte[] seed) {
        
    }
    
}
