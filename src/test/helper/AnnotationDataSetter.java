package test.helper;

import javax.lang.model.type.TypeMirror;

import test.model.AnnotationInfo;
import test.model.JavaClassInfo;

import com.sun.source.tree.AnnotationTree;

/**
 * Helper class to set the properties of an Annotation
 * to the java class model
 * 
 * @author Seema Richard (Seema.Richard@ust-global.com)
 * @author Deepa Sobhana (Deepa.Sobhana@ust-global.com)
 */
public class AnnotationDataSetter {

    /**
     * Set annotation attributes to the java class model
     * @param clazzInfo The java class model
     * @param annotationTree Curently visiting annotation tree
     * @param mirror type mirror
     */
    public static void populateAnnotationInfo(JavaClassInfo clazzInfo, 
                                          AnnotationTree annotationTree, 
                                          TypeMirror mirror) {
        String qualifiedName = mirror.toString();
        AnnotationInfo annotationInfo = new AnnotationInfo();
        //set the full qualified name of annotation
        annotationInfo.setName(qualifiedName);
        clazzInfo.addAnnotation(annotationInfo);
    }
}
