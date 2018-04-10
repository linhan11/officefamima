package jp.co.saison.tvc.offcefamima;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import jp.co.saison.tvc.officefamima.OfficeFamimaStreamHandler;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class OfficeFamimaStreamHandlerTest {

    private static Object input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testOffceFamimaStreamHandler() {
        OfficeFamimaStreamHandler handler = new OfficeFamimaStreamHandler();
        Context ctx = createContext();

        //TODO:ここをテストできるよう書き換える必要がある
        //String output = handler.handleRequest(input, ctx);
        String output = "hoge";

        // TODO: validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
