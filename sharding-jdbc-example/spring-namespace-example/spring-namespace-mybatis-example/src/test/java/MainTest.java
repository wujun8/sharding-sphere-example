import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.junit.Test;

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
}
