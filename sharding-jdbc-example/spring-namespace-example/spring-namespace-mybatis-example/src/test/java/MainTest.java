import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

public class MainTest {

    @Test
    public void testHash() {
        HashFunction hf = Hashing.md5();
        for (int i = 0; i < 100; i++) {
            Hasher hasher = hf.newHasher();
            hasher.putInt(i);
            HashCode hc = hasher.hash();
            System.out.println(hc.asInt());
        }

    }

    @Test
    public void testNormal() {
        NormalDistribution normalDistribution = new NormalDistribution(0, 1);
        /*int [] hashes = new int[20];
        for (int i = 0; i < hashes.length; i++) {
            double hash = normalDistribution.sample();
            System.out.println(hash);
//            hashes[i] = hash;
        }*/
        double [] hashes = normalDistribution.sample(6000);
        System.out.println("-----------------------");
        Map<Integer, Integer> hashCount = Maps.newTreeMap();
        for (double h : hashes) {
            if (h > 0) {
                continue;
            }
//            System.out.println(h);
            int hashKey = (int) (Math.abs(h + 3) / 0.3);
            if (hashCount.containsKey(hashKey)) {
                hashCount.put(hashKey, hashCount.get(hashKey) + 1);
            } else {
                hashCount.put(hashKey, 1);
            }
        }
        for (Integer h : hashCount.keySet()) {
            int count = hashCount.get(h);
            System.out.println(String.format("%d, %d\t%s", h, count, repeat("-", count)));
        }
    }

    public static String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }
}
