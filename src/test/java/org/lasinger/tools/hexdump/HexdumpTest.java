package org.lasinger.tools.hexdump;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;
import static org.assertj.core.api.Assertions.assertThat;
import static org.lasinger.tools.hexdump.Hexdump.hexdump;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class HexdumpTest {

    @Test
    public void utilityClass() throws Exception {
        assertUtilityClassWellDefined(Hexdump.class);
    }

    @Test
    public void example_gitignore() throws Exception {
        String in = "/.classpath\n" + //
                "/.project\n" + //
                "/.settings/\n" + //
                "/target/\n" + //
                "/nbproject/\n" + //
                "/nb-configuration.xml";
        System.out.println(hexdump(in.getBytes(UTF_8)));
    }

    @Test
    public void nullBytes() throws Exception {
        assertThat(hexdump(null)).isEqualTo("null");
    }

    @Test
    public void noBytes() throws Exception {
        assertThat(hexdump(new byte[0])).isEqualTo("empty");
    }

    @Test
    public void hexEncoding() throws Exception {
        String in = "1fe20bc4";
        int i = Integer.parseInt(in, 16);
        byte[] bytes = new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i };

        String dump = hexdump(bytes);

        System.out.println(dump);
        assertThat(dump.replaceAll(" ", "")).contains(in);
    }

    @Test
    public void eightBytes() throws Exception {
        String dump = hexdump(new byte[] { 48, 49, 50, 51, 52, 53, 54, 55 });

        System.out.println(dump);
        assertThat(dump).startsWith("00000000  30 31 32 33 34 35 36 37                           |01234567|");
    }

    @Test
    public void nineBytes() throws Exception {
        String dump = hexdump(new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56 });

        System.out.println(dump);
        assertThat(dump).startsWith("00000000  30 31 32 33 34 35 36 37  38                       |012345678|");
    }

    @Test
    public void specialBytes() throws Exception {
        String dump = hexdump(new byte[] { 0x09, 0x20, 0x20, 0x0D, 0x0A, 0x2E, 0x00 });

        System.out.println(dump);
        assertThat(dump).startsWith("00000000  09 20 20 0d 0a 2e 00                              |→␣␣¤¶.·|");
    }

    @Test
    public void edgeBytes() throws Exception {
        String dump = hexdump(new byte[] { 0x00, Byte.MIN_VALUE, Byte.MAX_VALUE, (byte) 255 });

        System.out.println(dump);
        assertThat(dump).startsWith("00000000  00 80 7f ff                                       |····|");
    }

    @Test
    public void singleLine() throws Exception {
        String dump = hexdump("1234".getBytes(UTF_8));
        assertThat(dump).isEqualTo("00000000  31 32 33 34                                       |1234|\n" //
                + "00000004\n");
    }

    @Test
    public void singleLineFull() throws Exception {
        String dump = hexdump("1234567812345678".getBytes(UTF_8));
        assertThat(dump).isEqualTo("00000000  31 32 33 34 35 36 37 38  31 32 33 34 35 36 37 38  |1234567812345678|\n" //
                + "00000010\n");
    }

    @Test
    public void multiLine() throws Exception {
        String dump = hexdump("1234567812345678abc".getBytes(UTF_8));
        assertThat(dump).isEqualTo("00000000  31 32 33 34 35 36 37 38  31 32 33 34 35 36 37 38  |1234567812345678|\n"
                + "00000010  61 62 63                                          |abc|\n" //
                + "00000013\n");
    }

    @Test
    public void printableChars() throws Exception {
        String nonAscii = "äöüß";

        String in = " !\"#$%&'()*+,-./0123456789:;<=>?@ABC-" + nonAscii + "-XYZ[\\]^_`abc-xyz{|}~";

        String dump = hexdump(in.getBytes(UTF_8));

        System.out.println(dump);
        String printableAscii = in.replace(nonAscii, "");
        assertThat(codepointList(dump)).containsAll(codepointList(printableAscii));
        assertThat(codepointList(dump)).doesNotContainAnyElementsOf(codepointList(nonAscii));
        assertThat(dump).contains("ABC-········-X");
    }

    // this test currently has no assertion. it is for visual output only.
    @Test
    public void fullByteRange() throws Exception {
        byte[] allBytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            allBytes[i] = (byte) i;
        }
        System.out.println(hexdump(allBytes));
    }

    private List<Integer> codepointList(String string) {
        return string.codePoints().collect(ArrayList::new, List::add, List::addAll);
    }

}
