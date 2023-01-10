/*
 * Copyright 2016 Harvard University Library
 *
 * This file is part of FITS (File Information Tool Set).
 *
 * FITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FITS.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hul.ois.fits.junit;

import edu.harvard.hul.ois.fits.tests.AbstractXmlUnitTest;
import org.junit.Test;

/**
 * These tests compare actual FITS output with expected output on various word processing documents.
 * These tests should be run with &lt;display-tool-output&gt;false&lt;/display-tool-output&gt; in fits.xml.
 *
 * @author dan179
 */
public class DocMDXmlUnitTest extends AbstractXmlUnitTest {

    @Test
    public void testWordDocUrlEmbeddedResources() throws Exception {
        testFile("Word2003_has_URLs_has_embedded_resources.doc");
    }

    @Test
    public void testWordDocGraphics() throws Exception {
        testFile("Word2003_many_graphics.doc");
    }

    @Test
    public void testWordDocPasswordProtected() throws Exception {
        testFile("Word2003PasswordProtected.doc");
    }

    @Test
    public void testWordDoc2011() throws Exception {
        testFile("Word2011_Has_Outline.doc");
    }

    @Test
    public void testWordDocLibreOffice() throws Exception {
        testFile("LibreOffice.doc");
    }

    @Test
    public void testWordDocHyperlinks() throws Exception {
        testFile("Word2003_has_table_of_contents.doc");
    }

    @Test
    public void testWordDocPasswordAndEncrypted() throws Exception {
        testFile("Word_protected_encrypted.doc");
    }

    @Test
    public void testWordDocMacMSWord() throws Exception {
        testFile("MacMSWORD_4-5.doc");
    }

    @Test
    public void testWordDocV2() throws Exception {
        testFile("NEWSSLID_Word2_0.DOC");
    }

    @Test
    public void testOpenOfficeDoc() throws Exception {
        testFile("LibreODT-Ur-doc.odt");
    }

    @Test
    public void testOpenOfficeDocEmbeddedResources() throws Exception {
        testFile("LibreODTwFormulas.odt");
    }

    @Test
    public void testOpenOfficeDocHasTables() throws Exception {
        testFile("LibreODT-hasTables.odt");
    }

    @Test
    public void testOpenOfficeDocUnparseableDate() throws Exception {
        testFile("UnparseableDate.odt");
    }

    @Test
    public void testWordDocxOutput() throws Exception {
        testFile("Word_has_index.docx");
    }

    @Test
    public void testXlsxOutput() throws Exception {
        testFile("DH43D5TQESXBZ8W.xlsx");
    }

    @Test
    public void testXlsOutput() throws Exception {
        testFile("valid.xls");
    }

    @Test
    public void testPptxOutput() throws Exception {
        testFile("samplepptx.pptx");
    }

    @Test
    public void testPptOutput() throws Exception {
        testFile("ConleyPPLec.ppt");
    }

    /*
     * This output of this document produces what looks to be invalid output.
     */
    @Test
    public void testWordDocxPasswordProtected() throws Exception {
        testFile("WordPasswordProtected.docx");
    }

    @Test
    public void testWordDocmOutput() throws Exception {
        testFile("Document_Has_Form_Controls.docm");
    }

    @Test
    public void testEpubOutput() throws Exception {

        // process multiple files to examine different types of output
        String[] inputFilenames = {
            "Winnie-the-Pooh-protected.epub", // not properly identified as epub mimetype
            "GeographyofBliss_oneChapter.epub",
            "aliceDynamic_images_metadata_tableOfContents.epub", // not properly identified as epub mimetype
            "epub30-test-font-embedding-obfuscation.epub",
            "Calibre_hasTable_of_Contents.epub"
        };

        for (String inputFilename : inputFilenames) {
            testFile(inputFilename);
        }
    }

    @Test
    public void testWPDOutput() throws Exception {

        // process multiple files to examine different types of output
        String[] inputFilenames = {"WordPerfect6-7.wpd", "WordPerfect4_2.wp", "WordPerfect5_0.wp", "WordPerfect5_2.wp"};
        // "WordPerfectCompoundFile.wpd"}; // (not identified as a WordPerfect document)

        for (String inputFilename : inputFilenames) {
            testFile(inputFilename);
        }
    }

    @Test
    public void testRtfOutput() throws Exception {
        testFile("TestDoc.rtf");
    }

    @Test
    public void testRtfWithCompanyOutput() throws Exception {
        testFile("Doc2.rtf");
    }

    @Test
    public void testPdf() throws Exception {
        String[] inputFilenames = {
            "PDF_embedded_resources.pdf", "HasChangeHistory.pdf", "PDF_eng.pdf", "HasAnnotations.pdf"
        };

        for (String inputFilename : inputFilenames) {
            testFile(inputFilename);
        }
    }

    @Test
    public void testPdfA() throws Exception {
        String[] inputFilenames = {
            "PDFa_equations.pdf",
            "PDFa_multiplefonts.pdf",
            "PDFa_has_form.pdf",
            "PDFa_has_table_of_contents.pdf",
            "PDFa_has_tables.pdf",
            "PDFA_Document with tables.pdf",
            "PDFa_embedded_resources.pdf"
        };

        for (String inputFilename : inputFilenames) {
            testFile(inputFilename);
        }
    }

    @Test
    public void testPdfX() throws Exception {
        String[] inputFilenames = {"altona_technical_1v2_x3_has_annotations.pdf", "Book_pdfx1a.pdf", "PDFx3.pdf"};

        for (String inputFilename : inputFilenames) {
            testFile(inputFilename);
        }
    }

    @Test
    public void includeAllToolOutputWhenConsolidationDisabled() throws Exception {
        testFile("PDFa_has_table_of_contents.pdf");
    }
}
