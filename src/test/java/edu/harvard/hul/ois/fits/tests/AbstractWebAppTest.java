package edu.harvard.hul.ois.fits.tests;

import static edu.harvard.hul.ois.fits.FitsPaths.INPUT_DIR;

import edu.harvard.hul.ois.fits.FitsOutput;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractWebAppTest extends AbstractXmlUnitTest {

    // To be used when accessing an external FITS Servlet deployment.
    private static CloseableHttpClient httpclient;
    // Location of external FITS Servlet instance.
    private static final String servicePostURL = "http://localhost:8080/fits/examine?includeStandardOutput=false";

    private static Logger logger = null;

    @BeforeClass
    public static void initializeHttpClient() {
        logger = LoggerFactory.getLogger(AbstractXmlUnitTest.class);
        httpclient = HttpClients.createDefault();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (httpclient != null) {
            httpclient.close();
        }
    }

    /**
     * Same as testFile() except it examines the file in an external FITS web application.
     *
     * @param inputFilename the name of the file to examine relative testfiles/input
     * @throws Exception
     */
    protected void testFileInWebApp(String inputFilename) throws Exception {
        File input = new File(INPUT_DIR + inputFilename);
        FitsOutput fitsOut = examineInWebApp(input);
        writeAndValidate(fitsOut, inputFilename, OutputType.COMBINED);
    }

    /**
     * Same as testFile() except it examines the file in an external FITS web application.
     *
     * @param inputFilename the name of the file to examine relative testfiles/input
     * @throws Exception
     */
    protected void testFileInWebApp(String inputFilename, OutputType outputType) throws Exception {
        File input = new File(INPUT_DIR + inputFilename);
        FitsOutput fitsOut = examineInWebApp(input);
        writeAndValidate(fitsOut, inputFilename, outputType);
    }

    /**
     * To be called from a method in a class that is testing an external FITS web application.
     */
    protected FitsOutput examineInWebApp(File inputFile) throws Exception {

        HttpPost httpPost = new HttpPost(servicePostURL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("datafile", inputFile, ContentType.APPLICATION_OCTET_STREAM, inputFile.getName());
        HttpEntity reqEntity = builder.build();
        httpPost.setEntity(reqEntity);

        logger.info("executing request " + httpPost.getRequestLine());
        CloseableHttpResponse response = httpclient.execute(httpPost);

        FitsOutput fitsOutput = null;
        try {
            logger.info("HTTP Response Status Line: " + response.getStatusLine());
            // Expecting a 200 Status Code
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String reason = response.getStatusLine().getReasonPhrase();
                logger.warn("Unexpected HTTP response status code:["
                        + response.getStatusLine().getStatusCode() + "] -- Reason (if available): " + reason);
            } else {
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();
                BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                String output;
                StringBuilder sb = new StringBuilder();
                while ((output = in.readLine()) != null) {
                    sb.append(output);
                    sb.append(System.getProperty("line.separator"));
                }
                logger.info(sb.toString());
                in.close();
                EntityUtils.consume(resEntity);
                fitsOutput = new FitsOutput(sb.toString());
            }
        } finally {
            response.close();
        }
        return fitsOutput;
    }
}
