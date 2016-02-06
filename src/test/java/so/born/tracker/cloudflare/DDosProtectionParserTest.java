package so.born.tracker.cloudflare;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class DDosProtectionParserTest {

    @Test
    public void testParse503() throws Exception {
        InputStream is = DDosProtectionParser.class.getResourceAsStream("/horrible_503.html");
        String url = "http://horriblesubs.info/lib/latest.php";
        Document doc = Jsoup.parse(is, "utf8", url);
        String result = new DDosProtectionParser(new ScriptEngineManager(), false).parse(doc, url);
        assertEquals("http://horriblesubs.info/cdn-cgi/l/chk_jschl?jschl_answer=-2778&jschl_vc=687e0b49d3d1f090922b20fe77de8d2d&pass=1454759094.875-n3Zb1xlAeX",
                    result);
    }
}
