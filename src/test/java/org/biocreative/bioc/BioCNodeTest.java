package org.biocreative.bioc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.testing.EqualsTester;

public class BioCNodeTest {

  private static final String REFID = "1";
  private static final String ROLE = "role1";

  private static final String REFID_2 = "2";
  private static final String ROLE_2 = "role2";

  private BioCNode.Builder baseBuilder;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() {
    baseBuilder = BioCNode.newBuilder()
        .setRefid(REFID)
        .setRole(ROLE);
  }

  @Test
  public void test_equals() {

    BioCNode base = baseBuilder.build();
    BioCNode baseCopy = baseBuilder.build();

    BioCNode diffRefid = baseBuilder.setRefid(REFID_2).build();
    BioCNode diffRole = baseBuilder.setRole(ROLE_2).build();

    new EqualsTester()
        .addEqualityGroup(base, baseCopy)
        .addEqualityGroup(diffRefid)
        .addEqualityGroup(diffRole)
        .testEquals();
  }

  @Test
  public void test_allFields() {
    BioCNode base = baseBuilder.build();

    assertEquals(base.getRefid(), REFID);
    assertEquals(base.getRole(), ROLE);
  }

  @Test
  public void test_empty() {
    thrown.expect(IllegalArgumentException.class);
    BioCNode.newBuilder().build();
  }
  
  @Test
  public void test_nullRefid() {
    thrown.expect(NullPointerException.class);
    BioCNode.newBuilder().setRefid(null);
  }
  
  @Test
  public void test_nullRole() {
    thrown.expect(NullPointerException.class);
    BioCNode.newBuilder().setRole(null);
  }
}
