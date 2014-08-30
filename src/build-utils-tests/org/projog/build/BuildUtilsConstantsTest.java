package org.projog.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

public class BuildUtilsConstantsTest {
   @Test
   public void testLineEndings() {
      assertEquals("", BuildUtilsConstants.toUnixLineEndings(""));
      assertEquals("\nabc\nq\rw\ne\t\n", BuildUtilsConstants.toUnixLineEndings("\r\nabc\nq\rw\r\ne\t\r\n"));
   }

   @Test
   public void testIsPrologScript() {
      assertTrue(BuildUtilsConstants.isPrologScript(new File("test.pl")));
      assertFalse(BuildUtilsConstants.isPrologScript(new File("test.java")));
      assertFalse(BuildUtilsConstants.isPrologScript(new File("test.pl.tmp")));
   }

   @Test
   public void testConcatLines() {
      String actual = BuildUtilsConstants.concatLines(Arrays.asList("Lorem ipsum", "", " dolor ", "sit amet,", "\tconsectetur adipiscing elit"));
      assertEquals("Lorem ipsum\n\n dolor \nsit amet,\n\tconsectetur adipiscing elit\n", actual);
   }
}