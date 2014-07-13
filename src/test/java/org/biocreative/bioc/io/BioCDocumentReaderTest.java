package org.biocreative.bioc.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.stream.XMLStreamException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.biocreative.bioc.BioCCollection;
import org.biocreative.bioc.BioCDocument;
import org.biocreative.bioc.BioCPassage;
import org.biocreative.bioc.io.BioCFactory;
import org.biocreative.bioc.io.standard.JdkStrategy;

public class BioCDocumentReaderTest {

  private static final String XML_FILENAME = "xml/PMID-8557975-simplified-sentences.xml";
  private static final JdkStrategy STRATEGY = new JdkStrategy();
  private static final String DTD = "<!DOCTYPE collection SYSTEM \"BioC.dtd\" []>";

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test_success()
      throws Exception {
    BioCDocumentReader reader = BioCFactory.newFactory(STRATEGY)
        .createBioCDocumentReader(
            new InputStreamReader(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(XML_FILENAME)));
    BioCCollection collection = reader.readCollectionInfo();
    assertEquals(collection.getDocmentCount(), 0);
    

    assertEquals(collection.getSource(), "PubMed");
    assertEquals(collection.getKey(), "PMID-8557975-simplified-sentences.key");

    BioCCollection.Builder collectionBuilder = collection.getBuilder();

    BioCDocument doc = null;
    while ((doc = reader.readDocument()) != null) {
      System.out.println(doc);
      collectionBuilder.addDocument(doc);
    }
    collection = collectionBuilder.build();

    assertEquals(collection.getDocmentCount(), 1);

    doc = collection.getDocument(0);
    assertEquals(doc.getPassageCount(), 1);

    BioCPassage pass = doc.getPassage(0);
    assertEquals(pass.getSentenceCount(), 7);

    reader.close();
  }

  @Test
  public void test_emptyReader()
      throws Exception {
    thrown.expect(XMLStreamException.class);
    BioCDocumentReader reader = BioCFactory.newFactory(STRATEGY)
        .createBioCDocumentReader(new StringReader(""));
    reader.readCollectionInfo();
  }

  @Test
  public void test_readTwice()
      throws Exception {
    BioCDocumentReader reader = BioCFactory.newFactory(STRATEGY)
        .createBioCDocumentReader(
            new InputStreamReader(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(XML_FILENAME)));
    BioCCollection collection = reader.readCollectionInfo();
    assertEquals(collection.getDocmentCount(), 0);
    
    BioCDocument doc = null;
    while ((doc = reader.readDocument()) != null) {
      System.out.println(doc);
    }
    
    // twice
    collection = reader.readCollectionInfo();
    assertEquals(collection.getDocmentCount(), 0);
    assertNull(reader.readDocument());
  }

  @Test
  public void test_dtd()
      throws Exception {
    BioCDocumentReader reader = BioCFactory.newFactory(STRATEGY)
        .createBioCDocumentReader(
            new InputStreamReader(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(XML_FILENAME)));
    assertEquals(reader.getDTD(), DTD);
    reader.close();
  }
}