package com.pengyifan.bioc.util;

import com.pengyifan.bioc.BioCAnnotation;
import com.pengyifan.bioc.BioCCollection;
import com.pengyifan.bioc.BioCDocument;
import com.pengyifan.bioc.BioCLocation;
import com.pengyifan.bioc.BioCNode;
import com.pengyifan.bioc.BioCPassage;
import com.pengyifan.bioc.BioCRelation;
import com.pengyifan.bioc.BioCSentence;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public class BioCValidate {

  /**
   * Checks the annotations and relations.
   *
   * @param sentence input sentence
   */
  public static void check(BioCSentence sentence) {
    // check annotation offset and text
    checkAnnotations(sentence.getAnnotations(), sentence.getText().get(), sentence.getOffset());
    // check relation
    for (BioCRelation relation : sentence.getRelations()) {
      for (BioCNode node : relation.getNodes()) {
        checkArgument(sentence.getAnnotation(node.getRefid()).isPresent(),
            "Cannot find node %s in relation %s", node, relation);
      }
    }
  }

  /**
   * If the passage has text, checks annotations and relations; otherwise directly returns.
   *
   * @param passage input passage
   */
  public static void check(BioCPassage passage) {
    for (BioCSentence sentence : passage.getSentences()) {
      check(sentence);
    }

    String text = getText(passage);

    // check annotation offset and text
    checkAnnotations(passage.getAnnotations(), text, passage.getOffset());
    // check relation
    for (BioCRelation relation : passage.getRelations()) {
      for (BioCNode node : relation.getNodes()) {
        checkArgument(passage.getAnnotation(node.getRefid()).isPresent(),
            "Cannot find node %s in relation %s", node, relation);
      }
    }
  }

  /**
   * Checks annotations and relations.
   *
   * @param document input document
   */
  public static void check(BioCDocument document) {
    for(BioCPassage passage: document.getPassages()) {
      check(passage);
    }

    String text = getText(document);
    // check annotation offset and text
    checkAnnotations(document.getAnnotations(), text, 0);
    // check relation
    for (BioCRelation relation : document.getRelations()) {
      for (BioCNode node : relation.getNodes()) {
        checkArgument(document.getAnnotation(node.getRefid()).isPresent(),
            "Cannot find node %s in relation %s", node, relation);
      }
    }
  }

  /**
   * Checks annotations and relations.
   *
   * @param collection input collection
   */
  public static void check(BioCCollection collection) {
    BioCPassageIterator iterator = new BioCPassageIterator(collection);
    while (iterator.hasNext()) {
      BioCValidate.check(iterator.next());
    }
  }

  public static String getText(BioCDocument document) {
    StringBuilder sb = new StringBuilder();
    for (BioCPassage passage : document.getPassages()) {
      fillText(sb, passage.getOffset());
      sb.append(getText(passage));
    }
    return sb.toString();
  }

  public static String getText(BioCPassage passage) {
    StringBuilder sb = new StringBuilder();
    if (passage.getText().isPresent() && !passage.getText().get().isEmpty()) {
      sb.append(passage.getText().get());
    } else {
      for (BioCSentence sentence : passage.getSentences()) {
        fillText(sb, sentence.getOffset() - passage.getOffset());
        checkArgument(sentence.getText().isPresent(), "BioC sentence has no text");
        sb.append(sentence.getText().get());
      }
    }
    return sb.toString();
  }

  public static void checkAnnotations(Collection<BioCAnnotation> annotations,
      String text, int offset) {
    for (BioCAnnotation annotation : annotations) {
      for (BioCLocation location : annotation.getLocations()) {
        String substring = text.substring(
            location.getOffset() - offset,
            location.getOffset() + location.getLength() - offset
        );
        checkArgument(substring.equals(annotation.getText().get()),
            "Annotation text is incorrect.\n" +
                "Annotation:  %s\n" +
                "Actual text: %s", annotation, substring);
      }
    }
  }

  private static StringBuilder fillText(StringBuilder sb, int offset) {
    while (sb.length() < offset) {
      sb.append('\n');
    }
    return sb;
  }
}
